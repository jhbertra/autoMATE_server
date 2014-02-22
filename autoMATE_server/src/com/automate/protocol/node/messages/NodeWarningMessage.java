package com.automate.protocol.node.messages;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.server.messages.ServerClientWarningMessage;
import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlFormatException;

public class NodeWarningMessage extends Message<ClientProtocolParameters> {
	
	public final long nodeId;
	public final String message;
	
	public NodeWarningMessage(ClientProtocolParameters parameters, long nodeId, String message) {
		super(parameters);
		if(message == null) {
			throw new NullPointerException("message was null in ServerWarningMessage");
		}
		this.nodeId = nodeId;
		this.message = message;
	}

	@Override
	protected void addContent() throws XmlFormatException {
		addElement("warning", true
				, new Attribute("node-id", String.valueOf(nodeId))
				, new Attribute("message", message));
	}

	@Override
	public com.automate.protocol.Message.MessageType getMessageType() {
		return MessageType.WARNING_NODE;
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.Message#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj)) {
			return 	this.nodeId == ((ServerClientWarningMessage)obj).nodeId
					&& this.message.equals(((ServerClientWarningMessage)obj).message);
		} else return false;
	}

	@Override
	public String toString() {
		return super.toString() + "\nNodeWarningMessage:\nnodeId: " + nodeId + "\nmessage: " + message;
	}

}
