package processes;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import main.Application;
import processes.tasks.ScheduledTask;
import processes.tasks.Task;

public class TaskScheduler {
	private class DoLater implements Runnable {
    	Task t;
    	public DoLater(Task t) {
			this.t = t;
		}
		@Override
		public void run() {
			TaskExecutor.getInstance().queueTask(t);
		}
	}
	private static TaskScheduler taskScheduler;
	public static TaskScheduler getInstance() {
    	return getInstance(1);
    }
	public static TaskScheduler getInstance(int poolSize) {
    	if(taskScheduler==null) {
    		taskScheduler = new TaskScheduler(poolSize);
    	}
    	return taskScheduler;
    }
    private Connection dbConnection;
    private ScheduledExecutorService scheduler;
    
    private TaskScheduler(int poolSize) {
		scheduler = Executors.newScheduledThreadPool(poolSize);
		try {
			dbConnection = Application.getDataSource().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
}
