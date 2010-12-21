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

import weka.classifiers.Classifier;


import learner.ElementClassifier;
import learner.Learner;
import main.Application;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class Crawler implements Serializable {
	
	final private double EXAMPLE_RATIO = 0.3;
	//Required tools
	private WebClient client;
	private Learner learner;

	
	//Collected data
	private Queue<Page> downloadQueue;
	private Map<String,Page> visited;
	private Collection<Page> wantedPages;

	//Seed Data
	private URL startUrl;
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
	
	public ElementClassifier startCrawl(String startUrl,String[] labels, String[] xpaths, int depth) throws MalformedURLException {
		this.xpaths 		= xpaths;
		this.startUrl		= new URL(startUrl);
		this.learner 		= new Learner(labels,xpaths);
		this.downloadQueue 	= new PriorityQueue<Page>();
		this.visited 		= new TreeMap<String,Page>(); 
		this.wantedPages 	= new HashSet<Page>();

		Page p = new Page(this.wantedPages,this.startUrl,0);
		
		visited.put(this.startUrl.toString(),p);
		downloadQueue.add(p);
		
		crawl();
		
		//Think about this. Needed?
		System.out.println("Creating classifier...");
		return learner.createClassifier();
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
		
		processLinks(parentPage);
		extractPageData(parentPage);
		
		if(parentPage.isWanted()) {	
			int collectedInstances = learner.feedTrainingData(
					parentPage.getHtmlPage(),
					parentPage.getWantedElements(),
					onlyPositive
			);
			onlyPositive = true;
			wantedPages.add(parentPage);
			double ratio = (double)totalPositiveInstances/collectedInstances;
			System.out.println("\tTotal +ve instances: " + totalPositiveInstances + " Total instances: "+ collectedInstances+" Ratio: " +ratio);
			if(ratio >= EXAMPLE_RATIO) stopCrawling = true;
		}
		
	}
	
	private void processLinks(Page p) {
		List<HtmlAnchor> links = p.getHtmlPage().getAnchors();
		for(HtmlAnchor a: links) processLink(a,p);
	}
	
	private void processLink(HtmlAnchor a, Page parentPage){
		try {
			URL url = CrawlerUtils.linkToUrl(a);
			if(url.getHost().equals(this.startUrl.getHost())){
				Page page = visited.get(url.toString());
				if(page==null){
					page = new Page(this.wantedPages,url, parentPage.getDepth()+1); 
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
	@SuppressWarnings("unchecked")
	private void extractPageData(Page p){
		int count = 0;
		List<HtmlElement>[] wantedElements = (List<HtmlElement>[])new List[xpaths.length];
		for(int i=0; i<xpaths.length;i++){
			wantedElements[i] = (List<HtmlElement>)p.getHtmlPage().getByXPath(xpaths[i]);
			if(wantedElements[i]==null || wantedElements[i].isEmpty()){
				p.setHtmlPage(null);
				return;
			}
			count+=wantedElements[i].size();
		}
		p.setWantedElements(wantedElements);
		totalPositiveInstances += count;
	}

	public Collection<Page> getPages() {
		return visited.values();
	}

	
}
