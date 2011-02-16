package database;


import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import main.Application;
import beans.Extractor;
import beans.Page;



public class DataAccess {
	/*
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
	*/
	public static Page retrievePage(Connection con,int id){
		QueryRunner run = Application.getQueryRunner();
		try {
			Page pages = run.query(
					con,
					"SELECT * FROM pages WHERE id = ?",
					new BeanHandler<Page>(Page.class),
					id);
			return pages;
		} catch (SQLException e) {
			Database.logger.warning(e.getMessage());
		}
		return null;
	}
	public static Extractor retrieveExtractor( int id) {
		QueryRunner run = Application.getQueryRunner();
		try {
			Extractor e = run.query(
					"SELECT id,domain, update_time as updateTime FROM extractors WHERE id = ?",
					new BeanHandler<Extractor>(Extractor.class),
					id
			);
			return e;
		} catch (SQLException e) {
			Database.logger.warning(e.getMessage());
		}
		return null;
	}
	
}
