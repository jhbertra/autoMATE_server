package com.automate.protocol.server.messages;

import com.automate.protocol.Message;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlFormatException;

public class ServerClientCommandMessage extends Message<ServerProtocolParameters> {

	public final long commandId;
	public final int responseCode;
	public final String message;
	
	public ServerClientCommandMessage(ServerProtocolParameters parameters, long commandId, int responseCode, String message) {
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
			return	this.commandId == ((ServerClientCommandMessage)obj).commandId
					&& this.responseCode == ((ServerClientCommandMessage)obj).responseCode
					&& (this.message == null ? 
						((ServerClientCommandMessage)obj).message == null 
						: this.message.equals(((ServerClientCommandMessage)obj).message));
		} else return false;
	}

	@Override
	public String toString() {
		return super.toString() + "\nServerClientCommandMessage:\n" + "\ncommandId: " + commandId + "\nresponseCode: " + 
					responseCode + "\nmessage: " + message;
	}

}
