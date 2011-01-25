package processes.tasks.extraction;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import learner.ElementClassifier;
import main.Application;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;


import beans.Annotation;
import beans.Extractor;
import processes.tasks.Task;
import spider.Crawler;
import utils.DataAccess;

public class Spider implements Task {
	private QueryRunner queryRunner;
	private Extractor extractor;
	private String[] startUrls;
	
	private String[] ids;
	private String[] xpaths;
	
	
	public Spider(Extractor e) {
		queryRunner = Application.getQueryRunner();
		this.startUrls = e.getUrls();
		List<Annotation> annotations = e.getAnnotations();
		this.ids = new String[annotations.size()];
		this.xpaths = new String[annotations.size()];
		this.extractor = e;
		int i = 0;
		for(Annotation a:annotations){
			ids[i] = Integer.toString(a.getId());
			xpaths[i] = a.getXpath();
			i++;
		}	
	}
	
	@Override
	public void run() {
		Crawler crawler = new Crawler();
		try {
			ElementClassifier classifier = crawler.startCrawl(startUrls, ids, xpaths, 3);
			saveClassifierModel(classifier);
			loadClassifierModel(extractor);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		
	}
	private void loadClassifierModel(Extractor extractor) {
		Connection con;
		try {
			con = Application.getDataSource().getConnection();
			PreparedStatement pstmt = con.prepareStatement("SELECT cmodel FROM extractors WHERE id = ?");
			pstmt.setInt(1,extractor.getId());
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				byte[] buf = rs.getBytes(1);
				if (buf != null) {
					ElementClassifier c = ElementClassifier.readElementClassifier(new ByteArrayInputStream(buf));
					System.out.println(c);
				}
			}
			rs.close();
			pstmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	private void saveClassifierModel(ElementClassifier classifier) {
		Connection con;
		try {
			con = Application.getDataSource().getConnection();
			PreparedStatement pstmt = con.prepareStatement("UPDATE extractors SET cmodel = ? WHERE id = ?");
			pstmt.setObject(	1, classifier);
			pstmt.setInt(		2, extractor.getId());
			System.out.println(pstmt);
			pstmt.executeUpdate();
			pstmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		Application.loadSettings();
		Extractor e =  DataAccess.retrieveExtractor(2);
		Scrape scrape = new Scrape(e);
		scrape.run();
		Spider s = new Spider(e);
		System.out.println(Arrays.toString(s.ids));
		System.out.println(Arrays.toString(s.xpaths));
		s.run();
	}
	
	@Override
	public Task getFollowUpActions() {
		return null;
	}

	@Override
	public boolean isSuccessful() {
		return false;
	}

}
