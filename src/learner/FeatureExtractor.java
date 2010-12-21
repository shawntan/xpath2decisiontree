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
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;




public class FeatureExtractor<T extends AbstractData>{
	final private  double RANDOM_PROPORTION = 0.2;
	
	public static final String CLASS_ATTRIBUTE = "wanted";
	public static final String CLASS_ATTRIBUTE_NIL_VALUE = "none";
	private Class<T> classObj;
	private Extractor[] extractors;
	private AttributeValues globalAttributes;
	private String[] labels;
	
	public FeatureExtractor(AttributeValues attributeValues) {
		globalAttributes = attributeValues;
		this.labels = attributeValues.getLabels();
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
			} 
			return (T)data;
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
	private void extractFromDomNode(List<T> extractedDataMaps,T data, DomNode node,List<HtmlElement>[] selectedItems, boolean onlyPositive) {
		extractFeatures(data,node);
		boolean isSelected = false;
		//System.out.println(selectedItems);
		if(selectedItems!=null) {
			for(int i=0;i<selectedItems.length;i++){
				if(selectedItems[i].contains(node)){
					data.put(CLASS_ATTRIBUTE, labels[i]);
					isSelected = true;
					break;
				}
			}
			if(!isSelected) data.put(CLASS_ATTRIBUTE, CLASS_ATTRIBUTE_NIL_VALUE);
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
				extractFromDomNode(
						extractedDataMaps,
						currentData,
						n,
						selectedItems,
						onlyPositive
				);
				if(previousData != null) {
					previousData.setNextData(currentData);
					currentData.setPreviousData(previousData);
				}
				previousData = currentData;
			}
		}	
		if(isSelected) {
			extractedDataMaps.add(data); 
		} else {
			//negative example
			double random = Math.random();
			if(onlyPositive) {
				if(random < RANDOM_PROPORTION)	extractedDataMaps.add(data);
			} else {
				if(random >= RANDOM_PROPORTION) extractedDataMaps.add(data); 
			}
		}
	}
	@SuppressWarnings("unchecked")
	public void extractFromHtmlPage(List<T> extractedDataMaps,HtmlPage page) {
		classObj = (Class<T>)ClassifierData.class;
		DomNode body = page.getBody();
		T data = (T)new ClassifierData() {
			private static final long serialVersionUID = 1L;
			public AbstractData getBodyData() {
				return this;
			}
		};
		((ClassifierData)data).setNode(body);	
		data.setAttributeValues(globalAttributes);
		extractFromDomNode(extractedDataMaps,data,body,null,false);

	}

	@SuppressWarnings("unchecked")
	public void extractFromHtmlPage(List<T> extractedDataMaps,HtmlPage page, List<HtmlElement>[] selectedItems, boolean onlyPositive) {
		classObj = (Class<T>)LearnerData.class;
		DomNode body = page.getBody();
		T data = (T)new LearnerData() {
			private static final long serialVersionUID = 1L;
			public AbstractData getBodyData() {
				return this;
			}
		};
		data.setAttributeValues(globalAttributes);
		extractFromDomNode(extractedDataMaps,data,body,selectedItems, onlyPositive);
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
