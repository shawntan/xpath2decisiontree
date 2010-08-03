package processes.tasks;


import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import main.Application;

import org.apache.commons.dbutils.QueryRunner;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


import beans.Page;

public class DownloadPage  implements Task{
	private static List<String> failures = new LinkedList<String>();

	private WebClient client;
	protected Page page;
	protected QueryRunner queryRunner;
	private boolean successful;

	public DownloadPage(Page page,QueryRunner queryRunner) {
		init(page, queryRunner);
	}
	private void init(Page page,QueryRunner queryRunner) {
		client = Application.getWebClient();
		this.page = page;
		this.queryRunner = queryRunner;
	}


	@Override
	public void run() {
		try {
			System.out.println("\tDownloading...");
			HtmlPage htmlPage=null;
			try {
				htmlPage = getPageWithFullUrl(page.getUrl());
				System.out.println("\tDone downloading.");
			}catch (FailingHttpStatusCodeException e) {
				System.out.println("HTTP failure code.");
			} catch (MalformedURLException e) {
				System.out.println("Malformed URL.");
			} catch (IOException e) {
				System.out.println("Download error.");
				//System.out.println(e.getMessage());
			}
			if(htmlPage != null) {
				queryRunner.update(
						"INSERT INTO revisions (html,page_id,created_at,updated_at) values (?,?,NOW(),NOW())",
						htmlPage.asXml(),
						page.getId()
				);
				PreparedStatement ps = Application
				.getDataSource()
				.getConnection()
				.prepareStatement("INSERT INTO revisions (html,page_id,created_at,updated_at) values (?,?,NOW(),NOW())");
				ps.setCharacterStream(1,new StringReader(htmlPage.asXml()));
				ps.setInt(2, page.getId());
				ps.execute();
			}
		} catch (SQLException e) {
			System.out.println("Database insert error");
			System.out.println(e.getMessage());
		}
			try {
				queryRunner.update(
						"UPDATE pages SET updated_at = NOW() WHERE pages.id = ?",
						page.getId()
				);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("UPDATED TIMESTAMP!!!");

			successful = true;

	}

	private HtmlPage getPage(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		HtmlPage page = null;
		try{
			page = client.getPage(url);
		} catch(ScriptException e){
			System.out.println("Script ERROR!!");
			page = null;
		}
		return page;
	}

	private void makeAttributeFullyQualified(HtmlPage page,List<HtmlElement> list,String attributeName) {
		for(HtmlElement n: list){
			try {
				n.setAttribute(attributeName, page.getFullyQualifiedUrl(n.getAttribute(attributeName)).toString());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public HtmlPage getPageWithFullUrl(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException{ 
		HtmlPage page = getPage(url);
		System.out.println("\tAltering URLs...");
		processUrls(page);
		return page;
	}
	public void processUrls(HtmlPage page) {
		makeAttributeFullyQualified(page, (List<HtmlElement>)page.getByXPath("//*[@src]"), "src");
		makeAttributeFullyQualified(page, (List<HtmlElement>)page.getByXPath("//*[@href]"), "href");
	}

	@Override
	public boolean isSuccessful() {
		return successful;
	}

	@Override
	public Task getFollowUpActions() {
		return new Task() {
			public void run() {
				DownloadPage.failures.add(page.getUrl());
			}
			public boolean isSuccessful() {
				return true;
			}

			@Override
			public Task getFollowUpActions() {return null;}
		};
	}



}
