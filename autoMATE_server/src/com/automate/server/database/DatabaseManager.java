package com.automate.server.database;

import java.sql.Connection;
import java.util.List;

import com.automate.protocol.models.Node;
import com.automate.server.database.models.User;

public class DatabaseManager implements IDatabaseManager {
	
	private Connection connection;
	
	/**
	 * Creates a new DatabaseManager
	 * @param connection the connection to the database.
	 */
	public DatabaseManager(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void initialize() throws Exception {
	}

	@Override
	public void start() {
	}

	@Override
	public void terminate() throws Exception {
		connection.close();
	}

	@Override
	public List<Node> getClientNodeList(String username) {
		return null;
	}

	@Override
	public User getUser(String username) {
		return null;
	}

}
