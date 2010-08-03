package processes.tasks;

public interface Task extends Runnable{
	public boolean isSuccessful();
	public Task getFollowUpActions();
}
