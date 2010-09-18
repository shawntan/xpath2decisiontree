package processes.tasks.extraction;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import main.Application;

import beans.Annotation;
import beans.Page;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import processes.tasks.Task;
import utils.DataAccess;

public class Scrape implements Task {
	private static List<Annotation> getAnnotations(Connection con,Page page){
		QueryRunner run = Application.getQueryRunner();
		try {
			List<Annotation> annotations = run.query(
					con,
					"SELECT * FROM annotations WHERE page_id = ?",
					new BeanListHandler<Annotation>(Annotation.class),
					page.getId()
			);
			return annotations;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private static HashMap<Annotation,List<HtmlElement>> applyXpath(HtmlPage page,List<Annotation> annotations){
		HashMap<Annotation,List<HtmlElement>> results = new HashMap<Annotation, List<HtmlElement>>();
		for(Annotation a:annotations) {
			System.out.println(a.getLabel());
			System.out.println("-----------");
			System.out.println(a.getXpath());
			List<HtmlElement> selected = (List<HtmlElement>)page.getByXPath(a.getXpath());
			for(HtmlElement e:selected) {
				System.out.println(e.asText());
			}
			results.put(a,selected);
		}
		return results;
	}
	public static void main(String[] args) {
		Application.loadSettings();
		try {
			Connection con = Application.getDataSource().getConnection();
			Page p = DataAccess.retrievePage(con, 56);
			List<Annotation> annotations = getAnnotations(con, p);
			HtmlPage htmlPage = Application.getWebClient().getPage(p.getUrl());
			applyXpath(htmlPage,annotations);
			DbUtils.close(con);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub

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
