package processes.tasks;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import main.Application;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import processes.TaskScheduler;

import database.Database;

import beans.Page;

public class PeriodicDownload extends DownloadPage implements ScheduledTask{



	private long intervalInSeconds;
	private static int scheduledPages;
	private static QueryRunner queryRunner;

	public static void main(String[] args) {
		PeriodicDownload.startInitialDownloads(10);
	}
	public static void startInitialDownloads(int scheduledPages){
		PeriodicDownload.scheduledPages = scheduledPages;
		PeriodicDownload.queryRunner = Application.getQueryRunner();
		try {
			Connection dbConn;
			dbConn = Application.getDataSource().getConnection();
			QueryRunner run = new QueryRunner();
			List<Page> pages = run.query(dbConn,
					"SELECT id,url,updated_at AS updatedAt FROM pages ORDER BY updated_at LIMIT ?;",
					new BeanListHandler<Page>(Page.class),
					scheduledPages
			);
			DbUtils.close(dbConn);
			for(Page p: pages){
				schedulePage(p,null);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	private static void schedulePage(Page p,PeriodicDownload task) {
		if(task==null) task = new PeriodicDownload(p, queryRunner);
		Calendar nextUpdateTime = Calendar.getInstance();
		nextUpdateTime.setTime(p.getUpdatedAt());
		nextUpdateTime.add(Calendar.DATE, 1);
		Calendar now = Calendar.getInstance();
		long timeToDL = (nextUpdateTime.getTimeInMillis()-now.getTimeInMillis())/1000L;
		System.out.println(timeToDL);
		TaskScheduler.getInstance().scheduleTask(
				task,
				timeToDL
		);

	}


	public PeriodicDownload(Page page, QueryRunner queryRunner) {
		super(page, queryRunner);
		// TODO Auto-generated constructor stub
	}

	public void run () {
		super.run();
		try {
			List<Page> pages = Application.getQueryRunner().query(
					"SELECT id,url,updated_at AS updatedAt FROM pages ORDER BY updated_at LIMIT 1;",
					new BeanListHandler<Page>(Page.class)
			);
			this.page = pages.get(0);
			System.out.println(this.page.getUrl());
			schedulePage(this.page,this);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public long getSecondsToTask() {
		// TODO Auto-generated method stub
		return intervalInSeconds;
	}



}
