package com.automate.protocol.server.messages;

import com.automate.protocol.Message;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlFormatException;

public class ServerWarningMessage extends Message<ServerProtocolParameters> {

	public final long warningId;
	public final long nodeId;
	public final String message;
	
	public ServerWarningMessage(ServerProtocolParameters parameters, long warningId, long nodeId, String message) {
		super(parameters);
		if(message == null) {
			throw new NullPointerException("message was null in ServerWarningMessage");
		}
		this.warningId = warningId;
		this.nodeId = nodeId;
		this.message = message;
	}

	@Override
	protected void addContent() throws XmlFormatException {
		addElement("warning", true
				, new Attribute("warning-id", String.valueOf(warningId))
				, new Attribute("node-id", String.valueOf(nodeId))
				, new Attribute("message", message));
	}

	@Override
	public com.automate.protocol.Message.MessageType getMessageType() {
		return MessageType.WARNING;
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.Message#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj)) {
			return 	this.warningId == ((ServerWarningMessage)obj).warningId
					&& this.nodeId == ((ServerWarningMessage)obj).nodeId
					&& this.message.equals(((ServerWarningMessage)obj).message);
		} else return false;
	}

}
