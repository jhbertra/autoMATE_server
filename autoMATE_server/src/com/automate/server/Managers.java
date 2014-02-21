package com.automate.server;

import java.sql.Connection;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.automate.protocol.IIncomingMessageParser;
import com.automate.protocol.IncomingMessageParser;
import com.automate.protocol.Message;
import com.automate.protocol.Message.MessageType;
import com.automate.protocol.MessageSubParser;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.server.connectivity.ConnectivityEngine;
import com.automate.server.connectivity.EngineCallback;
import com.automate.server.connectivity.IConnectivityManager;
import com.automate.server.database.DatabaseManager;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.messaging.IMessageManager;
import com.automate.server.messaging.IPacketReceiveThread;
import com.automate.server.messaging.ISocket;
import com.automate.server.messaging.ISocket.Factory;
import com.automate.server.messaging.MessageManager;
import com.automate.server.messaging.PacketReceiveThread;
import com.automate.server.messaging.SocketInjector;
import com.automate.server.messaging.handlers.IMessageHandler;
import com.automate.server.security.ISecurityManager;
import com.automate.server.security.ISessionManager;
import com.automate.server.security.SecurityManagerImpl;

public class Managers {

	public static IMessageManager newMessageManager(
			ISecurityManager securityManager, 
			IConnectivityManager connectivityManager, 
			int minorVersion, 
			int majorVersion, 
			HashMap<MessageType, IMessageHandler<? extends Message<ClientProtocolParameters>, ?>> handlers,
			HashMap<String, MessageSubParser<Message<ClientProtocolParameters>, ClientProtocolParameters>> subParsers) {
		IPacketReceiveThread receiveThread = new PacketReceiveThread(Executors.newFixedThreadPool(25));
		ExecutorService packetSendThreadpool = Executors.newFixedThreadPool(25);
		IIncomingMessageParser<ClientProtocolParameters> parser = new IncomingMessageParser<ClientProtocolParameters>(subParsers);
		ISocket.Factory socketFactory = new Factory() {
			@Override
			public ISocket newInstance(String host, int port) {
				return new SocketInjector(host, port);
			}
		};
		return new MessageManager(securityManager, connectivityManager, receiveThread, packetSendThreadpool, 
				parser, handlers, socketFactory, minorVersion, majorVersion);
	}
	
	public static ISecurityManager newSecurityManager(
			ISessionManager sessionManager, 
			IDatabaseManager dbManager, 
			int majorVersion, 
			int minorVersion) {
		return new SecurityManagerImpl(sessionManager, dbManager, majorVersion, minorVersion);
	}
	
	public static IDatabaseManager newDatabaseManager(Connection connection) {
		return new DatabaseManager(connection);
	}
	
	public static IConnectivityManager newConnectivityManager(EngineCallback callback) {
		return new ConnectivityEngine(30, 15, callback, Executors.newSingleThreadExecutor());
	}
	
}
