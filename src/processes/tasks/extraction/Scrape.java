package processes.tasks.extraction;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import main.Application;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import beans.Annotation;
import beans.Extractor;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import processes.TaskExecutor;
import processes.TaskScheduler;
import processes.tasks.Task;


public class Scrape implements Task {
	private static Logger logger;
	private static int RETRIES = 3;
	private static BeanListHandler<Annotation> annotationListHandler = new BeanListHandler<Annotation>(Annotation.class);
	private static ResultSetHandler<String[]> arrayRSHandler = 	new ResultSetHandler<String[]> (){
		public String[] handle(ResultSet rs)throws SQLException {
			rs.last();
			String[] urls = new String[rs.getRow()];
			rs.first();
			int i = 0;
			do {
				urls[i] = rs.getString(1);
				i++;
			} while(rs.next());
			return urls;
		}
	};
	private static HashMap<String, String> normalisableTags;
	private static List<String> tableTags;

	static {
		normalisableTags  = new HashMap<String, String>();
		normalisableTags.put("a","href");
		normalisableTags.put("img","src");
		String[] ttags = new String[] {
				"table",
				"tbody",
				"thead",
				"tr",
				"th",
				"td"
		};
		tableTags = Arrays.asList(ttags);
		logger = Logger.getLogger("scraper");
		try {
			FileHandler fh = new FileHandler("scrape.log");
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			logger.addHandler(fh);
		} catch (Exception e) {	e.printStackTrace();}
	}


	Extractor extractor;



	QueryRunner queryRunner;

	private int retries = 0;
	private int lastRevisionId = -1;
	private String[] urls;
	private Annotation[] annotations;
	private WebClient webClient;

	public Scrape(Extractor extractor){
		this.queryRunner = Application.getQueryRunner();
		this.webClient = Application.getWebClient();
		this.extractor = extractor;
	}

	private int createRevision() throws SQLException{
		Connection connection = Application.getDataSource().getConnection();	
		PreparedStatement pstmt = connection.prepareStatement(
				"INSERT INTO revisions (extractor_id,created_at,updated_at) VALUES (?,NOW(),NOW())",
				Statement.RETURN_GENERATED_KEYS);
		pstmt.setInt(1, extractor.getId());
		pstmt.executeUpdate();
		ResultSet rs = pstmt.getGeneratedKeys();
		rs.next();
		int lastRevisionId = rs.getInt(1);
		rs.close();
		pstmt.close();
		connection.close();
		return lastRevisionId;
	}
	@Override
	public Task getFollowUpActions() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isSuccessful() {
		// TODO Auto-generated method stub
		return false;
	}
	private void reloadExtractor() throws SQLException {
		List<Annotation> annotationList = queryRunner.query(
				"SELECT id,xpath FROM annotations WHERE extractor_id = ?",
				annotationListHandler,
				extractor.getId()
		);
		annotations = annotationList.toArray(new Annotation[annotationList.size()]);
		this.urls = queryRunner.query(
				"SELECT url FROM pages WHERE extractor_id = ?",
				arrayRSHandler,
				extractor.getId()
		);
		extractor.setAnnotations(annotationList);
		extractor.setUrls(this.urls);

	}

	private HtmlElement processTag(HtmlElement e,HtmlPage p){
		String attributeName = normalisableTags.get(e.getTagName());
		//normalise urls
		if(attributeName!=null) {
			String url = e.getAttribute(attributeName);
			try {
				url = ((HtmlPage)e.getPage()).getFullyQualifiedUrl(url).toString();
				e.setAttribute(attributeName, url);
			} catch (MalformedURLException e1) {
			}
		}
		HtmlElement old = e;
		e = replaceTable(e,p);
		old.getParentNode().insertBefore(e,old);
		//old.getParentNode().removeChild(e);
		return e;
	}


	private HtmlElement replaceTable(HtmlElement e,HtmlPage p) {
		HtmlElement replacement = e;
		if(tableTags.contains(e.getTagName().toLowerCase())) {
			replacement = p.createElement("div");
			Iterator<DomNode> dom = e.getChildren().iterator();
			DomNode n;
			while(dom.hasNext()){
				n = dom.next();
				if(n instanceof HtmlElement) n = replaceTable((HtmlElement)n,p);
				replacement.appendChild(n);
			}
		}
		return replacement;
	}


	private Object[][] buildBatchInsert(HtmlPage page, List<HtmlElement>[] selectedElements) {
		logger.log(Level.INFO,"["+lastRevisionId+"] Building values for revision.");
		ArrayList<Object[]> valuesToInsert = new ArrayList<Object[]>();
		Iterator<HtmlElement> elements = page.getHtmlElementDescendants().iterator();
		HtmlElement e;
		Date timeNow = new Date();
		while(elements.hasNext()) {
			e = elements.next();
			for(int j=0;j<selectedElements.length;j++) {
				if(selectedElements[j].contains(e)){
					e = processTag(e,page);
					valuesToInsert.add(new Object[] {annotations[j].getId(),e.asXml(),timeNow,timeNow,lastRevisionId});
					break;
				}
			}
		}
		Object[][] values = new Object[valuesToInsert.size()][valuesToInsert.get(0).length];
		for(int j=0;j<values.length;j++) values[j] = valuesToInsert.get(j);
		logger.log(Level.INFO,"["+lastRevisionId+"] Done building.");
		return values;
	}

	public void run() {
		try {
			reloadExtractor();
			//HashMap<Integer, List<HtmlElement>> labelItems = new HashMap<Integer, List<HtmlElement>>(annotations.size());
			if(lastRevisionId == -1) lastRevisionId = createRevision();
			logger.log(Level.INFO,"["+lastRevisionId+"-"+Thread.currentThread().getName()+"] Revision created.");
			for(String url:urls) {
				HtmlPage page = null;
				for(int r=0;r<RETRIES;r++) {
					try {
						page = webClient.getPage(url);
						break;
					} catch (IOException e) {}
				}
				if(page!=null) {
					List<HtmlElement>[] selectedElements = (List<HtmlElement>[])new List[annotations.length];
					for(int i=0;i<selectedElements.length;i++) selectedElements[i] = (List<HtmlElement>)page.getByXPath(annotations[i].getXpath());
					logger.log(Level.INFO,"["+lastRevisionId+"] Done inserting data...");
					queryRunner.batch(
							"INSERT INTO scraped_values (annotation_id,value,created_at,updated_at,revision_id) VALUES (?,?,?,?,?)",
							buildBatchInsert(page, selectedElements)
					);
					logger.log(Level.INFO,"["+lastRevisionId+"] Done inserting..");
				}
				page = null;
			}
			TaskExecutor.getInstance().queueTask(new Spider(extractor));
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.getMessage());
		} catch (FailingHttpStatusCodeException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
	}


}
