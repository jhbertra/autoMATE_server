package com.automate.protocol.node.messages;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlFormatException;

public class NodeCommandMessage extends Message<ClientProtocolParameters> {

	public final long commandId;
	public final int responseCode;
	public final String message;
	
	public NodeCommandMessage(ClientProtocolParameters parameters, long commandId, int responseCode, String message) {
		super(parameters);
		if(commandId < 0) {
			throw new IllegalArgumentException("command-id is negative.");
		}
		this.commandId = commandId;
		this.responseCode = responseCode;
		this.message = message;
	}

	@Override
	protected void addContent() throws XmlFormatException {
		if(message == null) {
			addElement("command", true
					, new Attribute("command-id", String.valueOf(commandId))
					, new Attribute("response-code", String.valueOf(responseCode)));
		} else {
			addElement("command", true
					, new Attribute("command-id", String.valueOf(commandId))
					, new Attribute("response-code", String.valueOf(responseCode))
					, new Attribute("message", message));
		}
	}

	@Override
	public com.automate.protocol.Message.MessageType getMessageType() {
		return MessageType.COMMAND;
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.Message#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj)) {
			return	this.commandId == ((NodeCommandMessage)obj).commandId
					&& this.responseCode == ((NodeCommandMessage)obj).responseCode
					&& (this.message == null ? 
						((NodeCommandMessage)obj).message == null 
						: this.message.equals(((NodeCommandMessage)obj).message));
		} else return false;
	}

}
