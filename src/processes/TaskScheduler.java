package processes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
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
			taskFutureMap.remove(t);
		}
	}
	private static Map<Task, ScheduledFuture> taskFutureMap;
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
		taskFutureMap = new HashMap<Task, ScheduledFuture>();
	}
    public void scheduleTask(ScheduledTask task){
    	scheduleTask(task,task.getSecondsToTask());
    }
    
    
    public void scheduleTask(Task task,long timeInSeconds){
    	System.out.println("scheduled "+ timeInSeconds);
    	ScheduledFuture future = scheduler.schedule(
    			new DoLater(task),
    			timeInSeconds,
    			TimeUnit.SECONDS
    		);
    	taskFutureMap.put(task,future);
    }
    public void cancelTask(Task task) {
    	ScheduledFuture sf = taskFutureMap.get(task);
    	if(sf!=null) sf.cancel(false);
    }
    
}
