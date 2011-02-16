package processes.tasks;

public class TaskTester implements Task{
	private static int count;
	private int id;

	public TaskTester() {
		id  = count;
		TaskTester.count++;
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
