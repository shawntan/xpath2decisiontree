package learner;

import com.gargoylesoftware.htmlunit.html.DomNode;

public interface ClassifiedTask {
	public void performTask(int label,DomNode element);
}
