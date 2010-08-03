package extractors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;

import org.w3c.dom.Node;

import weka.core.Instance;

import com.gargoylesoftware.htmlunit.html.DomNode;


public class ExtractedData extends HashMap<String, Serializable>{
	final private static String BODY = "body";
	final private static String NEXT = "next";
	final private static String PARENT = "parent";
	final private static String PREV = "previous";

	private int attributeSize;
	private AttributeValues attributeValues;
	private ExtractedData bodyData;
	private int flatAttributeSize;
	private ExtractedData parentData;
	private DomNode node;
	


	public DomNode getNode() {
		return node;
	}
	public void setNode(DomNode node) {
		this.node = node;
	}
	public ExtractedData(AttributeValues attributeValues) {
		this.attributeValues = attributeValues;
	}
	public int getAttributeSize() {
		return attributeSize;
	}
	public ExtractedData getBodyData(){
		return this.bodyData;
	}
	public int getInteger(String key){
		return (Integer)get(key);
	}
	public ExtractedData getParentData(){
		return this.parentData;
	}
	public String getString(String key){
		return get(key).toString();
	}
	public Serializable put(String key,Serializable value) {
		attributeValues.put(key, value);
		Serializable prev=null;
		prev = super.put(key, value);
		if(prev==null) {
			if(value instanceof ExtractedData) {
				attributeSize += ((ExtractedData) value).flatAttributeSize;
			} else {
				attributeSize+=1;
				flatAttributeSize+=1;
			}
		}
		return prev;
	}

	public void setBodyData(ExtractedData bodyData) {
		this.bodyData = bodyData;
		put(BODY,bodyData);
	}
	public void setNextData(ExtractedData nextData){
		put(NEXT, nextData);
	}
	public void setParentData(ExtractedData parentData){
		this.parentData = parentData;
		put(PARENT, parentData);
	}
	public void setPreviousData(ExtractedData previousData){
		put(PREV, previousData);
	}
}
