package learner;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import learner.data.AttributeValues;
import learner.data.ClassifierData;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class ElementClassifier implements Serializable  {
	private AttributeValues attributeValues;
	private Classifier classifier;
	private transient FeatureExtractor<ClassifierData> classifierExtractor;
	
	public ElementClassifier(Classifier classifier, AttributeValues attributeValues) {
		this.classifier = classifier;
		this.attributeValues = attributeValues;
		this.classifierExtractor =
			new FeatureExtractor<ClassifierData>(attributeValues);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return classifier.toString();
	}
	public void writeToStream(OutputStream os) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(this);
	}
	public static ElementClassifier readElementClassifier(InputStream is) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(is);
		ElementClassifier learner = (ElementClassifier)ois.readObject();
		learner.classifierExtractor = new FeatureExtractor<ClassifierData>(learner.attributeValues);
		return learner;
	}
	
	public void classifyPageElements(HtmlPage page,ClassifiedTask task) {
		ArrayList<ClassifierData> dataList = new ArrayList<ClassifierData>();
		classifierExtractor.extractFromHtmlPage(dataList, page);
		Instances instances = classifierExtractor.createTrainingSet("", dataList);
		int instanceSize = instances.numInstances();
		
		try {
			for(int i=0;i<instanceSize;i++) {
				Instance instance = instances.get(i);
				task.performTask(
						(int)Math.round(classifier.classifyInstance(instance)),
						dataList.get(i).getNode()
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
