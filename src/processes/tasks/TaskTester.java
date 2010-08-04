package processes.tasks;

public class TaskTester implements Task{
	private static int count;
	private int id;

	public TaskTester() {
		id  = count;
		TaskTester.count++;
	}
	
	@Override
	public Task getFollowUpActions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSuccessful() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void run() {
		System.out.println("TaskTester "+id +" RUNNING!");
		try {
			Thread.sleep((int)Math.random()*2000*5);
			System.out.println("TaskTester done!");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
