package processes.tasks.extraction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import main.Application;

import org.apache.commons.dbutils.QueryRunner;

import beans.Annotation;
import beans.Extractor;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import processes.TaskExecutor;

import processes.tasks.Task;
import utils.Utils;
import utils.WebClientFactory;


public class Scrape implements Task {
	private static Logger logger = Utils.createLogger("scraper");;
	Extractor extractor;
	QueryRunner queryRunner;
	private String[] urls;
	private Annotation[] annotations;

	public Scrape(Extractor extractor){
		this.queryRunner = Application.getQueryRunner();
		this.extractor = extractor;
	}

	private void reloadExtractor() throws SQLException {
		List<Annotation> annotationList = queryRunner.query(
				"SELECT id,xpath FROM annotations WHERE extractor_id = ?",
				ScrapeHelper.annotationListHandler,
				extractor.getId()
		);
		this.annotations = annotationList.toArray(new Annotation[annotationList.size()]);
		this.urls = queryRunner.query(
				"SELECT url FROM pages WHERE extractor_id = ?",
				ScrapeHelper.arrayRSHandler,
				extractor.getId()
		);
		extractor.setAnnotations(annotations);
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
		WebClient webClient = WebClientFactory.borrowClient();
		try {

			reloadExtractor();
			//HashMap<Integer, List<HtmlElement>> labelItems = new HashMap<Integer, List<HtmlElement>>(annotations.size());
			logger.log(Level.INFO,"Starting scheduled download for extractor...");
			int revisionId = -1;
			ArrayList<Object[]> valuesToInsert = new ArrayList<Object[]>();
			for(String url:urls) {
				boolean pass = true;
				HtmlPage page = ScrapeHelper.downloadPage(url,webClient,logger);
				if(page==null){
					logger.log(Level.INFO, url+" giving up.");
					continue;
				}
				List<HtmlElement>[] selectedElements = (List<HtmlElement>[])new List[annotations.length];
				for(int i=0;i<selectedElements.length;i++){
					selectedElements[i] = (List<HtmlElement>)page.getByXPath(annotations[i].getXpath());
					if(selectedElements[i].size()==0) pass = false;
					//insert failure code here.
				}
				if(!pass) {
					logger.warning("An error occured while downloading.");
					return;
				} else {
					revisionId = (revisionId == -1)?ScrapeHelper.createRevision(extractor):revisionId;
					buildBatchInsert(revisionId,page, selectedElements,valuesToInsert);
				}
			}
			
			Object[][] values = new Object[valuesToInsert.size()][valuesToInsert.get(0).length];
			for(int j=0;j<values.length;j++) values[j] = valuesToInsert.get(j);
			logger.log(Level.INFO,"["+revisionId+"]"+" Inserting...");
			queryRunner.batch(
					"INSERT INTO scraped_values (annotation_id,value,created_at,updated_at,revision_id) VALUES (?,?,?,?,?)",
					values
			);
			logger.log(Level.INFO,"["+revisionId+"]"+" Done.");
			//TaskExecutor.getInstance().queueTask(new Spider(extractor));
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.getMessage());
		} catch (FailingHttpStatusCodeException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
		WebClientFactory.returnClient(webClient);
		System.gc();
	}


}
