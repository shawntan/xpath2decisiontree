package processes.tasks;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import main.Application;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import processes.TaskScheduler;
import beans.Page;

public class PeriodicDownload extends DownloadPage implements ScheduledTask{
	private static int simultaneous;
	private static QueryRunner queryRunner;
	private static Deque<Page> pageDownloadQueue;
	private static BeanListHandler<Page> beanListHandler = new BeanListHandler<Page>(Page.class);

	private void scheduleSelf() {
		System.out.println("Scheduling "+page.getUrl()+" now...");
		Calendar nextUpdateTime = Calendar.getInstance();
		nextUpdateTime.setTime(page.getUpdatedAt());
		nextUpdateTime.add(Calendar.MINUTE, 5);
		Calendar now = Calendar.getInstance();
		long timeToDL = (nextUpdateTime.getTimeInMillis()-now.getTimeInMillis())/1000L;
		TaskScheduler.getInstance().scheduleTask(
				this,
				timeToDL
		);
		System.out.println("\t"+page.getUrl()+" scheduled to download in "+timeToDL+" seconds.");
	}
	public static void startInitialDownloads(int scheduledPages){
		PeriodicDownload.simultaneous = scheduledPages;
		PeriodicDownload.queryRunner = Application.getQueryRunner();
		pageDownloadQueue = new LinkedList<Page>();
		List<Page> oldestPages = addOldestPagesToQueue(5);
		for(Page page:oldestPages){
			new PeriodicDownload(page,queryRunner);
		}
	}
	private synchronized static List<Page> addOldestPagesToQueue(int n){
		List<Page> oldestPages = null;
		try {
			if(!pageDownloadQueue.isEmpty()) {
				System.out.println("\tLatest queue item is: "+pageDownloadQueue.peekLast().getUpdatedAt());
				oldestPages = queryRunner.query(
						"SELECT id,url,updated_at AS updatedAt FROM pages WHERE updated_at > ? ORDER BY updated_at LIMIT ? ",
						beanListHandler,
						pageDownloadQueue.peekLast().getUpdatedAt(),
						n
				);
			}
			else {
				n = Math.max(n,simultaneous);
				System.out.println("\tNo queue items found. Getting oldest.");
				oldestPages = queryRunner.query(
						"SELECT id,url,updated_at AS updatedAt FROM pages ORDER BY updated_at LIMIT ?",
						beanListHandler,
						n
				);
			}
			for(Page p:oldestPages)	{
				pageDownloadQueue.add(p);
				System.out.println("\t"+p.getUrl()+" added to download queue. Last updated at: "+p.getUpdatedAt());
			}
			return oldestPages;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	private synchronized static Page getNextPageInQueue(Page page){
		pageDownloadQueue.remove(page);
		List<Page> pageList = addOldestPagesToQueue(1);
		Page p=null;
		if(pageList == null || pageList.isEmpty()) System.out.println("\tNo new pages. All up to date");
		else {
			p = pageList.get(0);
		}
		return p;
	}
	public static void printDownloadQueue(PrintStream out) {
		out.println("Printing....");
		for(Page p:pageDownloadQueue) {
			out.println(p.getUrl()+" "+p.getUpdatedAt());
		}
	}

	public PeriodicDownload(Page page, QueryRunner queryRunner) {
		super(page, queryRunner);
		scheduleSelf();
	}
	public void run () {
		super.run();
		this.page = PeriodicDownload.getNextPageInQueue(this.page);
		scheduleSelf();
	}
	@Override
	public long getSecondsToTask() {
		// TODO Auto-generated method stub
		return 0;
	}
}
