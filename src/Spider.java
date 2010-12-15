
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import spider.Crawler;
import spider.Page;

public class Spider {

	public static void main(String[] args) {
		Crawler c = new Crawler();
		try {

			String url		= 	"http://www.straitstimes.com/BreakingNews/Breaking_News_Top_Stories_20101215.html";
			
			String[] xpaths = new String[]{
					
					"//div[@id='basecolour_bn']/table[1]/tbody[1]/tr[1]/td[2]/div[1]/div[3]/div[2]//a[1]"
			
			};
			
			String[] labels = new String[]{
					"headlines"
			};
			
			c.startCrawl(url,labels,xpaths,3);

/*
			Learner learner = new Learner(labels,xpaths);
			Page samplePage = null;
			int count = 0;
			ElementClassifier classifier = null;
			boolean onlyPositive = false;
			
			for(Page p: pages) {
				if(p.isWanted()){
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

		*/
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
