package spider;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

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
	public Crawler() {
		Application.loadSettings();
		client = Application.getWebClient();
		client.setTimeout(3000);
		downloadQueue = new LinkedBlockingQueue<Page>();
		visited = new TreeMap<String,Page>(); 
		continueCrawl = true;
	}

	public void crawl(String startUrl,int depth) throws MalformedURLException {
		URL url = new URL(startUrl);
		Page p = new Page(url,0);
		this.depth = depth;
		visited.put(url.toString(),p);
		downloadQueue.add(p);
		entryPoint = p;
		crawl();
	}
	private void crawl(){
		while(!downloadQueue.isEmpty()){
			Page p = downloadQueue.poll(); 
			try {
				System.out.println("Links left:"+downloadQueue.size()+" Depth: "+p.getDepth() +" Downloading "+p.getUrl().toString());

				if(p.getDepth() < this.depth){
					HtmlPage htmlPage = client.getPage(p.getUrl());
					p.setHtmlPage(htmlPage);
					processPage(p,htmlPage, p.getDepth());
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

	private URL linkToUrl(HtmlAnchor a) throws MalformedURLException{
		HtmlPage page = (HtmlPage)a.getPage();
		String url = a.getHrefAttribute();
		int index = url.indexOf('#');
		if(index<0) return page.getFullyQualifiedUrl(url);
		else {
			url = url.substring(0,index);
			return page.getFullyQualifiedUrl(url);
		}

	}

	private void processPage(Page parentPage,HtmlPage htmlPage,int depth){
		if(continueCrawl) {
			List<HtmlAnchor> links = htmlPage.getAnchors();
			for(HtmlAnchor a: links){
				try {
					URL url = linkToUrl(a);
					if(url.getHost().equals(parentPage.getUrl().getHost())){
						Page page = visited.get(url.toString());
						if(page==null){
							page = new Page(url,depth+1);
							downloadQueue.add(page);
							visited.put(url.toString(), page);
						}
						page.addToIncomingLinks(parentPage);	
						parentPage.addToOutgoingLinks(page);
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			if(downloadQueue.size() > 100) continueCrawl = false;
		}
		/*
		AttributeValues av = parentPage.getAttributeValues();
		LinkedList<LearnerData> collected = new LinkedList<LearnerData>();
		FeatureExtractor<LearnerData> extractor = new FeatureExtractor<LearnerData>(av);
		extractor.extractFromHtmlPage(collected, htmlPage,
			"//div[@id='mainContent']/div[1]/div[1]/ul/li/h3[1]/a[1]"
		);
		collected = null;
		extractor = null;
		Set<String> attSet = parentPage.getAttributeValues().keySet();
		Set<String> entrySet = entryPoint.getAttributeValues().keySet();
		HashSet<String> origSet = new HashSet<String>(entryPoint.getAttributeValues().keySet());
		origSet.retainAll(attSet);
		System.out.println(((double)origSet.size()/entrySet.size())*100);*/
	}
	public Page getMostIncomingLinks(){
		Collection<Page> pages = visited.values();
		Page highest = null;
		for(Page p:pages){
			if(highest==null || highest.getIncomingLinks().size() < p.getIncomingLinks().size())
				highest = p;
		}
		return highest;
	}
	public Collection<Page> getPages() {
		return visited.values();
	}

}
