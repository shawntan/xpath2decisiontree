import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import spider.Crawler;
import spider.Page;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import learner.Learner;
import main.Application;


public class Spider {

	public static void main(String[] args) {
		Crawler c = new Crawler();
		try {
			c.crawl("http://en.wikipedia.org/wiki/Artificial_intelligence",2);
			Page p = c.getMostIncomingLinks();
			System.out.println("Highest LINKS!: "+p.getUrl());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} 
	}

	private URL startUrl;
	private String domain;
	private WebClient client;
	private int depth;
	private TreeSet<String> visited;
	private File output;
	private PrintStream out;
	public Spider(WebClient client,String startUrl,int depth) throws MalformedURLException {
		this.startUrl = new URL(startUrl);
		this.domain = this.startUrl.getHost();
		System.out.println(domain);
		this.client = client;
		this.depth = depth;
		this.visited = new TreeSet<String>();
	}
	public void startCrawl(){
		System.out.println("Starting crawl: " + startUrl);
		try {
			output = new File(startUrl.getHost());
			out = new PrintStream(output);
			HtmlPage page = client.getPage(startUrl);
			crawl(startUrl.toString(),page,depth);
			System.out.println("Done finally");
			out.close();
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private void performActions(String url,HtmlPage page){
		Learner learner = new Learner();
		learner.feedTrainingData(page, "/html/body/center[1]/table[1]/tbody[1]/tr[3]/td[1]/table[1]/tbody[1]/tr/td[contains(concat(' ',@class,' '),' title ')]/a[1]");
		out.format("%40s%10d\n", url,learner.getAttributeValues().size());
	}
	
	public void crawl(String address,HtmlPage page,int depth){
		performActions(address,page);
		if(depth > 0) {
			List<HtmlAnchor> anchors = page.getAnchors();
			for(HtmlAnchor a: anchors) {
				for(int i=0;i<(this.depth-depth);i++) System.out.print("\t");
				HtmlPage p = null;
				try {
					URL url = page.getFullyQualifiedUrl(a.getHrefAttribute());
					if(visited.contains(url.toString())) continue;
					else if(!startUrl.getHost().equals(url.getHost())) continue;
					else {
						p = client.getPage(url);
						System.out.print("Descending... "+url);
						System.out.println();
						visited.add(url.toString());
					}	
					if(p!=null) crawl(url.toString(),p,depth-1);
				} catch (Exception e){
					e.printStackTrace();
				}
				
				
			}
		}
	}
}
