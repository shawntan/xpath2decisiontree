package learner.data;

import java.io.Serializable;
import java.util.HashMap;

public abstract class AbstractData extends HashMap<String, Serializable> {
	private static final String BODY = "body";
	private static final String NEXT = "next";
	private static final String PARENT = "parent";
	private static final String PREV = "previous";
	protected int attributeSize;
	protected AttributeValues attributeValues;


	private AbstractData bodyData;
	protected int flatAttributeSize;
	private AbstractData parentData;
	public int getAttributeSize() {
		return attributeSize;
	}

	public AbstractData getBodyData() {
		return this.bodyData;
	}

	public int getInteger(String key) {
		return (Integer)get(key);
	}
	public AbstractData getParentData() {
		return this.parentData;
	}

	public String getString(String key) {
		return get(key).toString();
	}
	@Override
	public Serializable put(String key,Serializable value) {
		Serializable prev = super.put(key, value);
		if(prev==null) {
			if(value instanceof AbstractData) {
				attributeSize += ((AbstractData) value).flatAttributeSize;
			} else {
				attributeSize+=1;
				flatAttributeSize+=1;
			}
		}
		return prev;
	}

	public void setAttributeValues(AttributeValues attributeValues) {
		this.attributeValues = attributeValues;
	}

	public void setBodyData(AbstractData bodyData) {
		this.bodyData = bodyData;
		put(BODY,bodyData);
	}

	public void setNextData(AbstractData nextData) {
		put(NEXT, nextData);
	}

	public void setParentData(AbstractData parentData) {
		this.parentData = parentData;
		put(PARENT, parentData);
	}

	public void setPreviousData(AbstractData previousData) {
		put(PREV, previousData);
	}

}