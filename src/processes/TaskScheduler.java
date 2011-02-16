package processes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;



import processes.tasks.ScheduledTask;
import processes.tasks.Task;
import utils.Utils;

public class TaskScheduler {
	private static Map<Task, ScheduledFuture> taskFutureMap;
	private static TaskScheduler taskScheduler;
	private Logger logger;
	
	
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
    	this.logger = Utils.createLogger("scheduler");
		this.scheduler = Executors.newScheduledThreadPool(poolSize);
		this.taskFutureMap = new HashMap<Task, ScheduledFuture>();
	}
    public void scheduleTask(ScheduledTask task){
    	scheduleTask(task,task.getSecondsToTask());
    }
    
    
    public void scheduleTask(Task task,long timeInSeconds){
    	logger.info("Scheduled "+ task.getClass().getSimpleName()+ " in "+timeInSeconds);
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
    
    
	private class DoLater implements Runnable {
    	Task t;
    	public DoLater(Task t) {
			this.t = t;
		}
		@Override
		public void run() {
			logger.info("Executing task: "+ t.getClass().getSimpleName());
			TaskExecutor.getInstance().queueTask(t);
			taskFutureMap.remove(t);
		}
	}
}
