package processes.tasks.download;


import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import main.Application;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;

import processes.tasks.Task;
import utils.WebClientFactory;

import beans.Page;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class DownloadPage  implements Task{
	private static List<String> failures = new LinkedList<String>();

	protected Page page;
	protected QueryRunner queryRunner;
	private boolean successful;

	public DownloadPage(Page page,QueryRunner queryRunner) {
		init(page, queryRunner);
	}


	private HtmlPage getPage(WebClient client,String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		HtmlPage page = null;
		try{
			page = client.getPage(url);
		} catch(ScriptException e){
			System.out.println("Script ERROR!!");
			page = null;
		}
		return page;
	}

	public HtmlPage getPageWithFullUrl(WebClient client,String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException{ 
		HtmlPage page = getPage(client,url);
		processUrls(page);
		return page;
	}

	private void init(Page page,QueryRunner queryRunner) {
		this.page = page;
		this.queryRunner = queryRunner;
	}
	private void makeAttributeFullyQualified(HtmlPage page,List<HtmlElement> list,String attributeName) {
		for(HtmlElement n: list){
			try {
				n.setAttribute(attributeName, page.getFullyQualifiedUrl(n.getAttribute(attributeName)).toString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
	private void processUrls(HtmlPage page) {
		makeAttributeFullyQualified(page, (List<HtmlElement>)page.getByXPath("//*[@src]"), "src");
		makeAttributeFullyQualified(page, (List<HtmlElement>)page.getByXPath("//*[@href]"), "href");
	}
	private Connection getConnection(){
		Connection conn = null;
		try {
			conn = Application.getDataSource().getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return conn;
	}
	private HtmlPage downloadPage(WebClient c) {
		HtmlPage htmlPage=null;
		try {
			htmlPage = getPageWithFullUrl(c,page.getUrl());
		} catch (FailingHttpStatusCodeException e) {
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
		return htmlPage;
	}
	private void insertPage(Connection con,HtmlPage htmlPage) {
		try {

			if(htmlPage != null) {
				PreparedStatement ps = con.prepareStatement(
						"INSERT INTO revisions (html,page_id,created_at,updated_at) values (?,?,NOW(),NOW())"
				);
				ps.setCharacterStream(1,new StringReader(htmlPage.asXml()));
				ps.setInt(2, page.getId());
				ps.execute();
			}
		} catch (SQLException e) {
		}
	}


	@Override
	public void run() {
		WebClient c = WebClientFactory.borrowClient();
		Connection con = getConnection();
		HtmlPage p = downloadPage(c);
		if(p!=null)
			insertPage(con,p);
		DbUtils.closeQuietly(con);
		successful = true;
		WebClientFactory.returnClient(c);
	}

}
