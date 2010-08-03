import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import extractors.ExtractedData;


public class Test {
	public static void main(String[] args) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File("settings")));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String url1 = properties.getProperty("url1");
		String xpath = properties.getProperty("xpath");;
		String url2 = properties.getProperty("url2");;

		FeatureExtractor f = new FeatureExtractor();

		List<ExtractedData> data= f.extractFromHtmlPage(
				f.buildPage(url1),
				xpath
		);
		System.out.println("Creating training set...");
		Instances trainingSet = f.createTrainingSet(data);
		System.out.println("Training set created. Creating model...");
		J48 cModel =  new J48();
		try {
			cModel.buildClassifier(trainingSet);
			System.out.println(cModel.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		FeatureExtractor b = new FeatureExtractor();
		HtmlPage resultPage = b.buildPage(url2);
		processUrls(resultPage);
		data = b.extractFromHtmlPage(
				resultPage
		);
		trainingSet = f.createTrainingSet(data);
		int passCount = 0;
		for (int i = 0; i < trainingSet.numInstances(); i++) {
			try {
				Instance instance  = trainingSet.instance(i);
				if(cModel.classifyInstance(instance) ==0.0) {
					passCount++;
					HtmlElement node = (HtmlElement)data.get(i).getNode();
					String currentStyle = node.getAttribute("style");
					node.setAttribute("style",currentStyle+";"+ "color:red;");
					passCount++;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Got "+passCount+" hit(s)!");
		System.out.println("Writing result to file...");
		try {
			PrintWriter out = new PrintWriter(new File("output.html"));
			out.write(resultPage.asXml());
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void processUrls(HtmlPage page) {
		makeAttributeFullyQualified(page, (List<HtmlElement>)page.getByXPath("//*[@src]"), "src");
		makeAttributeFullyQualified(page, (List<HtmlElement>)page.getByXPath("//*[@href]"), "href");
	}
	private static void makeAttributeFullyQualified(HtmlPage page,List<HtmlElement> list,String attributeName) {
		for(HtmlElement n: list){
			try {
				n.setAttribute(attributeName, page.getFullyQualifiedUrl(n.getAttribute(attributeName)).toString());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
