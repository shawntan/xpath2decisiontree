package spider;

import java.net.MalformedURLException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class CrawlerUtils {

	public static int editDistance(String s1, String s2){
		if(s1==null) s1 = "";
		if(s2==null) s2 = "";
		int[][] dp = new int[s1.length() + 1][s2.length() + 1];
		for (int i = 0; i < dp.length; i++) {
			for (int j = 0; j < dp[i].length; j++) {
				dp[i][j] = i == 0 ? j : j == 0 ? i : 0;
				if (i > 0 && j > 0) { 
					if (s1.charAt(i - 1) == s2.charAt(j - 1))
						dp[i][j] = dp[i - 1][j - 1];
					else
						dp[i][j] = Math.min(
							dp[i][j - 1] + 1, 		Math.min(
							dp[i - 1][j] + 1,
							dp[i - 1][j - 1] + 1));
				}
			}
		}
		return dp[s1.length()][s2.length()];
	}
	public static URL linkToUrl(HtmlAnchor a) throws MalformedURLException{
		HtmlPage page = (HtmlPage)a.getPage();
		String url = a.getHrefAttribute();
		int index = url.indexOf('#');
		if(index < 0) return page.getFullyQualifiedUrl(url);
		else {
			url = url.substring(0,index);
			return page.getFullyQualifiedUrl(url);
		}
	}
}
