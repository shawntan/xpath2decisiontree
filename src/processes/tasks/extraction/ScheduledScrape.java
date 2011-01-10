package processes.tasks.extraction;


import java.util.Calendar;


import beans.Extractor;

import processes.TaskScheduler;
import processes.tasks.ScheduledTask;




public class ScheduledScrape extends Scrape implements ScheduledTask {
	private long secondsToTask;
	private long timeBetweenUpdates = 24*60*60*1000;

	public ScheduledScrape(Extractor extractor) {
		super(extractor);
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


}
