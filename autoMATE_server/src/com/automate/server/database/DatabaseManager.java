<<<<<<< HEAD
package com.automate.server.database;

//import java.beans.Statement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.sql.SQLException;

import com.automate.server.database.models.Node;
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
	public User getUserByUsername(String username){
		// TODO Auto-generated method stub
		Statement stmt = null;
		String sqlQuery = "select * from users where username = \"" + username + "\""; 
		try{
			stmt = connection.createStatement();
			ResultSet rtSet = stmt.executeQuery(sqlQuery);
			long uid = rtSet.getLong("uid");
			String userName = rtSet.getString("username");
			String firstName = rtSet.getString("first_name");
			String lastName = rtSet.getString("last_name");
			String password = rtSet.getString("password");
			String email = rtSet.getString("email");
			User rtUser = new User(uid, userName, firstName, lastName, password, email);
			return rtUser;
		}
		catch(SQLException e){
			//TODO: Log error
			return null;
		}
	}

	@Override
	public User getUserByUid(long userId) {
		// TODO Auto-generated method stub
		Statement stmt = null;
		String sqlQuery = "select * from users where username = " + userId; 
		try{
			stmt = connection.createStatement();
			ResultSet rtSet = stmt.executeQuery(sqlQuery);
			long uid = rtSet.getLong("uid");
			String userName = rtSet.getString("username");
			String firstName = rtSet.getString("first_name");
			String lastName = rtSet.getString("last_name");
			String password = rtSet.getString("password");
			String email = rtSet.getString("email");
			User rtUser = new User(uid, userName, firstName, lastName, password, email);
			return rtUser;
		}
		catch(SQLException e){
			//TODO: Log error
			return null;
		}
	}

	@Override
	public com.automate.server.database.models.Node getNodeByUid(long nodeId) {
		// TODO Auto-generated method stub
		Statement stmt = null;
		String sqlQuery = "select * from node where uid = " + nodeId; 
		try{
			stmt = connection.createStatement();
			ResultSet rtSet = stmt.executeQuery(sqlQuery);
			long nId = rtSet.getLong("uid");
			String name = rtSet.getString("name");
			long userId = rtSet.getLong("user_id");
			long modelId = rtSet.getLong("model_id");
			String maxVersion = rtSet.getString("max_version");
			Node rtNode = new Node(nId,name,userId,modelId,maxVersion);
			return rtNode;
		}
		catch(SQLException e){
			//TODO: Log error
			return null;
		}
	}

	@Override
	public Model getModelByUid(long modelId) {
		// TODO Auto-generated method stub
		Statement stmt = null;
		String sqlQuery = "select * from model where uid = " + modelId; 
		try{
			stmt = connection.createStatement();
			ResultSet rtSet = stmt.executeQuery(sqlQuery);
			long uId = rtSet.getLong("uid");
			long mnfrId = rtSet.getLong("mnfr_id");
			String informationUrl = rtSet.getString("information_url");
			String commandListUrl = rtSet.getString("command_list_url");
			String modelName = rtSet.getString("name");
			Model rtModel = new Model(uId, mnfrId, informationUrl, commandListUrl, modelName);
			return rtModel;
		}
		catch(SQLException e){
			//TODO: Log error
			return null;
		}
	}

	@Override
	public Manufacturer getManufacturerByUid(long manufacturerId) {
		// TODO Auto-generated method stub
				Statement stmt = null;
				String sqlQuery = "select * from manufacturer where uid = " + manufacturerId; 
				try{
					stmt = connection.createStatement();
					ResultSet rtSet = stmt.executeQuery(sqlQuery);
					long uId = rtSet.getLong("uid");
					String mnfrName = rtSet.getString("name");
					String informationUrl = rtSet.getString("information_url");
					Manufacturer rtMnfr = new Manufacturer(uId, mnfrName, informationUrl);
					return rtMnfr;
				}
				catch(SQLException e){
					//TODO: Log error
					return null;
				}
	}

	@Override
	public List<com.automate.server.database.models.Node> getClientNodeList(
			String username) {
		// TODO Auto-generated method stub
		return null;
	}

}
=======
package com.automate.server.database;

//import java.beans.Statement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.sql.SQLException;

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
	public User getUserByUsername(String username){
		// TODO Auto-generated method stub
		Statement stmt = null;
		String sqlQuery = "select * from users where username = \"" + username + "\""; 
		try{
			stmt = connection.createStatement();
			ResultSet rtSet = stmt.executeQuery(sqlQuery);
			long uid = rtSet.getLong("uid");
			String userName = rtSet.getString("username");
			String firstName = rtSet.getString("first_name");
			String lastName = rtSet.getString("last_name");
			String password = rtSet.getString("password");
			String email = rtSet.getString("email");
			User rtUser = new User(uid, userName, firstName, lastName, password, email);
			return rtUser;
		}
		catch(SQLException e){
			//TODO: Log error
			return null;
		}
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
>>>>>>> branch 'master' of https://github.com/jhbertra/autoMATE_server.git
