package processes;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


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
    private ScheduledExecutorService scheduler;
    
    private TaskScheduler(int poolSize) {
		scheduler = Executors.newScheduledThreadPool(poolSize);
	}
    public void scheduleTask(ScheduledTask task){
    	scheduleTask(task,task.getSecondsToTask());
    }
    
    
    public void scheduleTask(Task task,long timeInSeconds){
    	System.out.println("scheduled "+ timeInSeconds);
    	scheduler.schedule(
    			new DoLater(task),
    			timeInSeconds,
    			TimeUnit.SECONDS
    		);
    }
}
