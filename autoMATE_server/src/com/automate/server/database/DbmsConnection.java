package com.automate.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbmsConnection {

	private String username;
	private String password;
	private String dbms;
	private String serverName;
	private String portNumber;

	/**
	 * Creates a new {@link DbmsConnection}
	 * @param username the database username
	 * @param password the database password
	 * @param dbms the dbms
	 * @param serverName the web address of the server
	 * @param portNumber the dbms port number
	 */
	public DbmsConnection(String username, String password, String dbms, String serverName, String portNumber) {
		this.password = password;
		this.username = username;
		this.dbms = dbms;
		this.serverName = serverName;
		this.portNumber = portNumber;
	}

	

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}



	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}



	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}



	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}



	/**
	 * @return the dbms
	 */
	public String getDbms() {
		return dbms;
	}



	/**
	 * @param dbms the dbms to set
	 */
	public void setDbms(String dbms) {
		this.dbms = dbms;
	}



	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}



	/**
	 * @param serverName the serverName to set
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}



	/**
	 * @return the portNumber
	 */
	public String getPortNumber() {
		return portNumber;
	}



	/**
	 * @param portNumber the portNumber to set
	 */
	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}


	/**
	 * Attempts to establish a connection to the dbms described by this object.
	 * @return a {@link Connection} to the dbms
	 * @throws SQLException if an error occurs while connecting.
	 */
	public Connection createConnection() throws SQLException {
		Properties connectionProperties = new Properties();
		connectionProperties.put("username", username);
		connectionProperties.put("password", password);
		return DriverManager.getConnection("jdbc:" + dbms + "://" + serverName + ":" + portNumber + "/", connectionProperties);
	}

}
