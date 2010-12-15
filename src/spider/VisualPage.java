package spider;

import java.net.URL;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class VisualPage extends Page {

	private Graph<Page,DefaultEdge> g;

	VisualPage(URL url, int depth,Graph<Page,DefaultEdge> g) {
		super(url, depth);
		g.addVertex(this);
		this.g = g;
	}

	public boolean addToOutgoingLinks(Page page) {
		// TODO Auto-generated method stub
		g.addEdge(this, page);
		return super.addToOutgoingLinks(page);
	}
	@Override
	public void removeFromIncomingLinks(Page page) {
		// TODO Auto-generated method stub
		//g.removeAllEdges(page,this);
		super.removeFromIncomingLinks(page);
	}
	public void removeFromOutgoingLinks(Page page){
		//g.removeAllEdges(this, page);
		super.removeFromOutgoingLinks(page);
	}

	void setHtmlPage(HtmlPage htmlPage) {
		super.setHtmlPage(htmlPage);
	}
	@Override
	public String toString() {
		String name = this.getUrl().getPath();
		if(this.getUrl().getQuery()!=null) name += "?"+this.getUrl().getQuery();
		return name;
	}
	@Override
	public void removePage() {
		// TODO Auto-generated method stub
		super.removePage();
		g.removeVertex(this);
		
		
	}

}
