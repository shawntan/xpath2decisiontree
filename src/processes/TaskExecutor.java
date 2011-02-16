package processes;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import processes.tasks.Task;


public class TaskExecutor {
	private static TaskExecutor taskExecutor;

    private BlockingQueue<Runnable> queue;
    private ThreadPoolExecutor threadPool;
  
	public static TaskExecutor getInstance(){
    	return getInstance(3,5,1000L);
    }
	public static TaskExecutor getInstance(int poolSize, int maxPoolSize, long keepAliveTime) {
    	if(taskExecutor==null) {
    		taskExecutor = new TaskExecutor(poolSize, maxPoolSize, keepAliveTime);
    	}
    	return taskExecutor;
    }
	
    private TaskExecutor(int poolSize, int maxPoolSize, long keepAliveTime) {
		super();

		queue = new LinkedBlockingQueue<Runnable>();
		threadPool = new ThreadPoolExecutor(
				poolSize,
				maxPoolSize,
				keepAliveTime,
				TimeUnit.DAYS,
				queue
		);
	}
    
    
    public void queueTask(Task t) {
    	threadPool.execute(t);
    }
    

}
