package com.automate.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.automate.protocol.Message;
import com.automate.protocol.Message.MessageType;
import com.automate.protocol.MessageSubParser;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.server.connectivity.ConnectivityWatchdogThread;
import com.automate.server.connectivity.ConnectivityWatchdogThread.OnClientTimeoutListener;
import com.automate.server.connectivity.EngineCallback;
import com.automate.server.connectivity.IConnectivityManager;
import com.automate.server.database.DbmsConnection;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.messaging.IMessageManager;
import com.automate.server.messaging.handlers.IMessageHandler;
import com.automate.server.security.ISecurityManager;
import com.automate.server.security.ISessionManager;
import com.automate.server.security.SessionManager;


public class AutomateServer {

	/**
	 * The subsystem responsible for handling message transit.
	 */
	private IMessageManager messageManager;
	/**
	 * The subsystem responsible for maintaining connectivity data
	 * for all nodes and applications.
	 */
	private IConnectivityManager connectivityManager;
	/**
	 * The subsystem responsible for validating and enforcing
	 * client session policy.
	 */
	private ISecurityManager securityManager;
	/**
	 * The subsystem responsible for all database interactions.
	 */
	private IDatabaseManager dbManager;
	
	/**
	 * flag to indicate if the server is running
	 */
	private boolean running = false;
	
	/**
	 * flag to indicate if the server has been initialized
	 */
	private boolean initialized = false;
	
	/**
	 * Major version of the maximum supported protocol version.
	 */
	private int majorVersion = 1;
	/**
	 * Minor version of the maximum supported protocol version.
	 */
	private int minorVersion = 0;
	
	/**
	 * Api object for use by user interfaces.
	 */
	private Api api;
	
	/**
	 * The properties object containing all information about the server.
	 */
	private Properties properties;
	
	/**
	 * The connection to the dbms.
	 */
	private DbmsConnection dbmsConnection;
	/**
	 * Parameters for creating and configuring the security manager.
	 */
	private SecurityManagerParameters securityParams;
	/**
	 * Parameters for creating and configuring the connectivity manager.
	 */
	private ConnectivityEngineParameters connectivityParams;
	/**
	 * Parameters for creating and configuring the message manager.
	 */
	private MessageManagerParameters messagingParams;
	
