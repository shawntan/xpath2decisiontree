
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import processes.tasks.Task;


import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;


import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import extractors.AttributeValues;
import extractors.Content;
import extractors.ExtractedData;
import extractors.Extractor;



public class FeatureExtractor implements Task {
	public static final String CLASS_ATTRIBUTE = "wanted";
	final public static List<String> doNotRecurse = Arrays.asList(new String[]{
			"body","parent","previous","next"
	});
	public static void printTable(Map<String,Serializable> tobeprinted){
		Set<Map.Entry<String,Serializable>> e = tobeprinted.entrySet();
		System.out.println("============================================================================");
		for(Map.Entry<String, Serializable> entry: e){
			Serializable value = entry.getValue();
			if(entry == tobeprinted) value = "SELF";
			else if(value instanceof ExtractedData) value = "NOT PRINTED";
			System.out.format("\t%-50s%-64s%n", entry.getKey(),value);
		}
		System.out.println("============================================================================");
	}
	private Extractor[] extractors;
	private AttributeValues globalAttributes;
	private ArrayList<DomNode> nodes;

	public FeatureExtractor() {
		globalAttributes = new AttributeValues();
		extractors = new Extractor[]{
				new Content()
		};
	}
	public HtmlPage buildPage(String url){
		System.out.println("Downloading page...");
		WebClient client = new WebClient();
		client.setJavaScriptEnabled(false);
		client.setCssEnabled(false);
		try {
			HtmlPage page = client.getPage(url);
			System.out.println("Done.");
			return page;
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private int createAndAddInstance(Instance instance,ArrayList<Attribute> attributeList,String prefix,ExtractedData data){
		Set<Map.Entry<String,Serializable>> entries = data.entrySet();
		int count = 0;
		for(Map.Entry<String,Serializable> entry:entries){
			String key = (prefix==null)?entry.getKey():prefix+"."+entry.getKey();
			Serializable value = entry.getValue();
			if(value instanceof ExtractedData){ //don't recurse down more than 1.
				if(prefix==null){
					count += createAndAddInstance(instance,attributeList,key,(ExtractedData)value);
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
						System.out.println("No such value ["+AttributeValues.stringify(value)+"] for "+att.name());
						System.out.println("Moving along...");
					}
					
					count++;
				}
			}
		}
		return count;
	}
	public Instances createTrainingSet(List<ExtractedData> extractedDataMaps) {
		ArrayList<Attribute> attributeList =  globalAttributes.getAttributeList();
		System.out.println("No. of Attributes: "+attributeList.size());
		Instances trainingSet = new Instances(
				"Training Data",
				attributeList,
				extractedDataMaps.size()
		);
		trainingSet.setClass(globalAttributes.getAttribute(CLASS_ATTRIBUTE));
		for(ExtractedData data: extractedDataMaps) {
			Instance instance = new SparseInstance(data.getAttributeSize());
			createAndAddInstance(instance,attributeList, null, data);
			if(instance==null)System.out.println("Instance is NULL!");
			if(data.getNode()==null) System.out.println("DomNode is NULL!");
			trainingSet.add(instance);
		}
		return trainingSet;
	}
	private void extractFeatures(ExtractedData data, DomNode n){
		for(Extractor e: extractors){
			e.setResults(data);
			e.extractFeature(data, n);
		}
	}
	private void extractFromDomNode(List<ExtractedData> extractedDataMaps,ExtractedData data, DomNode node,List<DomNode> selected){
		//extract data from dom node here.
		extractFeatures(data,node);
		if(selected!=null)data.put(CLASS_ATTRIBUTE, selected.contains(node));

		Iterable<DomNode> nodes = node.getChildNodes();
		ExtractedData parentData = data;
		ExtractedData previousData = null;
		if(nodes != null) {
			for(DomNode n : nodes) {
				if(n.getLocalName()==null||!n.isDisplayed()) continue;
				ExtractedData currentData = new ExtractedData(globalAttributes);
				currentData.setParentData(parentData);
				currentData.setBodyData(parentData.getBodyData());
				extractFromDomNode(extractedDataMaps,currentData,n,selected);
				if(previousData != null) {
					previousData.setNextData(currentData);
					currentData.setPreviousData(previousData);
				}
				previousData = currentData;
			}
		}
		data.setNode(node);
		extractedDataMaps.add(data);
	}
	public List<ExtractedData> extractFromHtmlPage(HtmlPage page,String xpath) {
		List<ExtractedData> extractedDataMaps = new LinkedList<ExtractedData>();
		List<DomNode> selected= null;
		if(xpath!=null) selected= (List<DomNode>)page.getByXPath(xpath);
		DomNode body = page.getBody();
		ExtractedData data = new ExtractedData(globalAttributes) {
			public ExtractedData getBodyData() {
				return this;
			}
		};
		extractFromDomNode(extractedDataMaps,data,body,selected);
		return extractedDataMaps;
	}
	public List<ExtractedData> extractFromHtmlPage(HtmlPage page) {
		return extractFromHtmlPage(page,null);
	}

	@Override
	public Task getFollowUpActions() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isSuccessful() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
	}
}
