package com.automate.protocol;

import java.io.OutputStream;

import com.automate.io.PacketReceiver;
import com.automate.io.PacketSender;
import com.automate.protocol.client.messages.ClientPingMessage;
import com.automate.util.xml.XmlFormatException;

public class ProtocolManager {

	private AddressProvider addressProvider;
	private PacketReceiver<?> packetReciever;
	private PacketSender packetSender;
	
	private boolean terminated;
	private int protocolPort;
	
	public ProtocolManager(AddressProvider addressProvider,
			PacketReceiver<?> packetReciever, PacketSender packetSender, int port) {
		this.addressProvider = addressProvider;
		this.packetReciever = packetReciever;
		this.packetSender = packetSender;
		this.protocolPort = port;
	}

	public void start() {
		packetReciever.start();
	}
	
	public void sendMessage(Message<?> message, String destinationUid) {
		if(terminated) return;
		String address = addressProvider.getAddress(destinationUid);
		try {
			StringBuilder builder = new StringBuilder();
			message.constructXml(builder, 0);
			String messageXml = builder.toString();
			packetSender.sendPacket(messageXml, address, protocolPort);
		} catch (XmlFormatException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		packetReciever.terminate();
		terminated = true;
	}

	public void sendMessage(ClientPingMessage message, OutputStream out) {
		if(terminated) return;
		try {
			StringBuilder builder = new StringBuilder();
			message.constructXml(builder, 0);
			String messageXml = builder.toString();
			packetSender.sendPacket(messageXml, out);
		} catch (XmlFormatException e) {
			e.printStackTrace();
		}
	}

}
