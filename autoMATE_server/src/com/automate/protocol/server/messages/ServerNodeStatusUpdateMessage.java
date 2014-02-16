package com.automate.protocol.server.messages;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlFormatException;

public class ServerNodeStatusUpdateMessage extends Message<ServerProtocolParameters> {

	public final long nodeId;
	
	public ServerNodeStatusUpdateMessage(ServerProtocolParameters parameters, long nodeId) {
		super(parameters);
		this.nodeId = nodeId;
	}

	@Override
	protected void addContent() throws XmlFormatException {
		addElement("status-update", true, new Attribute("node-id", String.valueOf(nodeId)));
	}

	@Override
	public com.automate.protocol.Message.MessageType getMessageType() {
		return MessageType.STATUS_UPDATE;
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.Message#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj)) {
			return 	this.nodeId == ((ServerNodeStatusUpdateMessage)obj).nodeId;
		} else return false;
	}

	@Override
	public String toString() {
		return super.toString() + "\nServerNodeStatusUpdateMessage:\nnodeId: " + nodeId;
	}

}
