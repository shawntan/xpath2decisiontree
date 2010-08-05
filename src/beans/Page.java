package beans;


import java.util.Date;

public class Page {
	private int id;
	private String title;
	private Date updatedAt;
	private String url;
	public int getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public String getUrl() {
		return url;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getUrl();
	}
}
