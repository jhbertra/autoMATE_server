package com.automate.io;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class PacketSender {

	public void sendPacket(String messageXml, String address, int port) {
		try {
			Socket socket = new Socket(address, port);
			byte[] packetBytes = messageXml.getBytes();
			socket.setSendBufferSize(packetBytes.length);
			sendPacket(messageXml, socket.getOutputStream());
			socket.close();
		} catch(IOException e) {
			//Log.e(LoggingTags.PACKET_IO, "Error sending packet.", e);
		}
	}

	public void sendPacket(String messageXml, OutputStream out) {
		try {
			byte[] packetBytes = messageXml.getBytes();
			out.write(packetBytes);
		} catch(IOException e) {
			//Log.e(LoggingTags.PACKET_IO, "Error sending packet.", e);
		}		
	}
}
