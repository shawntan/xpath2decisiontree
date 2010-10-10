package spider;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import learner.data.AttributeValues;


import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Page implements Serializable {
	/*
	 * Think about what other info page class should carry:
	 * -Labels found?
	 * -Number of each labels found?
	 */
	private URL url;
	private List<Page> incomingLinks;
	private List<Page> outgoingLinks;
	private HtmlPage htmlPage;
	private int depth;
	private AttributeValues attributeValues;
	
	Page(URL url,int depth){
		this.url = url;
		this.depth = depth;
		incomingLinks = new ArrayList<Page>();
		outgoingLinks = new ArrayList<Page>();
		attributeValues = new AttributeValues();
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
	public int getDepth(){
		return depth;
	}
	public HtmlPage getHtmlPage() {
		return htmlPage;
	}
	void setHtmlPage(HtmlPage htmlPage){
		this.htmlPage = htmlPage;
	}

	public AttributeValues getAttributeValues() {
		return attributeValues;
	}
}
