package processes.tasks.extraction;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import learner.ClassifiedTask;
import learner.ElementClassifier;
import main.Application;
import beans.Annotation;
import beans.Extractor;
import processes.tasks.Task;
import utils.DataAccess;

public class FallbackScrape implements Task {
	ElementClassifier classifier;
	Extractor extractor;
	WebClient client;
	QueryRunner queryRunner;

	public FallbackScrape(Extractor extractor){
		this.extractor = extractor;
		this.classifier = loadClassifierModel(extractor);
		this.client = Application.getWebClient();
		this.queryRunner = Application.getQueryRunner();
		this.client = Application.getWebClient();
	}

	public void run() {
		if(this.classifier==null) return;
		String[] urls = extractor.getUrls();
		final ArrayList<Object[]> valuesToInsert = new ArrayList<Object[]>();
		final Date timeNow = new Date();
		final int revisionId;
		final Annotation[] annotations= extractor.getAnnotations();
		try {
			revisionId = ScrapeHelper.createRevision(extractor);
			HtmlPage page;
			for(int i=0;i<urls.length;i++) {
				try {
					page = ScrapeHelper.downloadPage(urls[0],client,null);
					System.out.println(page.getTitleText());
					classifier.classifyPageElements(page,
							new ClassifiedTask() {
						public void performTask(int label, DomNode element) {
							if(label < annotations.length  && !element.getNodeName().equalsIgnoreCase("body")) {
								valuesToInsert.add(
										new Object[] {annotations[label].getId(),element.asXml(),timeNow,timeNow,revisionId}
								);
							}
						}
					}
					);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			Object[][] values = new Object[valuesToInsert.size()][valuesToInsert.get(0).length];
			for(int j=0;j<values.length;j++) values[j] = valuesToInsert.get(j);

			queryRunner.batch(
					"INSERT INTO scraped_values (annotation_id,value,created_at,updated_at,revision_id) VALUES (?,?,?,?,?)",
					values
			);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public Task getFollowUpActions() {
		// TODO Auto-generated method stub
		return null;
	}

	private ElementClassifier loadClassifierModel(Extractor extractor) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = Application.getDataSource().getConnection();
			pstmt = con.prepareStatement("SELECT cmodel FROM extractors WHERE id = ?");
			pstmt.setInt(1,extractor.getId());
			rs = pstmt.executeQuery();
			if(rs.next()) {
				byte[] buf = rs.getBytes(1);
				if (buf != null) {
					ElementClassifier c = ElementClassifier.readElementClassifier(new ByteArrayInputStream(buf));
					System.out.println(c);
					return c;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				pstmt.close();
				con.close();
			} catch (Exception e) {}
		}
		return null;
	}
	public static void main(String[] args) {
		Application.loadSettings();
		Extractor extractor = DataAccess.retrieveExtractor(31);

		FallbackScrape fbs = new FallbackScrape(extractor);
		try {
			extractor.setUrls(fbs.queryRunner.query(
					"SELECT url FROM pages WHERE extractor_id = ?",
					ScrapeHelper.arrayRSHandler,
					extractor.getId()
			));
			List<Annotation> annotations = fbs.queryRunner.query(
					"SELECT id,xpath FROM annotations WHERE extractor_id = ?",
					ScrapeHelper.annotationListHandler,
					extractor.getId()
			);
			extractor.setAnnotations(annotations.toArray(new Annotation[annotations.size()]));

		} catch (SQLException e) {e.printStackTrace();}
		fbs.run();
	}
}

