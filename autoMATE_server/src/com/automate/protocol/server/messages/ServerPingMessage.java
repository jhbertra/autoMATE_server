package com.automate.protocol.server.messages;

import com.automate.protocol.Message;
import com.automate.protocol.server.ServerProtocolParameters;

public class ServerPingMessage extends Message<ServerProtocolParameters> {

	public ServerPingMessage(ServerProtocolParameters parameters) {
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
	
}
