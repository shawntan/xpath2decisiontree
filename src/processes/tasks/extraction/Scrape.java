package processes.tasks.extraction;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.Application;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import beans.Annotation;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import processes.tasks.Task;


public class Scrape implements Task {
	private static BeanListHandler<Annotation> annotationListHandler = new BeanListHandler<Annotation>(Annotation.class);
	private static ArrayHandler arrayHandler = new ArrayHandler();
	private int extractorId;
	private Connection connection;
	private QueryRunner queryRunner;
	private WebClient webClient;
	
	private String[] urls;
	List<Annotation> annotations;
	
	

	private static ResultSetHandler<String[]> arrayRSHandler = 	new ResultSetHandler<String[]> (){
		public String[] handle(ResultSet rs)throws SQLException {
			rs.last();
			String[] urls = new String[rs.getRow()];
			rs.first();
			int i = 0;
			do{
				urls[i] = rs.getString(1);
				i++;
			}while(rs.next());
			return urls;
		}
	};

	Scrape(Connection connection, QueryRunner queryRunner, WebClient webClient, int extractorId){
		this.connection = connection;
		this.queryRunner = queryRunner;
		this.webClient = webClient;
		this.extractorId = extractorId;
	}

	public static void main(String[] args) {
		try {
			Application.loadSettings();
			Scrape s = new Scrape(
					Application.getDataSource().getConnection(),
					Application.getQueryRunner(),
					Application.getWebClient(),
					9
			);
			s.run();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			this.annotations = queryRunner.query(connection,
					"SELECT id,xpath FROM annotations WHERE extractor_id = ?",
					annotationListHandler,
					extractorId
			);
			this.urls = queryRunner.query(connection,
					"SELECT url FROM pages WHERE extractor_id = ?",
					arrayRSHandler,
					extractorId
			);
			HashMap<Integer, List<HtmlElement>> labelItems = new HashMap<Integer, List<HtmlElement>>(annotations.size());
			ArrayList<Object[]> valuesToInsert = new ArrayList<Object[]>();
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
				while(elements.hasNext()) {
					HtmlElement e = elements.next();
					for(int j=0;j<selectedElements.length;j++) {
						if(selectedElements[j].contains(e)){
							Date timeNow = new Date();
							valuesToInsert.add(new Object[] {ids[j],e.asXml(),timeNow,timeNow});
							break;
						}
					}
				}
				Object[][] values = new Object[valuesToInsert.size()][valuesToInsert.get(0).length];
				for(int j=0;j<values.length;j++){
					values[j] = valuesToInsert.get(j);
				}
				queryRunner.batch(connection,
						"INSERT INTO scraped_values (annotation_id,value,created_at,updated_at) VALUES (?,?,?,?)",
						values
						);
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
