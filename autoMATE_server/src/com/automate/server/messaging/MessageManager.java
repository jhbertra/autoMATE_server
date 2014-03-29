package com.automate.server.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automate.protocol.IIncomingMessageParser;
import com.automate.protocol.Message;
import com.automate.protocol.Message.MessageType;
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
	private boolean terminated;

	private static final Logger logger = LogManager.getLogger();

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
		logger.info("initializing message manager...");
		this.connectivityManager.setMessageManager(this);
		this.receiveThread.setManager(this);
	}

	@Override
	public void start() throws Exception {
		if(terminated) throw new IllegalStateException("Cannot restart a terminated MessageManager.");
		logger.info("starting message manager...");
		receiveThread.start();
	}

	@Override
	public void terminate() throws Exception {
		logger.info("Shutting down message manager...");
		receiveThread.cancel();
		packetSendThreadpool.shutdown();
		this.terminated = true;
	}

	@Override
	public void sendMessage(Message<ServerProtocolParameters> message) {
		sendMessage(message, null);
	}

	@Override
	public void sendMessage(final Message<ServerProtocolParameters> message, final MessageSentListener listener) {
		if(terminated) return;
		if(message == null) {
			throw new NullPointerException("message was null.");
		}
		final Socket socket = securityManager.getSocket(message.getParameters().sessionKey);
		if(socket == null) {
			System.out.println("No socket");
			return;
		}
		sendMessage(message, listener, socket);
		securityManager.returnSocket(message.getParameters().sessionKey);
	}

	private void sendMessage(final Message<ServerProtocolParameters> message, final MessageSentListener listener, Socket socket) {
		System.out.println("Address: " + socket.getInetAddress().getHostAddress());
		logger.info("Sending {} message to {}.", message.getMessageType().toString(), 
				securityManager.getUsername(message.getParameters().sessionKey));
		try {
			packetSendThreadpool.submit(new PacketSendTask(message, listener, socket));
		} catch(RejectedExecutionException e) {
			logger.warn("Message was not sent - manager shut down.");
		}
	}

	@Override
	public void handleInput(BufferedReader reader, final Socket socket) {
		if(reader == null) {
			throw new NullPointerException("reader was null.");
		}
		if(socket == null) {
			throw new NullPointerException("socket was null.");
		}
		try {
			System.out.println("Receiving message.");
			String line;
			StringBuilder lineBuilder = new StringBuilder();
			while(!(line = reader.readLine()).equals("\0")) {
				lineBuilder.append(line);
				lineBuilder.append('\n');
			}
			String xml = lineBuilder.toString();
			System.out.println("Message contents:\n" + xml);
			if(xml == null) {
				throw new NullPointerException("attempted to handle null input.");
			} else if(xml.isEmpty()) {
				throw new IllegalArgumentException("the input received had no content.");
			}
			Message<ClientProtocolParameters> message = parser.parse(xml);
			if(message == null) {
				throw new NullPointerException("parser returned null message.");
			}
			logger.info("Received {} message from {}.", message.getMessageType().toString(), 
					securityManager.getUsername(message.getParameters().sessionKey));
			IMessageHandler handler = handlers.get(message.getMessageType());
			if(handler == null) {
				throw new NullPointerException("No handler registered for message type " + message.getMessageType() + ".");
			}
			Message<ServerProtocolParameters> responseMessage = handler.handleMessage(majorVersion, minorVersion, 
					securityManager.validateParameters(message.getParameters()), message, getParameters(message, socket));
			if(responseMessage != null) {
				if(!message.getParameters().sessionKey.isEmpty() && !message.getParameters().sessionKey.equalsIgnoreCase("null")) {					
					securityManager.updateSocket(message.getParameters().sessionKey, socket);
				}
				if(!responseMessage.getParameters().sessionKey.isEmpty()) {
					sendMessage(responseMessage);
				} else {
					sendMessage(responseMessage, new MessageSentListener() {
						@Override
						public void messageSent() {
							PrintWriter writer;
							try {
								writer = new PrintWriter(socket.getOutputStream());
								writer.println("EOF");
								writer.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void messageDeliveryFailed(Message<ServerProtocolParameters> message) {
						}
					}, socket);
				}
			}
		} catch (Exception e) {
			logger.error("Error handling received message.", e);
		}
	}

	private Object getParameters(Message<ClientProtocolParameters> message, Socket socket) {
		switch (message.getMessageType()) {
		case AUTHENTICATION:
			return new AuthenticationMessageHandlerParams(socket);
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
