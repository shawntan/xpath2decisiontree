package learner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import learner.data.AbstractData;
import learner.data.AttributeValues;
import learner.data.ClassifierData;
import learner.data.LearnerData;
import learner.extractors.Content;
import learner.extractors.Extractor;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;




public class FeatureExtractor<T extends AbstractData>{
	public static final String CLASS_ATTRIBUTE = "wanted";
	private Class<T> classObj;
	private Extractor[] extractors;
	private AttributeValues globalAttributes;

	public FeatureExtractor(AttributeValues attributeValues) {
		globalAttributes = attributeValues;
		extractors = new Extractor[]{
				new Content()
		};
	}

	private T createData(DomNode node){
		try {
			AbstractData data =classObj.newInstance();
			data.setAttributeValues(globalAttributes);
			try {
				((ClassifierData)data).setNode(node);			
			} catch (ClassCastException e){
			} finally {
				return (T)data;
			}
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}

	public Instances createTrainingSet(String name,List<T> extractedDataMaps) {
		
		ArrayList<Attribute> attributeList =  globalAttributes.getAttributeList();
		System.out.println("No. of Attributes: "+attributeList.size());
		Instances trainingSet = new Instances(
				"Training Data",
				attributeList,
				extractedDataMaps.size()
		);
		trainingSet.setClass(globalAttributes.getAttribute(FeatureExtractor.CLASS_ATTRIBUTE));
		for(AbstractData data: extractedDataMaps) {
			Instance instance = new SparseInstance(data.getAttributeSize());
			fillInstanceWithData(instance,attributeList, null, data);
			trainingSet.add(instance);
			
		}
		return trainingSet;
	}
	private void extractFeatures(HashMap<String, Serializable> data, DomNode n){
		for(Extractor e: extractors){
			e.setResults(data);
			e.extractFeature(data, n);
		}
	}
	private void extractFromDomNode(List<T> extractedDataMaps,T data, DomNode node,List<DomNode> selected, boolean onlyPositive) {
		extractFeatures(data,node);
		boolean isSelected = false;
		if(selected!=null) {
			isSelected = selected.contains(node);
			data.put(CLASS_ATTRIBUTE, isSelected);
		}
		Iterable<DomNode> nodes = node.getChildNodes();
		AbstractData parentData = data;
		AbstractData previousData = null;
		if(nodes != null) {
			for(DomNode n : nodes) {
				if(n.getLocalName()==null||!n.isDisplayed()) continue;
				T currentData = createData(n);
				currentData.setParentData(parentData);
				currentData.setBodyData(parentData.getBodyData());
				extractFromDomNode(extractedDataMaps,currentData,n,selected, onlyPositive);
				if(previousData != null) {
					previousData.setNextData(currentData);
					currentData.setPreviousData(previousData);
				}
				previousData = currentData;
			}
		}
		if(onlyPositive) {
			if(isSelected) extractedDataMaps.add(data); 
		} else {
			extractedDataMaps.add(data);
		}
		
	}
	@SuppressWarnings("unchecked")
	public void extractFromHtmlPage(List<T> extractedDataMaps,HtmlPage page) {
		classObj = (Class<T>)ClassifierData.class;
		List<DomNode> selected= null;
		DomNode body = page.getBody();
		T data = (T)new ClassifierData() {
			@Override
			public AbstractData getBodyData() {
				return this;
			}
		};
		((ClassifierData)data).setNode(body);	
		data.setAttributeValues(globalAttributes);
		extractFromDomNode(extractedDataMaps,data,body,null,false);
		
	}
	
	@SuppressWarnings("unchecked")
	public void extractFromHtmlPage(List<T> extractedDataMaps,HtmlPage page,String xpath, boolean onlyPositive) {
		classObj = (Class<T>)LearnerData.class;
		List<DomNode> selected= (List<DomNode>)page.getByXPath(xpath);
		System.out.println("Positive examples:"+selected.size());
		DomNode body = page.getBody();
		T data = (T)new LearnerData() {
			@Override
			public AbstractData getBodyData() {
				return this;
			}
		};
		data.setAttributeValues(globalAttributes);
		extractFromDomNode(extractedDataMaps,data,body,selected, onlyPositive);
		System.out.println("Size of dataset: "+extractedDataMaps.size());
	}
	private int fillInstanceWithData(Instance instance,ArrayList<Attribute> attributeList,String prefix,AbstractData data){
		Set<Map.Entry<String,Serializable>> entries = data.entrySet();
		int count = 0;
		for(Map.Entry<String,Serializable> entry:entries){
			String key = (prefix==null)?entry.getKey():prefix+"."+entry.getKey();
			Serializable value = entry.getValue();
			if(value instanceof AbstractData){ //don't recurse down more than 1.
				if(prefix==null){
					count += fillInstanceWithData(instance,attributeList,key,(AbstractData)value);
				}
				else continue;
			} else {
				Attribute att = globalAttributes.getAttribute(key);
				if(att== null) continue;
				else if(att.isNumeric()) {
					instance.setValue(att, ((Number)value).doubleValue());
					count++;
				}
				else {
					try {
						instance.setValue(att, AttributeValues.stringify(value));
					} catch (IllegalArgumentException e){
						//System.out.println("No such value ["+AttributeValues.stringify(value)+"] for "+att.name());
						//System.out.println("Moving along...");
					}
					count++;
				}
			}
		}
		return count;
	}

}
