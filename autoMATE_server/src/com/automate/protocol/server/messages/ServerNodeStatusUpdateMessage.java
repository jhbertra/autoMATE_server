package com.automate.protocol.server.messages;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlFormatException;

/**
 * Represents a status update message sent from the server to a node
 * @author jamie.bertram
 *
 */
public class ServerNodeStatusUpdateMessage extends Message<ServerProtocolParameters> {

	/**
	 * The uid of the node
	 */
	public final long nodeId;
	
	/**
	 * Creates a new {@link ServerNodeStatusUpdateMessage}
	 * @param parameters the protocol parameters sent by the server
	 * @param nodeId the id of the node
	 * @throws IllegalArgumentException if nodeId < 0
	 */
	public ServerNodeStatusUpdateMessage(ServerProtocolParameters parameters, long nodeId) {
		super(parameters);
		if(nodeId < 0) {
			throw new IllegalArgumentException("nodeId less than zero. " + nodeId);
		}
		this.nodeId = nodeId;
	}

	@Override
	protected void addContent() throws XmlFormatException {
		addElement("status-update", true, new Attribute("node-id", String.valueOf(nodeId)));
	}

	@Override
	public com.automate.protocol.Message.MessageType getMessageType() {
		return MessageType.STATUS_UPDATE_CLIENT;
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
