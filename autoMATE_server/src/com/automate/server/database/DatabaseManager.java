package com.automate.server.database;

//import java.beans.Statement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
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
		if(connection == null){
			throw new NullPointerException("Database manager requires a non null connection.");
		}
		this.connection = connection;
	}

	@Override
	public List<com.automate.server.database.models.Node> getClientNodeList(String username) {
		if(username == null){
			throw new IllegalArgumentException("Invalid user id.");
		}
		
		Statement stmt = null;
		String sqlQuery = 
					"select * "
				+ 	"from node "
				+ 	"where user_id = "
				+ 		"select uid"
				+ 		"from user"
				+ 		"where username = \"" + username + "\"";
		
		try{
			stmt = connection.createStatement();
			ResultSet rtSet = stmt.executeQuery(sqlQuery);
			List<Node> rtNodeList = new ArrayList<Node>();
			while(rtSet.next()){
				long nId = rtSet.getLong("uid");
				String name = rtSet.getString("name");
				long usrID = rtSet.getLong("user_id");
				long modelId = rtSet.getLong("model_id");
				String maxVersion = rtSet.getString("max_version");
				Node rtNode = new Node(nId,name,usrID,modelId,maxVersion);
				rtNodeList.add(rtNode);
			}
			return rtNodeList;
		}
		catch(SQLException e){
			//TODO: Log error
		}
		return null;
		
	}

	@Override
	public Manufacturer getManufacturerByUid(long manufacturerId) {
		if(manufacturerId < 0){
			throw new IllegalArgumentException("Invalid manufacturer id " +  manufacturerId);
		}
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
	public Model getModelByUid(long modelId) {
		if(modelId < 0){
			throw new IllegalArgumentException("Invalid model id " +  modelId);
		}
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
	public com.automate.server.database.models.Node getNodeByUid(long nodeId) {
		if(nodeId < 0){
			throw new IllegalArgumentException("Invalid node id " +  nodeId);
		}
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
	public User getUserByUid(long userId) {
		if(userId < 0){
			throw new IllegalArgumentException("Invalid user id " +  userId);
		}
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
	public User getUserByUsername(String username){
		
		if(username == null){
			throw new NullPointerException("Username is null.");
		}
		
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
	public void initialize() throws Exception {
	}

	@Override
	public void start() {
	}

	@Override
	public void terminate() throws Exception {
		connection.close();
	}

}