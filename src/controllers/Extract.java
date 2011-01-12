package controllers;

import java.io.PrintStream;
import java.util.Map;

import processes.TaskExecutor;
import processes.tasks.extraction.ScheduledScrape;
import processes.tasks.extraction.Scrape;

public class Extract{
	public void extract(Map<String,String> request, PrintStream out) {
		int extractorId = Integer.parseInt(request.get("id"));
		ScheduledScrape.rescheduleScrape(extractorId);
	}
}
