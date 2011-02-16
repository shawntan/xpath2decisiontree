package processes.tasks.extraction;


import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.handlers.BeanHandler;

import database.DataAccess;


import beans.Extractor;

import processes.TaskScheduler;
import processes.tasks.ScheduledTask;




public class ScheduledScrape extends Scrape implements ScheduledTask {
	private static Map<Integer,ScheduledScrape> idScheduledScrapeMap = new HashMap<Integer, ScheduledScrape>();
	private static BeanHandler<Extractor> beanHandler = new BeanHandler<Extractor>(Extractor.class);
	
	private long secondsToTask;
	private long timeBetweenUpdates = 24*60*60*1000;

	public ScheduledScrape(Extractor extractor) {
		super(extractor);
		idScheduledScrapeMap.put(extractor.getId(), this);
		scheduleNextTime(false);
	}
	
	@Override
	public void run() {
		scheduleNextTime(true);
		TaskScheduler.getInstance().scheduleTask(this);
		super.run();
	}
	
	private void scheduleNextTime(boolean strictlyTommorrow) {
		Calendar scheduledTime = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		scheduledTime.setTime(extractor.getUpdateTime());
		scheduledTime.set(Calendar.YEAR, now.get(Calendar.YEAR));
		scheduledTime.set(Calendar.MONTH, now.get(Calendar.MONTH));
		scheduledTime.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
		long timeDiff = scheduledTime.getTimeInMillis() - now.getTimeInMillis();
		if(strictlyTommorrow || timeDiff<0) timeDiff = timeDiff + timeBetweenUpdates;
		now.setTimeInMillis(now.getTimeInMillis()+timeDiff);
		System.out.println(now.getTime());
		secondsToTask = timeDiff/1000L;
		System.out.println(secondsToTask);
		
	}
	
	@Override
	public long getSecondsToTask() {
		return secondsToTask;
	}

	
	private static boolean cancelScheduledScrape(ScheduledScrape s) {
		if(s!=null) {
			TaskScheduler.getInstance().cancelTask(s);
			return true;
		} else return false;
	}
	
	public static void rescheduleScrape(int extractorId) {
		ScheduledScrape s= idScheduledScrapeMap.get(extractorId);
		if(s!=null) {
			cancelScheduledScrape(s);
			try {
				s.extractor = s.queryRunner.query(
						"SELECT id,domain, update_time as updateTime FROM extractors WHERE id = ?",
						beanHandler,
						extractorId
				);
			} catch (SQLException e) {e.printStackTrace();}

		} else {
			s = new ScheduledScrape(DataAccess.retrieveExtractor(extractorId));
		}
		s.scheduleNextTime(false);
		TaskScheduler.getInstance().scheduleTask(s);
	}
}
