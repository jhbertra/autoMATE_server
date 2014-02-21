package com.automate.server.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.automate.protocol.IIncomingMessageParser;
import com.automate.protocol.Message;
import com.automate.protocol.Message.MessageType;
import com.automate.protocol.MessageFormatException;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.client.messages.ClientCommandMessage;
import com.automate.protocol.client.messages.ClientStatusUpdateMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.server.InitializationException;
import com.automate.server.connectivity.IConnectivityManager;
import com.automate.server.messaging.handlers.AuthenticationMessageHandlerParams;
import com.automate.server.messaging.handlers.ClientToNodeMessageHandlerParams;
import com.automate.server.messaging.handlers.IMessageHandler;
import com.automate.server.security.ISecurityManager;
import com.automate.util.xml.XmlFormatException;

public class MessageManager implements IMessageManager {

	private ISecurityManager securityManager;
	private IConnectivityManager connectivityManager;
	
	private IPacketReceiveThread receiveThread;
	private ExecutorService packetSendThreadpool;
	
	private IIncomingMessageParser<ClientProtocolParameters> parser;
	
	private ISocket.Factory socketFactory;
	
	private HashMap<MessageType, IMessageHandler<? extends Message<ClientProtocolParameters>, ?>> handlers;
	private int minorVersion;
	private int majorVersion;

	public MessageManager(
			ISecurityManager securityManager,
			IConnectivityManager connectivityManager,
			IPacketReceiveThread receiveThread,
			ExecutorService packetSendThreadpool,
			IIncomingMessageParser<ClientProtocolParameters> parser,
			HashMap<MessageType, IMessageHandler<? extends Message<ClientProtocolParameters>, ?>> handlers,
			ISocket.Factory socketFactory,
			int majorVersion, int minorVersion) {
		this.securityManager = securityManager;
		this.connectivityManager = connectivityManager;
		this.receiveThread = receiveThread;
		this.packetSendThreadpool = packetSendThreadpool;
		this.parser = parser;
		this.handlers = handlers;
		this.socketFactory = socketFactory;
		this.minorVersion = minorVersion;
		this.majorVersion = majorVersion;
	}

	@Override
	public void initialize()  throws InitializationException {
		this.connectivityManager.setMessageManager(this);
		this.receiveThread.setManager(this);
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
		if(message == null) {
			throw new NullPointerException("message was null.");
		}
		final String address = securityManager.getIpAddress(message.getParameters().sessionKey);
		if(address == null || address.isEmpty()) {
			return;
		}
		packetSendThreadpool.submit(new PacketSendTask(message, listener, this.socketFactory.newInstance(address, 6300)));		
	}

	@Override
	public void handleInput(BufferedReader reader, String hostAddress) {
		if(reader == null) {
			throw new NullPointerException("reader was null.");
		}
		if(hostAddress == null) {
			throw new NullPointerException("host address was null.");
		}
		try {
			String line;
			StringBuilder lineBuilder = new StringBuilder();
			while((line = reader.readLine()) != null) {
				lineBuilder.append(line);
			}
			String xml = lineBuilder.toString();
			if(xml == null) {
				throw new NullPointerException("attempted to handle null input.");
			} else if(xml.isEmpty()) {
				throw new IllegalArgumentException("the input received had no content.");
			}
			Message<ClientProtocolParameters> message = parser.parse(xml);
			if(message == null) {
				throw new NullPointerException("parser returned null message.");
			}
			IMessageHandler handler = handlers.get(message.getMessageType());
			Message<ServerProtocolParameters> responseMessage = handler.handleMessage(majorVersion, minorVersion, 
					securityManager.validateParameters(message.getParameters()), message, getParameters(message, hostAddress));
			if(responseMessage != null) {
				sendMessage(responseMessage);
			}
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
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	private Object getParameters(Message<ClientProtocolParameters> message, String ipAddress) {
		switch (message.getMessageType()) {
		case AUTHENTICATION:
			return new AuthenticationMessageHandlerParams(ipAddress);
		case COMMAND_CLIENT:
			long nodeId = ((ClientCommandMessage)message).nodeId;
			return new ClientToNodeMessageHandlerParams(nodeId);
		case STATUS_UPDATE_CLIENT:
			nodeId = ((ClientStatusUpdateMessage)message).nodeId;
			return new ClientToNodeMessageHandlerParams(nodeId);
		case COMMAND_NODE:
		case STATUS_UPDATE_NODE:
		case WARNING_NODE:
		case WARNING_CLIENT:
		case NODE_LIST:
		case PING:
		default:
			return null;
		}
	}
}
