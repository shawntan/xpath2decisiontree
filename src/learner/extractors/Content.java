package learner.extractors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.html.DomNode;

public class Content extends Extractor {
	final static String NUMERICAL_PREFIX = "numericalOccurences";
	final static Pattern numericalPattern = Pattern.compile("^-{0,1}\\d*\\.{0,1}\\d+$");
	final static Pattern tokenPattern = Pattern.compile("^[a-zA-Z]{4,}$");
	@Override
	public void extractFeature(HashMap<String, Serializable> data,DomNode target) {
		String targetText = target.asText()
		.toLowerCase()
		.replaceAll("[^a-z0-9\\s]", " ")
		.replaceAll("\\n|\\r", " ")
		;
		processText(targetText);
		put("tag",target);
	}
	private void processText(String text){
		String[] tokens = text.split("\\s+");
		String token;
		put("tokens",tokens.length);
		for(int i=0;i<tokens.length;i++){
			token = tokens[i];
			String key;
			Serializable prev;
			if(numericalPattern.matcher(token).matches()){
				key = NUMERICAL_PREFIX;
			} 
			else if(tokenPattern.matcher(token).matches()){
				key = "frequency"+"."+token;
				String positionKey = "position"+"."+token;
				put(positionKey,i);
			}
			else continue;
			
			prev = get(key);
			if(prev==null) {
				prev = 1;
			} else {
				prev = (Integer)prev + 1;
			}
			put(key,prev);

		}

	}

}
