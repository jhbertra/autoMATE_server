package com.automate.server.messaging;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.automate.protocol.Message;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.server.messaging.IMessageManager.MessageSentListener;
import com.automate.util.xml.XmlFormatException;

public class PacketSendTask implements Runnable {

	private Message<ServerProtocolParameters> message;
	private MessageSentListener listener;
	private ISocket socket;
	
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

}
