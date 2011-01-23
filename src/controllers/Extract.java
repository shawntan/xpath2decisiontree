package controllers;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import beans.Extractor;

import main.Application;

import processes.TaskExecutor;
import processes.tasks.extraction.ScheduledScrape;
import processes.tasks.extraction.Scrape;
import utils.DataAccess;

public class Extract{
	public void extract(Map<String,String> request, PrintStream out) {
		int extractorId = Integer.parseInt(request.get("id"));
		ScheduledScrape.rescheduleScrape(extractorId);
	}
	
	public void extractNow(Map<String,String> request, PrintStream out) {
		int extractorId = Integer.parseInt(request.get("id"));
		Extractor e = DataAccess.retrieveExtractor(extractorId);
		TaskExecutor.getInstance().queueTask(new Scrape(e));
	}
}
