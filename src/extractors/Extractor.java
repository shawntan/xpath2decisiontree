package extractors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import org.w3c.dom.Node;


import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public abstract class Extractor {
	private AttributeValues attributes;
	private ExtractedData results;

	public abstract void extractFeature(
			ExtractedData data,
			DomNode target
	);
	
	protected Serializable get(String key) {
		return results.get(this.getClass().getSimpleName()+"."+key);
	}
	
	public ExtractedData getResults() {
		return this.results;
	}


	protected void put(String key, Serializable value) {
		String fullKey = this.getClass().getSimpleName()+"."+key;
		results.put(fullKey, value);
	}
	public void setAttributesTable(AttributeValues attributes){
		this.attributes = attributes;
	}
	public void setResults(ExtractedData results){
		this.results = results;
	}
}
