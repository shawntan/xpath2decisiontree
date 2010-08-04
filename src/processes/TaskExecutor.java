package processes;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import processes.tasks.Task;


public class TaskExecutor {
	private static TaskExecutor taskExecutor;
	public static TaskExecutor getInstance(){
    	return getInstance(3,5,1000L);
    }
	public static TaskExecutor getInstance(int poolSize, int maxPoolSize, long keepAliveTime) {
    	if(taskExecutor==null) {
    		taskExecutor = new TaskExecutor(poolSize, maxPoolSize, keepAliveTime);
    	}
    	return taskExecutor;
    }
	private long keepAliveTime;
    private int maxPoolSize;
    private int poolSize;
    
    private BlockingQueue<Runnable> queue;
    private ThreadPoolExecutor threadPool;
  
    private TaskExecutor(int poolSize, int maxPoolSize, long keepAliveTime) {
		super();
		this.poolSize = poolSize;
		this.maxPoolSize = maxPoolSize;
		this.keepAliveTime = keepAliveTime;
		queue = new LinkedBlockingQueue<Runnable>();
		
		threadPool = new ThreadPoolExecutor(
				poolSize,
				maxPoolSize,
				keepAliveTime,
				TimeUnit.HOURS,
				queue
		);
	}
    
    
    public void queueTask(Task t) {
    	threadPool.execute(t);
    }
    

}
