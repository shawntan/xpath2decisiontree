package utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;



public class PseudoWebResponse implements WebResponse {

	private InputStream inputStream;
	private HttpMethod requestMethod = HttpMethod.GET;
	private WebRequestSettings requestSettings;
	private URL url;
	
	public PseudoWebResponse(String url,
			InputStream inputStream) throws MalformedURLException {
		super();
		this.url = new URL(url);
		this.requestSettings = new WebRequestSettings(this.url);
		this.inputStream = inputStream;
	}

	@Override
	public byte[] getContentAsBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getContentAsStream() throws IOException {
		return inputStream;
	}

	@Override
	public String getContentAsString() {
		StringBuffer sb = new StringBuffer();
		try {
			int c = inputStream.read();
			while(c>0) {
				sb.append((char)c);
				c = inputStream.read();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	@Override
	public String getContentAsString(String encoding) {
		return getContentAsString();
	}

	@Override
	public String getContentCharset() {

		return "UTF-8";
	}

	@Override
	public String getContentCharSet() {
		return null;
	}

	@Override
	public String getContentCharsetOrNull() {
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public long getLoadTime() {
		return 0;
	}

	@Override
	public HttpMethod getRequestMethod() {
		return requestMethod;
	}

	@Override
	public WebRequestSettings getRequestSettings() {
		return requestSettings;
	}

	@Override
	public URL getRequestUrl() {
		return url;
	}

	@Override
	public List<NameValuePair> getResponseHeaders() {
		return null;
	}

	@Override
	public String getResponseHeaderValue(String headerName) {
		return null;
	}

	@Override
	public int getStatusCode() {
		return 200;
	}

	@Override
	public String getStatusMessage() {
		return null;
	}
	
}