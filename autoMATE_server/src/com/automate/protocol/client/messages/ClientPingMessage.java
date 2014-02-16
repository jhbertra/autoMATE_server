package com.automate.protocol.client.messages;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;

public class ClientPingMessage extends Message<ClientProtocolParameters> {

	public ClientPingMessage(ClientProtocolParameters parameters) {
		super(parameters);
	}

	@Override
	protected void addContent() {
		addElement("ping", true);
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.PING;
	}

	@Override
	public String toString() {
		return super.toString() + "\nClientPingMessage";
	}
	
}
