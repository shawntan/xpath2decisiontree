package processes.tasks.download;



import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import main.Application;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import processes.TaskScheduler;
import processes.tasks.ScheduledTask;
import beans.Page;

public class PeriodicDownload extends DownloadPage implements ScheduledTask{
	private static QueryRunner queryRunner;
	private static BeanListHandler<Page> beanListHandler = new BeanListHandler<Page>(Page.class);
	private static Connection con;
	private static int previousId=0;

	public static void startInitialDownloads() {
		try {
			con = Application.getDataSource().getConnection();
			queryRunner = Application.getQueryRunner();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scheduleNextPage();
	}

	private static void scheduleNextPage() {
		synchronized(con){
			try {
				
				List<Page >oldestPages = queryRunner.query(
						con,
						"SELECT id,url,next_update AS nextUpdate FROM pages ORDER BY next_update LIMIT 1",
						beanListHandler
				);
				Page page = oldestPages.get(0);
				Calendar cal = Calendar.getInstance();
				long now = cal.getTimeInMillis();
				if(page.getNextUpdate()!=null) {
					cal.setTime(page.getNextUpdate());
				} else {
					page.setNextUpdate(cal.getTime());
				}
				
				long later = cal.getTimeInMillis();
				long timeInSeconds = (later-now)/1000L;
				TaskScheduler.getInstance().scheduleTask(
						new PeriodicDownload(page, queryRunner),
						timeInSeconds
				);
				System.out.println("Next download is: "+page.getUrl()+" to be done at "+page.getNextUpdate());
				updateTimestamp(page);
				
				//debug
				if(page.getId()==previousId) {
					System.out.println("[DUPLICATE!]");
				}
				previousId = page.getId();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void run() {
		PeriodicDownload.scheduleNextPage();
		super.run();
	}

	private synchronized static void updateTimestamp(Page page){
		Calendar nextUpdateTime = Calendar.getInstance();
		nextUpdateTime.setTime(page.getNextUpdate());
		Calendar now = Calendar.getInstance();
		Calendar temp = Calendar.getInstance();
		temp.setTime(page.getNextUpdate());
		boolean needsUpdating = false;
		while(!(temp.after(now) && temp.after(nextUpdateTime))){
			temp.add(Calendar.HOUR,1);
			needsUpdating = true;
		}
		Date nextUpdate = temp.getTime();
		try {
			queryRunner.update(
					con,
					"UPDATE pages SET next_update = ? WHERE id = ?",
					nextUpdate,
					page.getId()
			);
			System.out.println("After that, download for: "+page.getUrl()+" scheduled at: "+nextUpdate);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public PeriodicDownload(Page page, QueryRunner queryRunner) {
		super(page, queryRunner);
	}
	@Override
	public long getSecondsToTask() {
		return 0;
	}
}
