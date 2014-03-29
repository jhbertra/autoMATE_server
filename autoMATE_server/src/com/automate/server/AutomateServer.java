package com.automate.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automate.protocol.Message;
import com.automate.protocol.Message.MessageType;
import com.automate.protocol.MessageSubParser;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.client.subParsers.ClientAuthenticationMessageSubParser;
import com.automate.protocol.client.subParsers.ClientCommandMessageSubParser;
import com.automate.protocol.client.subParsers.ClientNodeListMessageSubParser;
import com.automate.protocol.client.subParsers.ClientPingMessageSubParser;
import com.automate.protocol.client.subParsers.ClientStatusUpdateMessageSubParser;
import com.automate.protocol.client.subParsers.ClientWarningMessageSubParser;
import com.automate.protocol.node.messages.NodeStatusUpdateMessage;
import com.automate.server.connectivity.ConnectivityWatchdogThread;
import com.automate.server.connectivity.ConnectivityWatchdogThread.OnClientTimeoutListener;
import com.automate.server.connectivity.EngineCallback;
import com.automate.server.connectivity.IConnectivityManager;
import com.automate.server.database.DbmsConnection;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.messaging.IMessageManager;
import com.automate.server.messaging.handlers.AuthenticationMessageHandler;
import com.automate.server.messaging.handlers.ClientCommandMessageHandler;
import com.automate.server.messaging.handlers.ClientStatusUpdateMessageHandler;
import com.automate.server.messaging.handlers.ClientWarningMessageHandler;
import com.automate.server.messaging.handlers.IMessageHandler;
import com.automate.server.messaging.handlers.NodeCommandMessageHandler;
import com.automate.server.messaging.handlers.NodeListMessageHandler;
import com.automate.server.messaging.handlers.NodeStatusUpdateMessageHandler;
import com.automate.server.messaging.handlers.NodeWarningMessageHandler;
import com.automate.server.messaging.handlers.PingMessageHandler;
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
	private ISessionManager sessionManager;
	private ExecutorService connectivityExecutorService;
	private EngineCallback connectivityCallback;
	private int connectivityTimeout;
	private int connectivityPingInterval;
	private int numReceiveThreads;
	private int numSendThreads;

	private static final Logger logger = LogManager.getLogger();

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
	}

	private void readConnectivityProperties() throws InitializationException {
		String timeoutString = properties.getProperty("connectivity.timeout");
		String intervalString = properties.getProperty("connectivity.heartbeat");
		logger.trace("Connectivity engine timeout: {} seconds", timeoutString);
		logger.trace("Connectivity engine ping interval: {} seconds", intervalString);
		if(timeoutString == null) {
			throw new InitializationException("Property not defined: connectivity.timeout");
		} else if(intervalString == null) {
			throw new InitializationException("Property not defined: connectivity.heartbeat");
		}


		try {
			connectivityTimeout = Integer.parseInt(timeoutString);
		} catch(NumberFormatException e) {
			throw new InitializationException("Error parsing connectivity properties: timeout malformed: connectivity.timeout = " + timeoutString);
		}
		try {
			connectivityPingInterval = Integer.parseInt(intervalString);
		} catch(NumberFormatException e) {
			throw new InitializationException("Error parsing connectivity properties: heartbeat malformed: connectivity.heartbeat = " + intervalString);
		}

		connectivityCallback = (EngineCallback) this.sessionManager;
		connectivityExecutorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable runnable) {
				return new Thread(runnable, "Connectivity Engine");
			}
		});
	}

	private void readSecurityProperties() {
		sessionManager = new SessionManager(majorVersion, minorVersion);
	}

	private void readVersionProperties() throws InitializationException {
		String majorVersionString = properties.getProperty("version.major");
		String minorVersionString = properties.getProperty("version.minor");
		logger.trace("Protocol version: {}.{}", majorVersionString, minorVersionString);
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
		String database = properties.getProperty("database.database");
		logger.trace("Database username: {}", username);
		logger.trace("Database password: {}", password);
		logger.trace("Database dbms: {}", dbms);
		logger.trace("Database server: {}", serverName);
		logger.trace("Database port: {}", portNumber);
		logger.trace("Database database: {}", database);
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
		} else if (database == null) {
			throw new InitializationException("Property not defined: database.database");
		}
		this.dbmsConnection = new DbmsConnection(username, password, dbms, serverName, portNumber, database);
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
		try {
			if(properties == null) {
				throw new IllegalStateException("Must set server properties before initializing!");
			}
			logger.trace("Initialization started.");
			if(initialized) {
				return;
			}
			readProperties();

			Connection connection = connectToDbms();
			logger.trace("Creating database manager.");
			dbManager = Managers.newDatabaseManager(connection);
			logger.trace("Creating security manager.");
			securityParams = new SecurityManagerParameters(sessionManager, dbManager, majorVersion, minorVersion);
			securityManager = Managers.newSecurityManager(securityParams);
			logger.trace("Creating connectivity manager.");
			connectivityParams = new ConnectivityEngineParameters(connectivityExecutorService, connectivityCallback, connectivityTimeout, 
					connectivityPingInterval);
			connectivityManager = Managers.newConnectivityManager(connectivityParams);
			logger.trace("Creating message manager.");
			messagingParams = new MessageManagerParameters(numReceiveThreads, numSendThreads, getMessageSubParsers(), securityManager, 
					connectivityManager, getMessageHandlers(), minorVersion, majorVersion);
			messageManager = Managers.newMessageManager(messagingParams);		

			try {
				dbManager.initialize();
				securityManager.initialize();
				connectivityManager.initialize();
				ConnectivityWatchdogThread watchdog = new ConnectivityWatchdogThread((OnClientTimeoutListener) connectivityManager);
				connectivityManager.setWatchdogThread(watchdog);
				watchdog.start();
				messageManager.initialize();
			} catch(RuntimeException e) {
				throw new InitializationException("Unexpected exception initializing subsystems.", e);
			}
			initialized = true;
		} finally {
			if(!initialized) logger.trace("Initialization aborted.");
			else logger.trace("Initialization ended.");
		}
	}

	private Connection connectToDbms() throws InitializationException {
		try {
			logger.info("Attempting to connect to dbms...");
			return dbmsConnection.createConnection();
		} catch (SQLException e) {
			throw new InitializationException(e);
		}
	}

	private HashMap<String, MessageSubParser<? extends Message<ClientProtocolParameters>, ClientProtocolParameters>> getMessageSubParsers() {
		logger.trace("Configuring message sub parsers.");
		HashMap<String, MessageSubParser<? extends Message<ClientProtocolParameters>,ClientProtocolParameters>> subParsers = 
				new HashMap<String, MessageSubParser<? extends Message<ClientProtocolParameters>,ClientProtocolParameters>>();
		subParsers.put(MessageType.AUTHENTICATION.toString(), new ClientAuthenticationMessageSubParser());
		subParsers.put(MessageType.NODE_LIST.toString(), new ClientNodeListMessageSubParser());
		subParsers.put(MessageType.PING.toString(), new ClientPingMessageSubParser());
		subParsers.put(MessageType.COMMAND_CLIENT.toString(), new ClientCommandMessageSubParser());
		//subParsers.put(MessageType.COMMAND_NODE.toString(), new NodeCommandMessageSubParser());
		subParsers.put(MessageType.STATUS_UPDATE_CLIENT.toString(), new ClientStatusUpdateMessageSubParser());
		//subParsers.put(MessageType.STATUS_UPDATE_NODE.toString(), new NodeStatusUpdateMessageSubParser());
		subParsers.put(MessageType.WARNING_CLIENT.toString(), new ClientWarningMessageSubParser());
		return subParsers;
	}

	private HashMap<MessageType, IMessageHandler<? extends Message<ClientProtocolParameters>, ?>> getMessageHandlers() {
		logger.trace("Configuring message handlers.");
		HashMap<MessageType, IMessageHandler<? extends Message<ClientProtocolParameters>, ?>> handlers =
				new HashMap<MessageType, IMessageHandler<? extends Message<ClientProtocolParameters>, ?>>();
		handlers.put(MessageType.AUTHENTICATION, new AuthenticationMessageHandler(securityManager));
		handlers.put(MessageType.NODE_LIST, new NodeListMessageHandler(dbManager, securityManager));
		handlers.put(MessageType.PING, new PingMessageHandler(connectivityManager));
		handlers.put(MessageType.COMMAND_CLIENT, new ClientCommandMessageHandler(dbManager, securityManager));
		handlers.put(MessageType.COMMAND_NODE, new NodeCommandMessageHandler(dbManager, securityManager));
		handlers.put(MessageType.STATUS_UPDATE_CLIENT, new ClientStatusUpdateMessageHandler(dbManager, securityManager));
		handlers.put(MessageType.STATUS_UPDATE_NODE, new NodeStatusUpdateMessageHandler(dbManager, securityManager));
		HashMap<Long, String> pendingWarnings = new HashMap<Long, String>();
		handlers.put(MessageType.WARNING_CLIENT, new ClientWarningMessageHandler(pendingWarnings));
		handlers.put(MessageType.WARNING_NODE, new NodeWarningMessageHandler(pendingWarnings, dbManager, securityManager));
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
				running = true;
				logger.info("Starting server...");
				dbManager.start();
				securityManager.start();
				connectivityManager.start();
				messageManager.start();
				logger.info("Server started.");
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
				logger.info("Shutting down server...");
				if(dbManager != null) {
					dbManager.terminate();
				}
				if(securityManager != null) {
					securityManager.terminate();
				}
				if(connectivityManager != null) {
					connectivityManager.terminate();
				}
				if(messageManager != null) {
					messageManager.terminate();
				}
				logger.info("Server shut down.");
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
