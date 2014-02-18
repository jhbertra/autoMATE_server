package com.automate.protocol.server.messages;

import com.automate.protocol.Message;
import com.automate.protocol.server.ServerProtocolParameters;

/**
 * Represents a ping message sent from the server
 * @author jamie.bertram
 *
 */
public class ServerPingMessage extends Message<ServerProtocolParameters> {

	/**
	 * Creates a new {@link ServerPingMessage}
	 * @param parameters the protocol parameters sent by the server.
	 */
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

	@Override
	public String toString() {
		return super.toString() + "\nServerPingMessage";
	}
	
}
