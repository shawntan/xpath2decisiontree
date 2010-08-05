package utils;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import learner.data.LearnerData;



public class Utils {

	public static void printTable(Map<String,Serializable> tobeprinted){
		Set<Map.Entry<String,Serializable>> e = tobeprinted.entrySet();
		System.out.println("============================================================================");
		for(Map.Entry<String, Serializable> entry: e){
			Serializable value = entry.getValue();
			if(entry == tobeprinted) value = "SELF";
			else if(value instanceof LearnerData) value = "NOT PRINTED";
			System.out.format("\t%-50s%-64s%n", entry.getKey(),value);
		}
		System.out.println("============================================================================");
	}

}
