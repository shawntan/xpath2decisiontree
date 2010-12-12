package spider;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;


import learner.Learner;
import main.Application;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class Crawler implements Serializable {
	WebClient client;
	Queue<Page> downloadQueue;
	Map<String,Page> visited;
	Page entryPoint;
	int depth;
	boolean continueCrawl;
	String domain;
	String[] xpaths;
	Learner[] learners;

	public Crawler() {
		Application.loadSettings();
		client = Application.getWebClient();
		client.setTimeout(3000);
		downloadQueue = new PriorityQueue<Page>();
		visited = new TreeMap<String,Page>(); 
		continueCrawl = true;
	}

	public void startCrawl(String startUrl,String[] xpaths, int depth) throws MalformedURLException {
		URL url = new URL(startUrl);
		Page p = new Page(url,0,xpaths);
		this.depth = depth;
		this.xpaths = xpaths;
		this.domain = url.getHost();
		this.learners = new Learner[xpaths.length];
	
		visited.put(url.toString(),p);
		downloadQueue.add(p);
		entryPoint = p;
		crawl();
	}
	
	private void crawl(){
		while(!downloadQueue.isEmpty()){
			Page p = downloadQueue.poll();
			System.out.println(p.getScore()+"\t"+p.getUrl());
			try {
				System.out.println("Links left:"+downloadQueue.size()+" Depth: "+p.getDepth() +" Downloading "+p.getUrl().toString());
				if(p.getDepth() < this.depth){
					HtmlPage htmlPage = client.getPage(p.getUrl());
					p.setHtmlPage(htmlPage);
					processPage(p);
				}
			} catch (FailingHttpStatusCodeException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassCastException e) {
				System.out.println("This is feed lah!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("\"And we are done.\" - Sanjay Jain");
	}

	private void processPage(Page parentPage){
		if(continueCrawl) {
			List<HtmlAnchor> links = parentPage.getHtmlPage().getAnchors();
			for(HtmlAnchor a: links){
				processLink(a,parentPage);
			}
			if(downloadQueue.size()>= 50) continueCrawl = false;
		}
	}
	
	private void processLink(HtmlAnchor a, Page parentPage){
		try {
			URL url = CrawlerUtils.linkToUrl(a);
			if(url.getHost().equals(this.domain)){
				Page page = visited.get(url.toString());
				if(page==null){
					page = new Page(url, parentPage.getDepth() ,xpaths); 
					page.setScore(entryPoint);
					downloadQueue.add(page);
					visited.put(url.toString(), page);
				}
				if(parentPage != page) {
					page.addToIncomingLinks(parentPage);
					parentPage.addToOutgoingLinks(page);
				}
			}
		} catch (MalformedURLException e) {
		}
	}

	public Page getMostIncomingLinks(){
		Collection<Page> p = visited.values();
		Page[] parr = new Page[p.size()];
		p.toArray(parr);
		Arrays.sort(parr);
		System.out.println("Top 10 links:");
		for(int i=0;i<10 && i<p.size();i++){
			System.out.println(parr[i].getIncomingLinks().size()+"\t"+parr[i].getUrl());
		}
		return parr[0];
	}	
	public Collection<Page> getPages() {
		return visited.values();
	}
}
