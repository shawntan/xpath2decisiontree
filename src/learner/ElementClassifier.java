package learner;


import java.util.ArrayList;

import learner.data.AttributeValues;
import learner.data.ClassifierData;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class ElementClassifier {
	private AttributeValues attributeValues;
	private Classifier classifier;
	private FeatureExtractor<ClassifierData> classifierExtractor;
	public ElementClassifier(Classifier classifier,
			AttributeValues attributeValues) {
		this.classifier = classifier;
		this.attributeValues = attributeValues;
		this.classifierExtractor =
			new FeatureExtractor<ClassifierData>(attributeValues);
	}
	public void classifyPageElements(HtmlPage page,ClassifiedTask task) {
		ArrayList<ClassifierData> dataList = new ArrayList<ClassifierData>();
		classifierExtractor.extractFromHtmlPage(dataList, page);
		Instances instances = classifierExtractor.createTrainingSet("", dataList);
		int instanceSize = instances.numInstances();
		int passCount = 0;
		try {
			for(int i=0;i<instanceSize;i++) {
				Instance instance = instances.get(i);
				task.performTask(
						(int)Math.round(classifier.classifyInstance(instance)),
						dataList.get(i).getNode()
				);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(passCount);
	}
}
