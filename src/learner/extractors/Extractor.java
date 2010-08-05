package learner.extractors;

import java.io.Serializable;
import java.util.HashMap;

import com.gargoylesoftware.htmlunit.html.DomNode;

public abstract class Extractor {
	private StringBuilder builder = new StringBuilder(this.getClass().getSimpleName()).append('.');
	private int prefixLength = builder.length();
	private HashMap<String, Serializable> results;
	
	public abstract void extractFeature(
			HashMap<String, Serializable> data,
			DomNode target
	);
	
	protected Serializable get(String key) {
		String fullKey = builder.append(key).toString();
		builder.delete(prefixLength, builder.length());
		return results.get(fullKey);
	}
	
	public HashMap<String, Serializable> getResults() {
		return this.results;
	}


	protected void put(String key, Serializable value) {
		String fullKey = builder.append(key).toString();
		results.put(fullKey, value);
		builder.delete(prefixLength, builder.length());
	}

	public void setResults(HashMap<String, Serializable> results){
		this.results = results;
	}
}
