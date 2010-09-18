package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import main.Application;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import processes.TaskExecutor;
import processes.tasks.download.DownloadPage;
import beans.Page;

public class Cacher {
	private TaskExecutor te;
	public Cacher() {
		te = TaskExecutor.getInstance();
	}

	public void dataSourceStatus (Map<String,String> request, PrintStream out) {
		BasicDataSource bds = (BasicDataSource)Application.getDataSource();
		out.println("Active: "+bds.getNumActive());
		out.println("Idle: "+bds.getNumIdle());
	}
	public void display(Map<String,String> request, PrintStream out) {
		QueryRunner run = new QueryRunner(Application.getDataSource());
		try {
			Reader in =  run.query(
					"SELECT html FROM revisions WHERE revisions.page_id = ? ORDER BY revisions.id LIMIT 1;",
					new ResultSetHandler<BufferedReader> (){
						@Override
						public BufferedReader handle(ResultSet rs)	throws SQLException {
							if(rs.next()){
								BufferedReader in = new BufferedReader(rs.getCharacterStream("html"));
								return in;
							} else return null;
						}

					},
					Integer.parseInt(request.get("id"))
			);
			try {
				int c = in.read();
				while(((char)c)!='\n'){
					c = in.read();
				}//get rid of first line
				while(c>0){
					out.write((char)c);
					c = in.read();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void force(Map<String,String> request, PrintStream out) {
		QueryRunner queryRunner = Application.getQueryRunner();
		
		try {
			int id = Integer.parseInt(request.get("id"));
			List<Page> pages = queryRunner.query(
					"SELECT * FROM pages WHERE pages.id=?",
					new BeanListHandler<Page>(Page.class),
					id
			);
			te.queueTask(new DownloadPage(pages.get(0), queryRunner));
			out.println("Acknowledging task.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

