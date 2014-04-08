package com.automate.server.database;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automate.server.InitializationException;
import com.automate.server.commandLine.GrammarFile.State;
import com.automate.server.database.models.Node;
import com.automate.server.database.models.Manufacturer;
import com.automate.server.database.models.Model;
import com.automate.server.database.models.User;

public class DatabaseManager implements IDatabaseManager {

	private Connection connection;

	private static final Logger logger = LogManager.getLogger();
	
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
					"SELECT * "
				+ 	"FROM NODE "
				+ 	"WHERE user_id = (SELECT uid FROM USER WHERE username = \"" + username + "\")";
		
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
			System.out.println(rtNodeList.size() + " nodes returned.");
			return rtNodeList;
		}
		catch(SQLException e){
			logger.error("Error in getClientNodeList.", e);
			return null;
		}
		
	}

	@Override
	public Manufacturer getManufacturerByUid(long manufacturerId) {
		if(manufacturerId < 0){
			throw new IllegalArgumentException("Invalid manufacturer id " +  manufacturerId);
		}
		Statement stmt = null;
		String sqlQuery = "SELECT * FROM MANUFACTURER WHERE uid = " + manufacturerId; 
		try{
			stmt = connection.createStatement();
			ResultSet rtSet = stmt.executeQuery(sqlQuery);
			if(!rtSet.next()) return null;
			long uId = rtSet.getLong("uid");
			String mnfrName = rtSet.getString("name");
			String informationUrl = rtSet.getString("information_url");
			Manufacturer rtMnfr = new Manufacturer(uId, mnfrName, informationUrl);
			return rtMnfr;
		}
		catch(SQLException e){
			logger.error("Error in getManufacturerByUid.", e);
			return null;
		}
		
	}

	@Override
	public Model getModelByUid(long modelId) {
		if(modelId < 0){
			throw new IllegalArgumentException("Invalid model id " +  modelId);
		}
		Statement stmt = null;
		String sqlQuery = "SELECT * FROM MODEL WHERE uid = " + modelId; 
		try{
			stmt = connection.createStatement();
			ResultSet rtSet = stmt.executeQuery(sqlQuery);
			if(!rtSet.next()) return null;
			long uId = rtSet.getLong("uid");
			long mnfrId = rtSet.getLong("mnfr_id");
			String informationUrl = rtSet.getString("information_url");
			String commandListUrl = rtSet.getString("command_list_url");
			String modelName = rtSet.getString("name");
			Model rtModel = new Model(uId, mnfrId, informationUrl, commandListUrl, modelName);
			return rtModel;
		}
		catch(SQLException e){
			logger.error("Error in getModelByUid.", e);
			return null;
		}
	}

	@Override
	public Node getNodeByUid(long nodeId) {
		if(nodeId < 0){
			throw new IllegalArgumentException("Invalid node id " +  nodeId);
		}
		Statement stmt = null;
		String sqlQuery = "SELECT * FROM NODE WHERE uid = " + nodeId; 
		try{
			stmt = connection.createStatement();
			ResultSet rtSet = stmt.executeQuery(sqlQuery);
			if(!rtSet.next()) return null;
			long nId = rtSet.getLong("uid");
			String name = rtSet.getString("name");
			long userId = rtSet.getLong("user_id");
			long modelId = rtSet.getLong("model_id");
			String maxVersion = rtSet.getString("max_version");
			Node rtNode = new Node(nId,name,userId,modelId,maxVersion);
			return rtNode;
		}
		catch(SQLException e){
			logger.error("Error in getNodeByUid.", e);
			return null;
		}
	}

	@Override
	public User getUserByUid(long userId) {
		if(userId < 0){
			throw new IllegalArgumentException("Invalid user id " +  userId);
		}
		Statement stmt = null;
		String sqlQuery = "SELECT * FROM USER WHERE uid = " + userId; 
		try{
			stmt = connection.createStatement();
			ResultSet rtSet = stmt.executeQuery(sqlQuery);
			if(!rtSet.next()) return null;
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
			logger.error("Error in getUserByUid.", e);
			return null;
		}
	}

	@Override
	public User getUserByUsername(String username){
		
		if(username == null){
			throw new NullPointerException("Username is null.");
		}
		
		Statement stmt = null;
		String sqlQuery = "SELECT * FROM USER WHERE username = \"" + username + "\""; 
		try{
			stmt = connection.createStatement();
			ResultSet rtSet = stmt.executeQuery(sqlQuery);
			if(!rtSet.next()) return null;
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
			logger.error("Error in getUserByUsername.", e);
			return null;
		}
	}

	@Override
	public User addUser(String username, String password, String name, String email) {
		if(username == null) {
			throw new NullPointerException("username is null.");
		} else if(password == null) {
			throw new NullPointerException("password is null.");
		} else if(name == null) {
			throw new NullPointerException("name is null.");
		} else if(email == null) {
			throw new NullPointerException("email is null.");
		}
		
		int spaceIndex = name.indexOf(" ");
		String firstName = null;
		String lastName = "";
		if(spaceIndex != -1) {
			firstName = name.substring(0, spaceIndex);
			lastName = name.substring(spaceIndex + 1);
		} else {
			firstName = name;
		}
		
		try {
			String query = "INSERT INTO `automate`.`USER`"
					+ "(`username`,"
					+ "`first_name`,"
					+ "`last_name`,"
					+ "`password`,"
					+ "`email`)"
					+ "VALUE"
					+ "(?,"
					+ "?,"
					+ "?,"
					+ "?,"
					+ "?);";
			PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, username);
			statement.setString(2, firstName);
			statement.setString(3, lastName);
			statement.setString(4, password);
			statement.setString(5, email);
			
			int affectedRows = statement.executeUpdate();
			if(affectedRows == 0) {
				return null;
			}
			
			ResultSet generatedKeys = statement.getGeneratedKeys();
			if(generatedKeys.next()) {
				return new User(generatedKeys.getLong(1), username, firstName, lastName, password, email);
			} else {
				return null;
			}
		} catch (SQLException e) {
			logger.error("Error in addUser.", e);
			return null;
		}
	}

	@Override
	public void initialize() throws InitializationException {
	}

	@Override
	public void start() {
	}

	@Override
	public void terminate() throws Exception {
		logger.info("Shutting down database manager...");
		connection.close();
	}

	@Override
	public Node addNode(String maxVersion, long modelId, String name, long userId) {
		if(modelId < 0) {
			throw new IllegalArgumentException("modelId is invalid.");
		} else if(userId < 0) {
			throw new IllegalArgumentException("userId is invalid.");
		} else if(maxVersion == null) {
			throw new NullPointerException("max version is null.");
		}
		
		try {
			String query = "INSERT INTO `automate`.`NODE`"
					+ "(`user_id`,"
					+ "`model_id`,"
					+ "`name`,"
					+ "`max_version`)"
					+ "VALUE"
					+ "(?,"
					+ "?,"
					+ "?,"
					+"?);";
			PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setLong(1, userId);
			statement.setLong(2, modelId);
			statement.setString(3, name);
			statement.setString(4, maxVersion);
			int affectedRows = statement.executeUpdate();
			if(affectedRows == 0) return null;
			
			ResultSet generatedKeys = statement.getGeneratedKeys();
			if(generatedKeys.next()) {
				return new Node(generatedKeys.getLong(1), name, userId, modelId, maxVersion);
			} else {
				return null;
			}
		} catch(SQLException e) {
			logger.error("Error in addNode.", e);
			return null;
		}
	}

}
