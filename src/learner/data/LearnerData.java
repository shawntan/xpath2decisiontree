package learner.data;

import java.io.Serializable;


public class LearnerData extends AbstractData {
	@Override
	public Serializable put(String key,Serializable value) {
		super.attributeValues.put(key, value);
		return super.put(key, value);
	}
}
