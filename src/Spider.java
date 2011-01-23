
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;

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

import spider.Crawler;
import spider.Page;

public class Spider {

	public static void main(String[] args) {
		Crawler c = new Crawler();
		try {
			
			String url		= 	
					"http://www.google.com.sg/search?q=HtmlUnit+logging&ie=utf-8&oe=utf-8&aq=t&rls=org.mozilla:en-US:official&client=firefox-a";
			
			WebClient client = Application.getWebClient();
			HtmlPage page = client.getPage(url);
			List<HtmlElement> scripts = (List<HtmlElement>) page.getByXPath("//script");
			for(HtmlElement s: scripts) s.getParentNode().removeChild(s);
			processUrls(page);
			File f= new File("entrypoint.html");
			
			String[] xpaths = new String[]{
					"//div[@id='ires']/ol[1]/li/div[1]/span[1]/h3[1]/a[1] ",
					"//div[@id='ires']/ol[1]/li[contains(concat(' ',@class,' '),' g ')]/div[contains(concat(' ',@class,' '),' vsc ')]/div[contains(concat(' ',@class,' '),' s ')]"
			};
			
			final String[] labels = new String[]{
					"parcels_listshow",
					"parcels_highlight"
			};
			
			
			ElementClassifier classifier = c.startCrawl(new String[]{url},labels,xpaths,3);
			FileOutputStream fos = new FileOutputStream(new File("cls.model"));
			classifier.writeToStream(fos);
			fos.close();
			
			Collection<Page> pages = c.getPages();
			int count = 0;
			
			for(Page p: pages) {
				if(p.isWanted()){
					count++;
					HtmlPage htmlPage = p.getHtmlPage();
					HtmlPage sampleHtmlPage = htmlPage;
					processUrls(sampleHtmlPage);			
					HtmlElement head = sampleHtmlPage.getElementsByTagName("head").get(0);
					HtmlElement style = sampleHtmlPage.createElement("link");
					style.setAttribute("rel", "stylesheet");
					style.setAttribute("type","text/css");
					style.setAttribute("href", "./stylesheet.css");
					head.appendChild(style);
					classifier.classifyPageElements(htmlPage,
							new ClassifiedTask() {
								public void performTask(int label, DomNode element) {
									if(label < labels.length){ 
										HtmlElement e = (HtmlElement) element;
										e.setAttribute("class",e.getAttribute("class")+" "+labels[label]);
									}
								}
					});
					try {
						FileWriter writer = new FileWriter(new File(count+".html"));
						writer.write(sampleHtmlPage.asXml());
						System.out.println("Written to file.");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FailingHttpStatusCodeException e) {
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
				e.printStackTrace();
			}
		}
	}
	public static void processUrls(HtmlPage page) {
		makeAttributeFullyQualified(page, (List<HtmlElement>)page.getByXPath("//*[@src]"), "src");
		makeAttributeFullyQualified(page, (List<HtmlElement>)page.getByXPath("//*[@href]"), "href");
	}
}
