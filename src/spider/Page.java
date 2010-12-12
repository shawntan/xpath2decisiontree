package spider;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import learner.data.AttributeValues;


import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Page implements Serializable,Comparable<Page> {



	private int depth;

	private List<Page> incomingLinks;
	private List<Page> outgoingLinks;
	private URL url;
	private HtmlPage htmlPage;

	private String[] wantedXPaths;
	private List<HtmlElement>[] wantedElements;
	private boolean wanted;

	private AttributeValues attributeValues;

	private int score;

	Page(URL url,int depth, String[] wantedXPaths){
		this.url = url;
		this.depth = depth;
		this.wantedXPaths = wantedXPaths;
		this.wantedElements = (List<HtmlElement>[])new List[wantedXPaths.length];
		incomingLinks = new ArrayList<Page>();
		outgoingLinks = new ArrayList<Page>();
		attributeValues = new AttributeValues();
		score = 0;
	}

	public URL getUrl() {
		return url;
	}
	public List<Page> getIncomingLinks() {
		return incomingLinks;
	}
	public List<Page> getOutgoingLinks() {
		return outgoingLinks;
	}
	public boolean addToIncomingLinks(Page page){
		return incomingLinks.add(page);
	}
	public boolean addToOutgoingLinks(Page page){
		return outgoingLinks.add(page);
	}
	public void removeFromIncomingLinks(Page page){
		incomingLinks.remove(page);
	}
	public void removeFromOutgoingLinks(Page page){
		outgoingLinks.remove(page);
	}
	public int getDepth(){
		return depth;
	}
	public HtmlPage getHtmlPage() {
		return htmlPage;
	}
	void setHtmlPage(HtmlPage htmlPage){
		this.htmlPage = htmlPage;
		for(int i=0; i<wantedXPaths.length;i++){
			wantedElements[i] = (List<HtmlElement>)htmlPage.getByXPath(wantedXPaths[i]);
			if(wantedElements[i]==null || wantedElements[i].isEmpty()) return;
		}
		this.wanted = true;
		System.out.println("Wanted page!!");
	}

	public AttributeValues getAttributeValues() {
		return attributeValues;
	}

	public void removePage(){
		for(Page p: outgoingLinks){
			p.removeFromIncomingLinks(this);
		}
		for(Page p: incomingLinks){
			p.removeFromOutgoingLinks(this);
		}
	}
	public void setScore(Page startPage){
		String path1 = startPage.getUrl().getPath();
		String path2 = this.getUrl().getPath();
		int pathDistance = CrawlerUtils.editDistance(path1,path2);
		String query1 = startPage.getUrl().getQuery();
		String query2 = this.getUrl().getQuery();
		int queryDistance = CrawlerUtils.editDistance(query1,query2);
		this.score = pathDistance * 2 + queryDistance;
	}
	public int getScore(){
		return this.score;
	}

	@Override
	public int compareTo(Page otherPage) {
		return this.getScore() - otherPage.getScore(); 
	}

	public boolean isWanted() {
		return wanted;
	}


}
