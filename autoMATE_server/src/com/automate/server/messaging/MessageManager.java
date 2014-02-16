package com.automate.server.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.automate.protocol.Message;
import com.automate.protocol.Message.MessageType;
import com.automate.protocol.MessageFormatException;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.client.IncomingClientMessageParser;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.server.connectivity.IConnectivityManager;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.messaging.handlers.AuthenticationMessageHandler;
import com.automate.server.messaging.handlers.AuthenticationMessageHandlerParams;
import com.automate.server.messaging.handlers.IMessageHandler;
import com.automate.server.messaging.handlers.NodeListMessageHandler;
import com.automate.server.messaging.handlers.PingMessageHandler;
import com.automate.server.security.ISecurityManager;
import com.automate.util.xml.XmlFormatException;

public class MessageManager implements IMessageManager {

	private IDatabaseManager dbManager;
	private ISecurityManager securityManager;
	private IConnectivityManager connectivityManager;
	
	private PacketReceiveThread receiveThread;
	private ExecutorService packetSendThreadpool;
	
	private IncomingClientMessageParser parser;
	
	private HashMap<MessageType, IMessageHandler<? extends Message<ClientProtocolParameters>, ?>> handlers;
	private int minorVersion;
	private int majorVersion;
	
	public MessageManager(IDatabaseManager dbManager,
			ISecurityManager securityManager,
			IConnectivityManager connectivityManager,
			int minorVersion, int majorVersion) {
		this.dbManager = dbManager;
		this.securityManager = securityManager;
		this.connectivityManager = connectivityManager;
		this.parser = new IncomingClientMessageParser();
		this.handlers = new HashMap<Message.MessageType, IMessageHandler<? extends Message<ClientProtocolParameters>,?>>();
		this.minorVersion = minorVersion;
		this.majorVersion = majorVersion;
	}

	@Override
	public void initialize() throws Exception {
		receiveThread = new PacketReceiveThread(Executors.newFixedThreadPool(25), this);
		packetSendThreadpool = Executors.newFixedThreadPool(25);
		handlers.put(MessageType.AUTHENTICATION, new AuthenticationMessageHandler(securityManager));
		handlers.put(MessageType.NODE_LIST, new NodeListMessageHandler(dbManager, securityManager));
		handlers.put(MessageType.PING, new PingMessageHandler(connectivityManager));
		this.connectivityManager.setMessageManager(this);
	}

	@Override
	public void start() throws Exception {
		receiveThread.start();
	}

	@Override
	public void terminate() throws Exception {
		receiveThread.cancel();
	}

	@Override
	public void sendMessage(Message<ServerProtocolParameters> message) {
		sendMessage(message, null);
	}

	@Override
	public void sendMessage(final Message<ServerProtocolParameters> message, final MessageSentListener listener) {
		final String address = securityManager.getIpAddress(message.getParameters().sessionKey);
		packetSendThreadpool.submit(new Runnable() {
			@Override
			public void run() {
				try {
					Socket socket = new Socket(address, 6300);
					PrintWriter writer = new PrintWriter(socket.getOutputStream());
					StringBuilder xmlBuilder = new StringBuilder();
					message.toXml(xmlBuilder, 0);
					writer.print(xmlBuilder.toString());
					socket.close();
					if(listener != null) {
						listener.messageSent();
					}
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlFormatException e) {
					e.printStackTrace();
				}				
			}
		});		
	}

	@Override
	public void handleInput(Socket socket) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line;
			StringBuilder lineBuilder = new StringBuilder();
			while((line = reader.readLine()) != null) {
				lineBuilder.append(line);
			}
			String xml = lineBuilder.toString();
			Message<ClientProtocolParameters> message = parser.parse(xml);
			IMessageHandler handler = handlers.get(message.getMessageType());
			handler.handleMessage(majorVersion, minorVersion, securityManager.validateParameters(message.getParameters()), message, getParameters(message.getMessageType(), socket.getInetAddress().getHostAddress()));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlFormatException e) {
			e.printStackTrace();
		} catch (MessageFormatException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	private Object getParameters(MessageType messageType, String ipAddress) {
		switch (messageType) {
		case AUTHENTICATION:
			return new AuthenticationMessageHandlerParams(ipAddress);
		case COMMAND:
		case NODE_LIST:
		case PING:
		case STATUS_UPDATE:
		case WARNING:
		default:
			return null;
		}
	}
}
