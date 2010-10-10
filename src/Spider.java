

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;

import learner.ClassifiedTask;
import learner.ElementClassifier;
import learner.Learner;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import spider.Crawler;
import spider.Page;

public class Spider {

	public static void main(String[] args) {
		Crawler c = new Crawler();
		try {

			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("save.file")));
			} catch (Exception e) {
				c.crawl("http://www.google.com.sg/search?q=in+my+skin",3);
				try {
					ObjectOutputStream objOut  = new ObjectOutputStream(new FileOutputStream(new File("save.file")));
					objOut.writeObject(c);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			String xpath = "//div[@id='ires']/ol[1]/li[contains(concat(' ',@class,' '),' g ')]/h3[contains(concat(' ',@class,' '),' r ')]/a[contains(concat(' ',@class,' '),' l ')]";
			Page popular = c.getMostIncomingLinks();
			Collection<Page> pages = c.getPages();

			System.out.println("Highest LINKS!: "+popular.getUrl());
			Learner learner = new Learner();

			Page samplePage = null;
			int count = 0;
			ElementClassifier classifier = null;
			for(Page p: pages) {
				HtmlPage htmlPage = p.getHtmlPage();
				if(htmlPage!=null){
					if(count <= 10 && !htmlPage.getByXPath(xpath).isEmpty()){
						if(classifier== null) {
							learner.feedTrainingData(htmlPage, xpath);
							classifier = learner.createClassifier();
						}
						else {
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
								public void performTask(DomNode element) {
									HtmlElement e = (HtmlElement) element;
									e.setAttribute("class",e.getAttribute("class")+" "+"parcels_listshow");
								}
							}
							);
							
							try {
								FileWriter writer = new FileWriter(new File(count+".html"));
								writer.write(sampleHtmlPage.asXml());
								count++;
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
				}

			}






		} catch (MalformedURLException e) {
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
