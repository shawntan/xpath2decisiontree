package beans;

import java.util.Date;
import java.util.List;

public class Extractor {
	private int id;
	private int userId;
	private String domain;
	private Date updateTime;
	private String[] urls;
	private Annotation[] annotations;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String[] getUrls() {
		return urls;
	}
	public void setUrls(String[] urls) {
		this.urls = urls;
	}
	public Annotation[] getAnnotations() {
		return annotations;
	}
	public void setAnnotations(Annotation[] annotations) {
		this.annotations = annotations;
	}
}
