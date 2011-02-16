package processes.tasks.extraction;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import database.DataAccess;

import learner.ElementClassifier;
import main.Application;



import beans.Annotation;
import beans.Extractor;
import processes.tasks.Task;
import spider.Crawler;

public class Spider implements Task {
	private Extractor extractor;
	private String[] startUrls;
	
	private String[] ids;
	private String[] xpaths;
	
	
	public Spider(Extractor e) {
		this.startUrls = e.getUrls();
		Annotation[] annotations = e.getAnnotations();
		this.ids = new String[annotations.length];
		this.xpaths = new String[annotations.length];
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
			//loadClassifierModel(extractor);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		System.gc();
	}

	
	
	private void saveClassifierModel(ElementClassifier classifier) {
		Connection con;
		try {
			con = Application.getDataSource().getConnection();
			PreparedStatement pstmt = con.prepareStatement("UPDATE extractors SET cmodel = ? WHERE id = ?");
			pstmt.setObject	(1, classifier);
			pstmt.setInt	(2, extractor.getId());
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
	

}
