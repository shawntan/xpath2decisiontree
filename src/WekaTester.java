import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToNominal;


public class WekaTester {
	final private static String SUNNY = "sunny";
	final private static String OVERCAST = "overcast";
	final private static String RAINY = "rainy";
	
	public static void main(String[] args) {
		String T = Boolean.TRUE.toString();
		String F = Boolean.FALSE.toString();
		
		ArrayList<String> outlookValues = new ArrayList<String>(3);
		outlookValues.add(SUNNY);
		outlookValues.add(OVERCAST);
		outlookValues.add(RAINY);
		
		ArrayList<String> windyValues = new ArrayList<String>(2);
		windyValues.add(T);
		windyValues.add(F);	
		
		
		Attribute outlook = new Attribute("outlook",outlookValues);
		Attribute temperature = new Attribute("temperature");
		Attribute humidity = new Attribute("humidity");
		Attribute windy = new Attribute("windy",windyValues);
		windyValues.add("shit");
		Attribute play = windy.copy("play");
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(outlook);
		attributes.add(temperature);
		attributes.add(humidity);
		attributes.add(windy);
		attributes.add(play);
		HashMap<String, Attribute> attributeMap = new HashMap<String, Attribute>();
		attributeMap.put("outlook",outlook);
		attributeMap.put("temperature",temperature);
		attributeMap.put("humidity",humidity);
		attributeMap.put("windy", windy);
		attributeMap.put("play", play);
		
		Instances trainingSet = new Instances("Rel", attributes, 14);
		Collection<Attribute> values = attributeMap.values();
		for(Attribute a:values){
			System.out.println(a.index());
		}
		System.out.println(trainingSet);
		
		trainingSet.setClass(play);
		Object[][] data = {
			{SUNNY,		85,	85,		F,		F},
			{SUNNY,		80,	90,		T,		F},
			{OVERCAST,	83,	90,		F,		T},
			{RAINY,		70,	96,		F,		T},
			{RAINY,		68,	80,		F,		T},
			{RAINY,		65,	70,		T,		F},
			{OVERCAST,	64,	65,		T,		T},
			{SUNNY,		72,	95,		F,		F},
			{SUNNY,		69,	70,		F,		T},
			{RAINY,		75,	80,		F,		T},
			{SUNNY,		75,	70,		T,		T},
			{OVERCAST,	72,	90,		T,		T},
			{OVERCAST,	81,	75,		F,		T},
			{RAINY,		71,	91,		T,		F}
		};
		
		for(int i=0;i<data.length;i++){
			Instance instance = new DenseInstance(data[i].length);
			for(int j=0;j<data[i].length;j++){
				if(data[i][j] instanceof Number){
					instance.setValue(attributes.get(j), ((Number)data[i][j]).doubleValue());
				} else instance.setValue(attributes.get(j), data[i][j].toString());
			}
			trainingSet.add(instance);
		}
		
		J48 cModel =  new J48();
		try {
			cModel.buildClassifier(trainingSet);
			System.out.println(cModel.toString());
			System.out.println(cModel.toSummaryString());
			Evaluation eTest = new Evaluation(trainingSet);
			eTest.evaluateModel(cModel, trainingSet);
			System.out.println(eTest.toClassDetailsString());
			System.out.println(eTest.toMatrixString());
			System.out.println(eTest.toSummaryString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

}
