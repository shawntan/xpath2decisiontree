package utils;
import java.util.NoSuchElementException;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;
import org.w3c.css.sac.ErrorHandler;
import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;

public class WebClientFactory extends BasePoolableObjectFactory {
	private AjaxController ajaxController;
	private ErrorHandler cssErrorHandler;
	private static ObjectPool webClientPool;
	public WebClientFactory() {
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		
		this.ajaxController = new NicelyResynchronizingAjaxController();
		this.cssErrorHandler = new SilentCssErrorHandler();
	}
	
	public Object makeObject() throws Exception {
		BrowserVersion version = new BrowserVersion(
				"Mozilla",
				"5.0 (X11; en-US)",
				"Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101211 Firefox/3.5.2",
				5.0f
		);
		WebClient webClient = new WebClient(version);
		webClient.setAjaxController(ajaxController);
		webClient.setJavaScriptEnabled(false);
		webClient.setCssErrorHandler(cssErrorHandler);
		webClient.setThrowExceptionOnScriptError(false);
		webClient.setTimeout(10000);
		webClient.setHTMLParserListener(null);
		return webClient;
	}
	@Override
	public void passivateObject(Object obj) throws Exception {
		WebClient wc = (WebClient) obj;
		wc.closeAllWindows();
		wc.getCache().clear();
		super.passivateObject(obj);
	}
	public static WebClient borrowClient() {
		if(webClientPool == null) webClientPool = new StackObjectPool(new WebClientFactory());
		try {
			return (WebClient)webClientPool.borrowObject();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void returnClient(WebClient c) {
		try {
			webClientPool.returnObject(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
