package com.automate.server.database;

import java.sql.Connection;
import java.util.List;

import com.automate.protocol.models.Node;
import com.automate.server.database.models.Manufacturer;
import com.automate.server.database.models.Model;
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
	public User getUserByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUserByUid(int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public com.automate.server.database.models.Node getNodeByUid(long nodeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model getModelByUid(long modelId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Manufacturer getManufacturerByUid(long manufacturerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<com.automate.server.database.models.Node> getClientNodeList(
			String username) {
		// TODO Auto-generated method stub
		return null;
	}

}
