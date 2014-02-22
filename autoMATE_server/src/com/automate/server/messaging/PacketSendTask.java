package com.automate.server.messaging;

import java.io.PrintWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automate.protocol.Message;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.server.messaging.IMessageManager.MessageSentListener;

public class PacketSendTask implements Runnable {

	private Message<ServerProtocolParameters> message;
	private MessageSentListener listener;
	private ISocket socket;
	
	private static final Logger logger = LogManager.getLogger();
	
	public PacketSendTask(Message<ServerProtocolParameters> message, MessageSentListener listener, ISocket socket) {
		this.message = message;
		this.listener = listener;
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			socket.connect();
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			StringBuilder xmlBuilder = new StringBuilder();
			message.toXml(xmlBuilder, 0);
			String messageXml = xmlBuilder.toString();
			logger.trace("Message contents:\n{}", xmlBuilder);
			writer.print(messageXml);
			socket.close();
			if(listener != null) {
				listener.messageSent();
			}
		} catch (Exception e) {
			logger.error("Error sending message.", e);
		}
	}

}
