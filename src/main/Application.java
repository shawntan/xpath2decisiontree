package main;
import httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;

import processes.TaskExecutor;
import processes.TaskScheduler;
import processes.tasks.ScheduledTask;
import processes.tasks.Task;
import processes.tasks.download.PeriodicDownload;
import processes.tasks.extraction.ScheduledScrape;
import utils.WebClientFactory;

import beans.Extractor;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParserListener;

import database.Database;


public class Application {
	final private static String DATABASE_HOST = "databaseHost";
	final private static String DATABASE_NAME = "databaseName";
	final private static String DATABASE_PASS = "databasePass";
	final private static String DATABASE_PORT = "databasePort";
	final private static String DATABASE_USER = "databaseUser";
	private static DataSource dataSource;
	final private static String HTTP_SERVER_PORT = "httpServerPort";
	
	private static QueryRunner queryRunner;
	private static Properties settings;
	final private static String SETTINGS_FILE="settings.properties";
	final private static String THREAD_POOL_SIZE = "threadPoolSize";
	private static ObjectPool webClientPool;
	private static HttpServer httpServer;
	private static TaskScheduler taskScheduler;
	private static TaskExecutor taskExecutor;
	
	public static void main(String[] args) throws SQLException {
		loadSettings();
		startTaskDispatcher();
		startHttpServer();
		startScheduledTasks();
		saveSettings();
	}
	
	
	public static DataSource getDataSource() {
		if(dataSource == null) {
			try {
				dataSource = Database.getInstance(
						settings.getProperty(DATABASE_HOST),
						Integer.parseInt(settings.getProperty(DATABASE_PORT)),
						settings.getProperty(DATABASE_NAME),
						settings.getProperty(DATABASE_USER),
						settings.getProperty(DATABASE_PASS)
					).getDataSource();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dataSource;
	}
	public static QueryRunner getQueryRunner() {
		if(queryRunner == null) {
			queryRunner = new QueryRunner(getDataSource());
		}
		return queryRunner;
	}


	

	
	public static void loadSettings() {
		settings = new Properties();
		File settingsFile;
		try {
			settingsFile = new File(SETTINGS_FILE);
			settings.load(new FileInputStream(settingsFile));
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			System.out.println("Could not load settings file.");
			System.out.println(e.getMessage());
		}
	}

	private static void saveSettings() {
		File settingsFile;
		settingsFile = new File(SETTINGS_FILE);
		try {
			settings.store(new FileOutputStream(settingsFile),"Last changed: "+new Date().toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void startHttpServer() {
		httpServer = new HttpServer(Integer.parseInt(settings.getProperty(HTTP_SERVER_PORT)),"controllers");
	}
	private static void startScheduledTasks() {
		taskScheduler = TaskScheduler.getInstance(1);
		try {
			QueryRunner runner =getQueryRunner();
			List<Extractor> extractors = runner.query(
					"SELECT id,domain, update_time as updateTime FROM extractors",
					new BeanListHandler<Extractor>(Extractor.class));
			TaskScheduler ts = TaskScheduler.getInstance();
			for(Extractor e:extractors) {
				if(e.getUpdateTime()!=null)
					ts.scheduleTask(new ScheduledScrape(e));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private static void startTaskDispatcher() {
		taskExecutor = TaskExecutor.getInstance(5,5,1000L);
	}
}
