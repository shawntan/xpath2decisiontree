
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serializer.DOMSerializer;
import org.apache.xml.serializer.dom3.DOM3SerializerImpl;

import learner.ClassifiedTask;
import learner.ElementClassifier;
import main.Application;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.steadystate.css.dom.Property;

import spider.Crawler;
import spider.Page;

public class SerializeTest {

	public static void main(String[] args) {
		final String[] labels = new String[]{
				"parcels_listshow",
				"parcels_highlight"
		};

		WebClient client = Application.getWebClient();
		HtmlPage sampleHtmlPage;
		try {
			sampleHtmlPage = client.getPage("http://www.google.com.sg/search?q=HtmlUnit+logging&ie=utf-8&oe=utf-8&aq=t&rls=org.mozilla:en-US:official&client=firefox-a");

			processUrls(sampleHtmlPage);			
			HtmlElement head = sampleHtmlPage.getElementsByTagName("head").get(0);
			HtmlElement style = sampleHtmlPage.createElement("link");
			style.setAttribute("rel", "stylesheet");
			style.setAttribute("type","text/css");
			style.setAttribute("href", "./stylesheet.css");
			head.appendChild(style);


			FileInputStream fis = new FileInputStream(new File("cls.model"));
			ElementClassifier classifier = ElementClassifier.readElementClassifier(fis);

			classifier.classifyPageElements(sampleHtmlPage,
					new ClassifiedTask() {
				public void performTask(int label, DomNode element) {
					if(label < labels.length){ 
						HtmlElement e = (HtmlElement) element;
						e.setAttribute("class",e.getAttribute("class")+" "+labels[label]);
					}
				}
			});


			Source source = new DOMSource(sampleHtmlPage);
			File file = new File("output.html");
			Result resultSource = new StreamResult(System.out);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.transform(source, resultSource);

		} catch (Exception e){
			e.printStackTrace();
		}


	}

	private static void makeAttributeFullyQualified(HtmlPage page,List<HtmlElement> list,String attributeName) {
		for(HtmlElement n: list){
			try {
				n.setAttribute(attributeName, page.getFullyQualifiedUrl(n.getAttribute(attributeName)).toString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
	public static void processUrls(HtmlPage page) {
		makeAttributeFullyQualified(page, (List<HtmlElement>)page.getByXPath("//*[@src]"), "src");
		makeAttributeFullyQualified(page, (List<HtmlElement>)page.getByXPath("//*[@href]"), "href");
	}
}
