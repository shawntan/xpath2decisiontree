package processes.tasks;

public interface Task extends Runnable{
	public Task getFollowUpActions();
}
