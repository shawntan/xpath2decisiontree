package database;

import java.io.PrintWriter;

import javax.sql.DataSource;


import org.apache.commons.dbcp.BasicDataSource;


public class Database {
	private PrintWriter logWriter;
	private String host;
	private int port;
	private String schema;
	private String username;
	private String password;
	private String url;

	private static Database instance;
	private Database() {
	}

	public static Database getInstance(String host,int port,String schema,String username, String password) throws ClassNotFoundException {
		if(instance == null) {
			Class.forName("com.mysql.jdbc.Driver");
			instance = new Database();
			instance.host = host;
			instance.port = port;
			instance.schema = schema;
			instance.username = username;
			instance.password = password;
			instance.url = "jdbc:mysql://"+host+":"+port+"/"+schema;
		}
		return instance;
	}

	public DataSource getDataSource() {
		BasicDataSource bds = new BasicDataSource();
		bds.setUsername(this.username);
		bds.setPassword(this.password);
		bds.setUrl(this.url);
		return bds;
	}



}
