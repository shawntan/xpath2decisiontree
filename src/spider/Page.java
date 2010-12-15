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
	private int depth;
	private List<Page> incomingLinks;
	private List<Page> outgoingLinks;
	private URL url;
	private HtmlPage htmlPage;

	private List<HtmlElement>[] wantedElements;
	
	private boolean wanted;
	private int positiveInstanceCount = -1;
	private int instanceCount = -1;
	
	private double score;

	@SuppressWarnings("unchecked")
	Page(URL url,int depth){
		this.url = url;
		this.depth = depth;
		
		incomingLinks = new ArrayList<Page>();
		outgoingLinks = new ArrayList<Page>();
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
	
	@SuppressWarnings("unchecked")
	void setHtmlPage(HtmlPage htmlPage){
		this.htmlPage = htmlPage;
	}

	public void removePage(){
		for(Page p: outgoingLinks){
			p.removeFromIncomingLinks(this);
		}
		for(Page p: incomingLinks){
			p.removeFromOutgoingLinks(this);
		}
	}
	public void setScore(Collection<Page> wantedPages){
		int totalScore = 0;
		for(Page p: wantedPages) totalScore += score(p);
		this.score = (double)totalScore/wantedPages.size();
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
	
	
	public double getScore(){
		return this.score;
	}

	@Override
	public int compareTo(Page otherPage) {
		double thisScore = this.getScore() - this.incomingLinks.size();
		double otherScore = otherPage.getScore() - otherPage.incomingLinks.size();
		return (int)(thisScore - otherScore);
	}

	public boolean isWanted() {
		return wanted;
	}
	void setWanted(boolean wanted){
		this.wanted=wanted;
	}

	public List<HtmlElement>[] getWantedElements() {
		return wantedElements;
	}

	int getPositiveInstanceCount() {
		return positiveInstanceCount;
	}

	void setPositiveInstanceCount(int positiveInstanceCount) {
		this.positiveInstanceCount = positiveInstanceCount;
	}

	int getInstanceCount() {
		return instanceCount;
	}

	void setInstanceCount(int instanceCount) {
		this.instanceCount = instanceCount;
	}

	void setWantedElements(List<HtmlElement>[] wantedElements) {
		this.wantedElements = wantedElements;
	}


}
