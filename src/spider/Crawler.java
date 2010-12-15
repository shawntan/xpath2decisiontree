package spider;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class Crawler implements Serializable {
	
	//Required tools
	private WebClient client;
	private Learner learner;

	//Collected data
	private Queue<Page> downloadQueue;
	private Map<String,Page> visited;
	private Collection<Page> wantedPages;

	//Data to extract
	private String domain;
	private String[] xpaths;

	//Flow control
	private int totalPositiveInstances;
	private boolean stopCrawling = false;
	private boolean onlyPositive = false;
	
	public Crawler() {
		Application.loadSettings();
		client = Application.getWebClient();
		client.setTimeout(3000);

	}
	public void startCrawl(String startUrl,String[] labels, String[] xpaths, int depth) throws MalformedURLException {
		URL url = new URL(startUrl);
		Page p = new Page(url,0);
		
		this.xpaths 		= xpaths;
		this.domain 		= url.getHost();
		this.learner 		= new Learner(labels,xpaths);
		this.downloadQueue 	= new PriorityQueue<Page>();
		this.visited 		= new TreeMap<String,Page>(); 
		this.wantedPages 	= new HashSet<Page>();

		visited.put(url.toString(),p);
		downloadQueue.add(p);
		
		crawl();
		
		//Think about this. Needed?
		System.out.println("Creating classifier...");
		learner.createClassifier();
	}

	private void crawl(){
		while(!downloadQueue.isEmpty() && !stopCrawling){
			Page p = downloadQueue.poll();
			try {
				System.out.println(
						" Links left:"+downloadQueue.size() +
						" Depth: "+p.getDepth() + 
						" Score: "+ p.getScore()+
						" Downloading "+p.getUrl().toString()
				);
				HtmlPage htmlPage = client.getPage(p.getUrl());
				p.setHtmlPage(htmlPage);
				processPage(p);
			} catch (FailingHttpStatusCodeException e) {
				System.out.println("Failed.");
			} catch (IOException e) {
				System.out.println("Retry?");
			} catch (ClassCastException e) {
				System.out.println("This is feed lah!");
			}
		}
		System.out.println("\"And we are done.\" - Sanjay Jain");
	}

	private void processPage(Page parentPage){
		extractPageData(parentPage);
		if(parentPage.isWanted()) {	
			int collectedInstances = learner.feedTrainingData(
					parentPage.getHtmlPage(),
					parentPage.getWantedElements(),
					onlyPositive
			);
			onlyPositive = true;
			wantedPages.add(parentPage);
			System.out.println("Wanted page!!");
			
			double ratio = (double)totalPositiveInstances/collectedInstances;
			System.out.println(" Total +ve instances: " + totalPositiveInstances + " Total instances: "+ collectedInstances+" Ratio: " +ratio);
			if(ratio >= 0.5) stopCrawling = true;
		}
		List<HtmlAnchor> links = parentPage.getHtmlPage().getAnchors();
		for(HtmlAnchor a: links){
			processLink(a,parentPage);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void extractPageData(Page p){
		int count = 0;
		List<HtmlElement>[] wantedElements = (List<HtmlElement>[])new List[xpaths.length];
		for(int i=0; i<xpaths.length;i++){
			wantedElements[i] = (List<HtmlElement>)p.getHtmlPage().getByXPath(xpaths[i]);
			if(wantedElements[i]==null || wantedElements[i].isEmpty()) return;
			count+=wantedElements[i].size();
		}
		p.setWanted(true);
		p.setPositiveInstanceCount(count);
		p.setWantedElements(wantedElements);
		totalPositiveInstances += count;
	}

	private void processLink(HtmlAnchor a, Page parentPage){
		try {
			URL url = CrawlerUtils.linkToUrl(a);
			if(url.getHost().equals(this.domain)){
				Page page = visited.get(url.toString());
				if(page==null){
					page = new Page(url, parentPage.getDepth()+1 ); 
					page.setScore(wantedPages);
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
