package processes;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import main.Application;

import processes.tasks.ScheduledTask;
import processes.tasks.Task;

import database.Database;

public class TaskScheduler {
	private Connection dbConnection;
	private ScheduledExecutorService scheduler;
	private static TaskScheduler taskScheduler;
	private TaskScheduler(int poolSize) {
		scheduler = Executors.newScheduledThreadPool(poolSize);
		try {
			dbConnection = Application.getDataSource().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    public static TaskScheduler getInstance(int poolSize) {
    	if(taskScheduler==null) {
    		taskScheduler = new TaskScheduler(poolSize);
    	}
    	return taskScheduler;
    }
    public static TaskScheduler getInstance() {
    	return getInstance(1);
    }
    
    public void scheduleTask(ScheduledTask task){
    	scheduleTask(task,task.getSecondsToTask());
    }
    public void scheduleTask(Task task,long timeInSeconds){
    	scheduler.schedule(
    			new DoLater(task),
    			timeInSeconds,
    			TimeUnit.SECONDS
    		);
    }
    
    
    private class DoLater implements Runnable {
    	Task t;
    	public DoLater(Task t) {
			this.t = t;
		}
		public void run() {
			TaskExecutor.getInstance().queueTask(t);
		}
	}
}
