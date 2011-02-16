package utils;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import learner.data.LearnerData;



public class Utils {
	final private static Formatter logFormatter = new SimpleFormatter();
	private static File logDir;
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
	public static Logger createLogger(String subsystemName) {
		Logger l = Logger.getLogger(subsystemName);
		String parent = "log";
		String filename = subsystemName+".log"; 
		for(int i=0;i<1;i++) {
			try {
				FileHandler fh = new FileHandler(parent+"/"+filename,true);
				fh.setFormatter(logFormatter);
				l.addHandler(fh);
				break;
			} catch (SecurityException e) {
				break;
			} catch (IOException e) {
				if(logDir == null){
					logDir = new File("log");
					if(!logDir.exists()) logDir.mkdir();
				}
				new File(logDir,filename);
			}
		}
		return l;
	}

}
