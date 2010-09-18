package learner;

import java.util.LinkedList;
import java.util.List;

import learner.data.AttributeValues;
import learner.data.LearnerData;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import com.gargoylesoftware.htmlunit.html.HtmlPage;



public class Learner {
	private AttributeValues attributeValues;
	private List<LearnerData> extractedDataMaps;
	private FeatureExtractor<LearnerData> featureExtractor;
	
	public Learner() {
		attributeValues = new AttributeValues();
		featureExtractor = new FeatureExtractor<LearnerData>(attributeValues);
	}

	public ElementClassifier createClassifier(){
		Instances trainingSet = featureExtractor.createTrainingSet("Learning...", extractedDataMaps);
		J48 cModel = new J48();
		try {
			cModel.buildClassifier(trainingSet);
			System.out.println(cModel.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ElementClassifier(cModel, attributeValues);
	}
	


	
	public void feedTrainingData(HtmlPage page, String xpath) {
		if(extractedDataMaps == null) extractedDataMaps = new LinkedList<LearnerData>();
		featureExtractor.extractFromHtmlPage(extractedDataMaps,page, xpath);
	}

	public AttributeValues getAttributeValues() {
		return attributeValues;
	}
}
