package processes.tasks.extraction;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import main.Application;

import beans.Annotation;
import beans.Extractor;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ScrapeHelper {
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
				"th",
				"td"
		};
		tableTags = Arrays.asList(ttags);
		
	}
	public static HtmlElement processTag(HtmlElement e,HtmlPage p){
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
		old.getParentNode().removeChild(old);
		return e;
	}

	private static HtmlElement replaceTable(HtmlElement e,HtmlPage p) {
		HtmlElement replacement = e;
		if(tableTags.contains(e.getTagName().toLowerCase())) {
			replacement = p.createElement("div");
			Iterator<DomNode> dom = e.getChildren().iterator();
			DomNode n;
			while(dom.hasNext()){
				n = dom.next();
				replacement.appendChild(n);
			}
		}
		return replacement;
	}

	public static int createRevision(Extractor extractor) throws SQLException{
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

	private static int RETRIES = 3;

	public static HtmlPage downloadPage(String url, WebClient webClient, Logger logger) {
		HtmlPage page = null;
		for(int r=0;r<RETRIES;r++) {
			try {
				page = webClient.getPage(url);
				break;
			} catch (IOException e) {
				if(logger != null) 	logger.log(Level.WARNING,url+ " "+ e.getMessage() +" -> "+"Retry: "+(r+1));
			}
		}
		return page;
	}

	final public static ResultSetHandler<String[]> arrayRSHandler = 	new ResultSetHandler<String[]> (){
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
	final public static BeanListHandler<Annotation> annotationListHandler = new BeanListHandler<Annotation>(Annotation.class);

}
