package processes.tasks.extraction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import main.Application;

import org.apache.commons.dbutils.QueryRunner;
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
	private static BeanListHandler<Annotation> annotationListHandler = new BeanListHandler<Annotation>(Annotation.class);
	static {
		try {
			logger = Logger.getLogger("scraper");
			FileHandler fh = new FileHandler("scrape.log");
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			logger.addHandler(fh);
		} catch (Exception e) {	e.printStackTrace();}
	}


	Extractor extractor;



	QueryRunner queryRunner;

	private String[] urls;
	private Annotation[] annotations;
	private WebClient webClient;

	public Scrape(Extractor extractor){
		this.queryRunner = Application.getQueryRunner();
		this.webClient = Application.getWebClient();
		this.extractor = extractor;
	}

	@Override
	public Task getFollowUpActions() {
		// TODO Auto-generated method stub
		return null;
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
				ScrapeHelper.arrayRSHandler,
				extractor.getId()
		);
		extractor.setAnnotations(annotationList);
		extractor.setUrls(this.urls);
	}
	private void buildBatchInsert(int revisionId, HtmlPage page, List<HtmlElement>[] selectedElements,List<Object[]> valuesToInsert) {
		logger.log(Level.INFO,"["+revisionId+"] Building values for revision.");
		Date timeNow = new Date();

		traverseDOM(revisionId,page, page.getBody(), selectedElements,valuesToInsert,timeNow);


		logger.log(Level.INFO,"["+revisionId+"] Done building.");
	}
	private void traverseDOM(
			int revisionId, HtmlPage page,HtmlElement parent,
			List<HtmlElement>[] selectedElements, List<Object[]> valuesToInsert,
			Date timeNow) {
		DomNode e = parent.getFirstChild();
		HtmlElement el = null;
		if(e!=null) {
			do {
				if(e instanceof HtmlElement) {
					el = (HtmlElement)e;
					traverseDOM(revisionId,page,el,selectedElements,valuesToInsert,timeNow);
				}
			}while((e=e.getNextSibling())!=null);
		}
		for(int j=0;j<selectedElements.length;j++) {
			if(selectedElements[j].contains(parent)){
				parent = ScrapeHelper.processTag(parent,page);
				valuesToInsert.add(new Object[] {annotations[j].getId(),parent.asXml(),timeNow,timeNow,revisionId});
				break;
			}
		}
	}
	
	public void run() {
		try {
			reloadExtractor();
			//HashMap<Integer, List<HtmlElement>> labelItems = new HashMap<Integer, List<HtmlElement>>(annotations.size());
			
			logger.log(Level.INFO,"Starting scheduled download for extractor...");
			int revisionId = -1;
			ArrayList<Object[]> valuesToInsert = new ArrayList<Object[]>();
			for(String url:urls) {
				HtmlPage page = ScrapeHelper.downloadPage(url,webClient,logger);
				if(page!=null) {
					List<HtmlElement>[] selectedElements = (List<HtmlElement>[])new List[annotations.length];
					for(int i=0;i<selectedElements.length;i++){
						selectedElements[i] = (List<HtmlElement>)page.getByXPath(annotations[i].getXpath());
						//insert failure code here.
					}
					revisionId = (revisionId == -1)?ScrapeHelper.createRevision(extractor):revisionId;
					buildBatchInsert(revisionId,page, selectedElements,valuesToInsert);
				} else logger.log(Level.INFO, "["+revisionId+"]"+"Giving up.");
				page = null;
			}
			Object[][] values = new Object[valuesToInsert.size()][valuesToInsert.get(0).length];
			for(int j=0;j<values.length;j++) values[j] = valuesToInsert.get(j);
			logger.log(Level.INFO," Done building data. Inserting...");
			queryRunner.batch(
					"INSERT INTO scraped_values (annotation_id,value,created_at,updated_at,revision_id) VALUES (?,?,?,?,?)",
					values
			);
			//TODO:TaskExecutor.getInstance().queueTask(new Spider(extractor));
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.getMessage());
		} catch (FailingHttpStatusCodeException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
	}


}
