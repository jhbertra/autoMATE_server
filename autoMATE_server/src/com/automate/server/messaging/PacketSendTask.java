package com.automate.server.messaging;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automate.protocol.Message;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.server.messaging.IMessageManager.MessageSentListener;

public class PacketSendTask implements Runnable {

	private Message<ServerProtocolParameters> message;
	private MessageSentListener listener;
	private Socket socket;
	
	private static final Logger logger = LogManager.getLogger();
	
	public PacketSendTask(Message<ServerProtocolParameters> message, MessageSentListener listener, Socket socket) {
		this.message = message;
		this.listener = listener;
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			System.out.println("Sending message.");
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			StringBuilder xmlBuilder = new StringBuilder();
			message.toXml(xmlBuilder, 0);
			String messageXml = xmlBuilder.toString();
			System.out.println("Message contents:\n" + xmlBuilder);
			writer.println(messageXml);
			writer.println("\0");
			writer.flush();
			System.out.println("Message sent.");
			if(listener != null) {
				listener.messageSent();
			}
		} catch (IOException e) {
			listener.messageDeliveryFailed(message);
		} catch (Exception e) {
			logger.error("Error sending message.", e);
		}
	}

}
