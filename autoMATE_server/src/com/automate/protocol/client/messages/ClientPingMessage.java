package com.automate.protocol.client.messages;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;

/**
 * Represents a ping message from a client.
 * @author jamie.bertram
 *
 */
public class ClientPingMessage extends Message<ClientProtocolParameters> {

	/**
	 * Creates a new {@link ClientPingMessage}
	 * @param parameters
	 */
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
