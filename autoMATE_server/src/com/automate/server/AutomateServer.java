package com.automate.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import com.automate.protocol.Message;
import com.automate.protocol.Message.MessageType;
import com.automate.protocol.MessageSubParser;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.server.connectivity.ConnectivityEngine;
import com.automate.server.connectivity.IConnectivityManager;
import com.automate.server.database.DatabaseManager;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.messaging.IMessageManager;
import com.automate.server.messaging.MessageManager;
import com.automate.server.messaging.PacketReceiveThread;
import com.automate.server.messaging.handlers.IMessageHandler;
import com.automate.server.security.ISecurityManager;
import com.automate.server.security.SecurityManagerImpl;
import com.automate.server.security.SessionManager;


public class AutomateServer {

	/*
	 * The subsystems
	 */
	private IMessageManager messageManager;
	private IConnectivityManager connectivityManager;
	private ISecurityManager securityManager;
	private IDatabaseManager dbManager;
	
	private boolean started = false;
	
	private static final int MAJOR_VERSION = 1;
	private static final int MINOR_VERSION = 0;
	
	private Api api;
	
	public void initSubsystems() {
		Properties connectionProperties = new Properties();
		connectionProperties.put("user", "root");
		connectionProperties.put("password", "quinoa");
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", connectionProperties);
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.exit(0);
			return;
		}
		
		SessionManager sessionManager = new SessionManager(MAJOR_VERSION, MINOR_VERSION);
		dbManager = Managers.newDatabaseManager(connection);
		securityManager = Managers.newSecurityManager(sessionManager, dbManager, MAJOR_VERSION, MINOR_VERSION);
		connectivityManager = Managers.newConnectivityManager(sessionManager);
		
		HashMap<MessageType, IMessageHandler<? extends Message<ClientProtocolParameters>, ?>> handlers = getMessageHandlers();
		HashMap<String, MessageSubParser<Message<ClientProtocolParameters>, ClientProtocolParameters>> subParsers = getMessageSubParsers();
		
		messageManager = Managers.newMessageManager(
				securityManager, 
				connectivityManager, 
				MINOR_VERSION, 
				MAJOR_VERSION, 
				handlers, 
				subParsers);
		
		this.api = new Api();
		
		try {
			dbManager.initialize();
			securityManager.initialize();
			connectivityManager.initialize();
			messageManager.initialize();
		} catch(Exception e) {
			System.out.println("Error initializing managers.");
			e.printStackTrace();
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

	public void start() {
		if(!started) {
			try {
				dbManager.start();
				securityManager.start();
				connectivityManager.start();
				messageManager.start();
			} catch (Exception e) {
				System.out.println("Error starting server");
				e.printStackTrace();
			}
		} else {
			throw new IllegalStateException("Cannot start server twice!");
		}
	}
	
	public void shutdown() {
		if(started) {
			try {
				dbManager.terminate();
				securityManager.terminate();
				connectivityManager.terminate();
				messageManager.terminate();
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



	public class Api {
		
		public void shutdown() {
			AutomateServer.this.shutdown();
		}
		
	}
	
}