	/**
	 * Creates a new {@link AutomateServer}
	 * @param properties the properties that define the server's configurable behaviour.
	 */
	public AutomateServer() {
		this.api = new Api();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PROPERTY READING 
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void readProperties() throws InitializationException {
		readDatabaseProperties();
		readVersionProperties();
		readSecurityProperties();
		readConnectivityProperties();
		readMessagingProperties();
	}

	private void readMessagingProperties() throws InitializationException {
		String numReceiveThreadsString = properties.getProperty("messaging.receiveThreads");
		String numSendThreadsString = properties.getProperty("messaging.sendThreads");
		if(numReceiveThreadsString == null) {
			throw new InitializationException("Property not defined: messaging.receiveThreads");
		} else if(numSendThreadsString == null) {
			throw new InitializationException("Property not defined: messaging.sendThreads");
		}
		
		int numReceiveThreads;
		int numSendThreads;
		
		try {
			numReceiveThreads = Integer.parseInt(numReceiveThreadsString);
		} catch(NumberFormatException e) {
			throw new InitializationException("Error parsing messaging properties: receiveThreads malformed: messaging.receiveThreads = " + numReceiveThreadsString);
		}
		
		try {
			numSendThreads = Integer.parseInt(numSendThreadsString);
		} catch(NumberFormatException e) {
			throw new InitializationException("Error parsing messaging properties: sendThreads malformed: messaging.sendThreads = " + numSendThreadsString);
		}
		
		this.messagingParams = new MessageManagerParameters(numReceiveThreads, numSendThreads, getMessageSubParsers(), securityManager, 
				connectivityManager, getMessageHandlers(), minorVersion, majorVersion);
	}

	private void readConnectivityProperties() throws InitializationException {
		String timeoutString = properties.getProperty("connectivity.timeout");
		String intervalString = properties.getProperty("connectivity.heartbeat");
		if(timeoutString == null) {
			throw new InitializationException("Property not defined: connectivity.timeout");
		} else if(intervalString == null) {
			throw new InitializationException("Property not defined: connectivity.heartbeat");
		}
		
		int timeout;
		int pingInterval;
		
		try {
			timeout = Integer.parseInt(timeoutString);
		} catch(NumberFormatException e) {
			throw new InitializationException("Error parsing connectivity properties: timeout malformed: connectivity.timeout = " + timeoutString);
		}
		try {
			pingInterval = Integer.parseInt(intervalString);
		} catch(NumberFormatException e) {
			throw new InitializationException("Error parsing connectivity properties: heartbeat malformed: connectivity.heartbeat = " + intervalString);
		}
		
		EngineCallback callback = (EngineCallback) this.messageManager;
		ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable runnable) {
				return new Thread(runnable, "Connectivity Engine");
			}
		});
		
		this.connectivityParams = new ConnectivityEngineParameters(executorService, callback, timeout, pingInterval);
	}

	private void readSecurityProperties() {
		ISessionManager sessionManager = new SessionManager(majorVersion, minorVersion);
		this.securityParams = new SecurityManagerParameters(sessionManager, dbManager, majorVersion, minorVersion);
	}

	private void readVersionProperties() throws InitializationException {
		String majorVersionString = properties.getProperty("version.major");
		String minorVersionString = properties.getProperty("version.minor");
		if(majorVersionString == null) {
			throw new InitializationException("Property not defined: version.major");
		} else if(minorVersionString == null) {
			throw new InitializationException("Property not defined: version.minor");
		}
		try {
			this.majorVersion = Integer.parseInt(majorVersionString);
		} catch(NumberFormatException e) {
			throw new InitializationException("Error parsing protocol version properties: major version malformed: version.major = " + majorVersionString);
		}
		try {
			this.minorVersion = Integer.parseInt(minorVersionString);
		} catch(NumberFormatException e) {
			throw new InitializationException("Error parsing protocol version properties: minor version malformed: version.minor = " + minorVersionString);
		}
	}

	void readDatabaseProperties() throws InitializationException {
		String username = properties.getProperty("database.username");
		String password = properties.getProperty("database.password");
		String dbms = properties.getProperty("database.dbms");
		String serverName = properties.getProperty("database.server");
		String portNumber = properties.getProperty("database.port");
		if(username == null) {
			throw new InitializationException("Property not defined: database.username");
		} else if(password == null) {
			throw new InitializationException("Property not defined: database.password");
		} else if(dbms == null) {
			throw new InitializationException("Property not defined: database.dbms");
		} else if(serverName == null) {
			throw new InitializationException("Property not defined: database.server");
		} else if(portNumber == null) {
			throw new InitializationException("Property not defined: database.port");
		}
		this.dbmsConnection = new DbmsConnection(username, password, dbms, serverName, portNumber);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// END PROPERTY READING 
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INITIALIZATION ROUTINES 
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Defines new properties for this server, overwriting the previous properties.
	 * Server must be re-initialized afterwards.
	 * @param properties the new properties to set.
	 * @throws IllegalStateException if the server is currently running.
	 */
	public void setProperties(Properties properties) {
		if(running) throw new IllegalStateException("Cannot set new properties on a running server!");
		this.properties = properties;
		this.initialized = false;
	}
	
	/**
	 * Creates and initialises the application subsystems.
	 * @throws InitializationException if an error occurs during initialisation.
	 * @throws IllegalStateException if the properties have not been set.
	 */
	public void initSubsystems() throws InitializationException {
		if(properties == null) {
			throw new IllegalStateException("Must set server properties before initializing!");
		}
		if(initialized) return;
		readProperties();
		
		Connection connection = connectToDbms();
		dbManager = Managers.newDatabaseManager(connection);
		securityManager = Managers.newSecurityManager(securityParams);
		connectivityManager = Managers.newConnectivityManager(connectivityParams);
		messageManager = Managers.newMessageManager(messagingParams);		
		
		try {
			dbManager.initialize();
			securityManager.initialize();
			connectivityManager.initialize();
			connectivityManager.setWatchdogThread(new ConnectivityWatchdogThread((OnClientTimeoutListener) connectivityManager));
			messageManager.initialize();
		} catch(RuntimeException e) {
			throw new InitializationException("Unexpected exception initializing subsystems.", e);
		}
		initialized = true;
	}
	
	private Connection connectToDbms() throws InitializationException {
		try {
			return dbmsConnection.createConnection();
		} catch (SQLException e) {
			throw new InitializationException(e);
		}
	}
	
	private HashMap<String, MessageSubParser<Message<ClientProtocolParameters>, ClientProtocolParameters>> getMessageSubParsers() {
		HashMap<String, MessageSubParser<Message<ClientProtocolParameters>,ClientProtocolParameters>> subParsers = 
				new HashMap<String, MessageSubParser<Message<ClientProtocolParameters>,ClientProtocolParameters>>();
		return subParsers;
	}

	private HashMap<MessageType, IMessageHandler<? extends Message<ClientProtocolParameters>, ?>> getMessageHandlers() {
		HashMap<MessageType, IMessageHandler<? extends Message<ClientProtocolParameters>, ?>> handlers =
				new HashMap<MessageType, IMessageHandler<? extends Message<ClientProtocolParameters>, ?>>();
		return handlers;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// END INITIALIZATION ROUTINES 
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// POST INITIALIZATION ROUTINES 
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Starts the server.
	 * @throws IllegalStateException if the server is already running
	 * @throws IllegalStateException if the server is not yet initialized
	 */
	public void start() {
		if(!initialized) throw new IllegalStateException("Server not initialized!");
		if(!running) {
			try {
				dbManager.start();
				securityManager.start();
				connectivityManager.start();
				messageManager.start();
				running = true;
			} catch (Exception e) {
				System.out.println("Error starting server");
				e.printStackTrace();
			}
		} else {
			throw new IllegalStateException("Cannot start server twice!");
		}
	}
	
	/**
	 * Shuts down the server.
	 * @throws IllegalStateException if the server is shutdown before it is started.
	 */
	public void shutdown() {
		if(running) {
			try {
				dbManager.terminate();
				securityManager.terminate();
				connectivityManager.terminate();
				messageManager.terminate();
				running = false;
			} catch (Exception e) {
				System.out.println("Error stopping server");
				e.printStackTrace();
			}
		} else {
			throw new IllegalStateException("Server not started, cannot shut down!");
		}
	}
	
	/**
	 * @return the api
	 */
	public Api getApi() {
		return api;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// END POST INITIALIZATION ROUTINES 
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// API METHODDS 
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public class Api {
		
		public void shutdown() {
			AutomateServer.this.shutdown();
		}
		
	}
	
}
