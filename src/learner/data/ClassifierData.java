package learner.data;

import java.io.Serializable;

import com.gargoylesoftware.htmlunit.html.DomNode;

public class ClassifierData extends AbstractData {
	protected DomNode node;
	public DomNode getNode() {
		return node;
	}
	@Override
	public Serializable put(String key,Serializable value) {
		if(value instanceof AbstractData ) return super.put(key, value);
		else if(attributeValues.containsKey(key)) return super.put(key, value);
		else return null;
	}
	public void setNode(DomNode node) {
		this.node = node;
	}
}
