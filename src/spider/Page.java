package spider;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Page implements Serializable,Comparable<Page> {

	private static final long serialVersionUID = -1937458209711391963L;
	
	private URL url;
	private int depth;
	private List<Page> incomingLinks;
	private List<Page> outgoingLinks;

	private HtmlPage htmlPage;
	private List<HtmlElement>[] wantedElements;
	
	private double score;

	@SuppressWarnings("unchecked")
	Page(Collection<Page> wantedPages, URL url,int depth){
		this.url = url;
		this.depth = depth;
		this.incomingLinks = new ArrayList<Page>();
		this.outgoingLinks = new ArrayList<Page>();
		setScore(wantedPages);
	}

	public boolean addToIncomingLinks(Page page){
		return incomingLinks.add(page);
	}
	public boolean addToOutgoingLinks(Page page){
		return outgoingLinks.add(page);
	}
	@Override
	public int compareTo(Page otherPage) {
		return (int)(this.getScore()-otherPage.getScore());
	}
	public int getDepth(){
		return depth;
	}
	public HtmlPage getHtmlPage() {
		return htmlPage;
	}
	public List<Page> getIncomingLinks() {
		return incomingLinks;
	}
	public List<Page> getOutgoingLinks() {
		return outgoingLinks;
	}

	
	public double getScore(){
		return this.score - this.incomingLinks.size();
	}

	public URL getUrl() {
		return url;
	}
	public List<HtmlElement>[] getWantedElements() {
		return wantedElements;
	}
	
	public boolean isWanted() {
		return wantedElements!=null;
	}
	
	
	public void removeFromIncomingLinks(Page page){
		incomingLinks.remove(page);
	}
	public void removeFromOutgoingLinks(Page page){
		outgoingLinks.remove(page);
	}

	public void removePage(){
		for(Page p: outgoingLinks){
			p.removeFromIncomingLinks(this);
		}
		for(Page p: incomingLinks){
			p.removeFromOutgoingLinks(this);
		}
	}
	
	private int score(Page p){
		String path1 = p.getUrl().getPath();
		String path2 = this.getUrl().getPath();
		int pathDistance = CrawlerUtils.editDistance(path1,path2);
		String query1 = p.getUrl().getQuery();
		String query2 = this.getUrl().getQuery();
		int queryDistance = CrawlerUtils.editDistance(query1,query2);
		return pathDistance * 2 + queryDistance;
	}

	@SuppressWarnings("unchecked")
	void setHtmlPage(HtmlPage htmlPage){
		this.htmlPage = htmlPage;
	}

	private void setScore(Collection<Page> wantedPages){
		if(wantedPages.size()==0) {
			this.score = 0;
		} else {
			int totalScore = 0;
			for(Page p: wantedPages) totalScore += score(p);
			this.score = (double)totalScore/wantedPages.size();
		}

	}
	protected void setWantedElements(List<HtmlElement>[] wantedElements) {
		this.wantedElements = wantedElements;
	}


}
