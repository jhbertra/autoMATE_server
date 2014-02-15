package com.automate.protocol.client.messages;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;

public class ClientNodeListMessage extends Message <ClientProtocolParameters> {

	public ClientNodeListMessage(ClientProtocolParameters parameters) {
		super(parameters);
	}

	@Override
	protected void addContent() {
		addElement("node-list", true);
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.NODE_LIST;
	}
	
}
