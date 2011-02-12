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

import main.Application;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import beans.Annotation;
import beans.Extractor;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;

import processes.TaskExecutor;
import processes.tasks.Task;


public class Scrape implements Task {
	private static BeanListHandler<Annotation> annotationListHandler = new BeanListHandler<Annotation>(Annotation.class);
	Extractor extractor;
	QueryRunner queryRunner;
	private WebClient webClient;

	private String[] urls;
	private List<Annotation> annotations;

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
				"td"
		};
		tableTags = Arrays.asList(ttags);
	}

	private int lastRevisionId = -1;
	private int retries = 0;

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

	public Scrape(Extractor extractor){
		this.queryRunner = Application.getQueryRunner();
		this.webClient = Application.getWebClient();
		this.extractor = extractor;
	}

	public void run() {
		try {
			this.annotations = queryRunner.query(
					"SELECT id,xpath FROM annotations WHERE extractor_id = ?",
					annotationListHandler,
					extractor.getId()
			);
			this.urls = queryRunner.query(
					"SELECT url FROM pages WHERE extractor_id = ?",
					arrayRSHandler,
					extractor.getId()
			);
			extractor.setAnnotations(this.annotations);
			extractor.setUrls(this.urls);


			//HashMap<Integer, List<HtmlElement>> labelItems = new HashMap<Integer, List<HtmlElement>>(annotations.size());
			ArrayList<Object[]> valuesToInsert = new ArrayList<Object[]>();
			if(lastRevisionId == -1) lastRevisionId = createRevision();
			System.out.println(lastRevisionId);
			for(String url:urls) {
				HtmlPage page = webClient.getPage(url);
				int[] ids = new int[annotations.size()];
				List<HtmlElement>[] selectedElements = (List<HtmlElement>[])new List[annotations.size()];
				int i = 0;
				for(Annotation annotation: annotations) {
					ids[i] = annotation.getId();
					selectedElements[i] = (List<HtmlElement>)page.getByXPath(annotation.getXpath());
					i++;
				}

				Iterator<HtmlElement> elements = page.getHtmlElementDescendants().iterator();
				HtmlElement e;
				Date timeNow = new Date();
				while(elements.hasNext()) {
					e = elements.next();
					for(int j=0;j<selectedElements.length;j++) {
						if(selectedElements[j].contains(e)){
							e = processTag(e,page);
							valuesToInsert.add(new Object[] {ids[j],e.asXml(),timeNow,timeNow,lastRevisionId});
							break;
						}
					}
				}
				Object[][] values = new Object[valuesToInsert.size()][valuesToInsert.get(0).length];
				for(int j=0;j<values.length;j++) values[j] = valuesToInsert.get(j);
				queryRunner.batch(
						"INSERT INTO scraped_values (annotation_id,value,created_at,updated_at,revision_id) VALUES (?,?,?,?,?)",
						values
				);
				lastRevisionId = -1;
				retries = 0;
			}


		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			if(retries < 3) {
				retries++;
				TaskExecutor.getInstance().queueTask(this);
			}
		}
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


}
