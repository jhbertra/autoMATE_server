package com.automate.protocol.client.messages;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;

/**
 * Represents a node-list message sent from a client
 * @author jamie.bertram
 *
 */
public class ClientNodeListMessage extends Message <ClientProtocolParameters> {

	/**
	 * Created a new {@link ClientNodeListMessage}
	 * @param parameters
	 */
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

	@Override
	public String toString() {
		return super.toString() + "\nClientNodeListMessage";
	}
	
}
