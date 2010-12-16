package learner.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import learner.FeatureExtractor;

import weka.core.Attribute;

import com.gargoylesoftware.htmlunit.html.DomNode;


public class AttributeValues extends HashMap<String, AttributeValues.AttributeValuePair> {
	ArrayList<Attribute> attributeList;
	private String[] labels;
	
	protected class AttributeValuePair implements Serializable {
		Attribute attribute;
		int index;
		List<String> values;
		@Override
		public String toString() {
			if(attribute!= null) return attribute.toString();
			else if(values!=null) return values.toString();
			else return "NUMERIC";
		}
	}
	final static public ArrayList<String>  booleanValues = new ArrayList<String>();
	private static final long serialVersionUID = 1L;

	final static public List<String> tagNames = new ArrayList<String>();
	static {
		tagNames.add("body");
	}

	static {
		booleanValues.add(Boolean.TRUE.toString());
		booleanValues.add(Boolean.FALSE.toString());
	}
	public static String stringify (Serializable value) {
		if(value instanceof DomNode) {
			return ((DomNode)value).getLocalName().toLowerCase();
		}
		else if (value instanceof Boolean) {
			return value.toString();
		}
		else if (value instanceof String) {
			return (String)value;
		}
		return null;
	}





	public AttributeValues(String[] labels) {
		List<String> values = new ArrayList<String>(labels.length+1);
		Collections.addAll(values,labels);
		values.add(FeatureExtractor.CLASS_ATTRIBUTE_NIL_VALUE);
		System.out.println(values.get(values.size()-1));
		AttributeValuePair avp = new AttributeValuePair();
		avp.values = values;
		super.put(FeatureExtractor.CLASS_ATTRIBUTE,avp);
		this.labels = labels;
 	}



	private void addWithNoRepeat(List<String> values,String value) {
		if(values==null || value == null) return;
		if(!values.contains(value)) values.add(value);
	}



	private List<String> createAppropriateList(Serializable value) {
		List<String> result;
		if(value instanceof DomNode) {
			result = AttributeValues.tagNames;
		}
		else if(value instanceof Boolean) {
			result =  booleanValues;
		}
		else if (value instanceof String) { //future implementations of other strings.
			result = new ArrayList<String>();
		}
		else {
			result = null;
		}
		return result;
	}

	private Attribute createNewAttribute(String attributeName,List<String> values){
		if(values==null) {
			return new Attribute(attributeName);
		} else {
			return new Attribute(attributeName,values);
		}
	}

	private void expandSubData(String key,HashMap<String, Serializable> data) {
		Set<Map.Entry<String, Serializable>> subData = data.entrySet();
		for(Map.Entry<String, Serializable> entry: subData) {
			if(entry.getValue() instanceof LearnerData) continue;
			String fullKey = key + "." + entry.getKey();
			List<String> attributeValues = null;
			if(containsKey(fullKey)) {
				attributeValues = super.get(fullKey).values;
			} else  {
				if(super.get(entry.getKey())==null) System.out.println(entry.getKey()); 
				attributeValues = super.get(entry.getKey()).values;
				AttributeValuePair avp = new AttributeValuePair();
				avp.values = attributeValues;
				super.put(fullKey, avp);
			}
			addWithNoRepeat(attributeValues, stringify(entry.getValue()));
		}
	}
	public Attribute getAttribute(String key) {
		AttributeValuePair value = super.get(key);
		if(value!=null){
			return value.attribute;
		}
		else {
			return null;
		}
	}

	public ArrayList<Attribute> getAttributeList() {
		if(attributeList==null) {
			attributeList = new ArrayList<Attribute>();
			Set<Map.Entry<String, AttributeValuePair>> values = super.entrySet();
			Iterator<Map.Entry<String, AttributeValuePair>> entries = values.iterator();
			boolean isDone = false;
			while(!isDone){
				isDone=true;
				List<String> valueToLookFor =null;
				Map.Entry<String, AttributeValuePair> entry = null;
				String key = null;
				AttributeValuePair avp = null;
				while(entries.hasNext()) {
					//System.out.println("Searching for empty attribute...");
					entry = entries.next();
					key = entry.getKey();
					avp = entry.getValue();
					if(avp.attribute==null) {
						//System.out.println("Found!" + ((avp.values==null)?"numeric value":avp.values.toString()));
						valueToLookFor = avp.values;
						isDone = false;
						break;
					}
				}
				if(isDone){
					//System.out.println("Its DONE! Breakout.");
					break;
				}
				if(entry == null) return null;
				//System.out.println("Create master attribute");
				Attribute masterAttribute = createNewAttribute(key, valueToLookFor);
				avp.attribute = masterAttribute;
				attributeList.add(avp.attribute);
				//System.out.println("Look for more...");
				while(entries.hasNext()){
					entry = entries.next();
					key = entry.getKey();
					avp = entry.getValue();
					if(avp.values == valueToLookFor) {
						avp.attribute = masterAttribute.copy(key);
						//System.out.println("COPY!!");
						avp.index = attributeList.size();
						attributeList.add(avp.attribute);
					}
				}
				entries = values.iterator();
			}
		}
		return attributeList;
	}
	public int getIndex(String key) {
		AttributeValuePair value = super.get(key);
		if(value!=null){
			return value.index;
		}
		else {
			return -1;
		}
	}
	public void put(String key, Serializable value) {
		if(value instanceof LearnerData)  expandSubData(key,(HashMap<String, Serializable>) value);
		else {
			AttributeValuePair avp = super.get(key);
			if(avp == null) {
				List<String> values = createAppropriateList(value);
				avp = new AttributeValuePair();
				avp.values = values;
				super.put(key, avp);
			}
			addWithNoRepeat(avp.values, stringify(value));
		}
	}





	public String[] getLabels() {
		return labels;
	}

}

