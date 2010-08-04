
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;

import learner.ClassifiedTask;
import learner.ElementClassifier;
import learner.Learner;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;



public class Test {
	public static HtmlPage buildPage(String url){
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
		String xpath = properties.getProperty("xpath");
		String url2 = properties.getProperty("url2");
		
		HtmlPage page = buildPage(url1);
		Learner learner = new Learner();
		learner.feedTrainingData(page, xpath);
		
		
		HtmlPage page2  = buildPage(url2);
		HtmlElement head = page2.getElementsByTagName("head").get(0);
		HtmlElement style = page2.createElement("link");
		style.setAttribute("rel", "stylesheet");
		style.setAttribute("type","text/css");
		style.setAttribute("href", "./stylesheet.css");
		head.appendChild(style);
		
		ElementClassifier classifier = learner.createClassifier();
		classifier.classifyPageElements(page2, new ClassifiedTask() {
			public void performTask(DomNode element) {
				HtmlElement e = (HtmlElement) element;
				e.setAttribute("class",e.getAttribute("class")+" "+"parcels_listshow");
			}
		});
		
		try {
			FileWriter writer = new FileWriter(new File("output.html"));
			writer.write(page2.asXml());
			System.out.println("Written to file.");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	public static void processUrls(HtmlPage page) {
		makeAttributeFullyQualified(page, (List<HtmlElement>)page.getByXPath("//*[@src]"), "src");
		makeAttributeFullyQualified(page, (List<HtmlElement>)page.getByXPath("//*[@href]"), "href");
	}
}
