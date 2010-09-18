package utils;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import main.Application;

import beans.Page;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class DataAccess {
	public static HtmlPage retrieveHtmlPage(Connection con,Page page){
		try {
			
			PreparedStatement pstmt = con.prepareStatement(
					"SELECT html FROM revisions WHERE revisions.page_id = ? ORDER BY revisions.id LIMIT 1"
					);
			pstmt.setInt(1,page.getId());
			System.out.println(pstmt);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				InputStream is = rs.getAsciiStream("html");
				WebResponse wr = new PseudoWebResponse(page.getUrl(),is);
				WebClient wc = Application.getWebClient();
				HtmlPage hp = HTMLParser.parseHtml(wr, wc.getCurrentWindow());
				return hp;
			}
			DbUtils.close(con);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static Page retrievePage(Connection con,int id){
		QueryRunner run = Application.getQueryRunner();
		try {
			List<Page> pages = run.query(
					con,
					"SELECT * FROM pages WHERE id = ?",
					new BeanListHandler<Page>(Page.class),
					id);
			return pages.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		Application.loadSettings();
		Connection con;
		try {
			con = Application.getDataSource().getConnection();
			Page page = DataAccess.retrievePage(con,52);
			HtmlPage htmlPage = DataAccess.retrieveHtmlPage(con,page);
			//System.out.println(htmlPage.asXml());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
