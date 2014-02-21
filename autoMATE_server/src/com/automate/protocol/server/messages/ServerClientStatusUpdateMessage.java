package com.automate.protocol.server.messages;

import java.util.List;

import com.automate.protocol.Message;
import com.automate.protocol.models.Status;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlFormatException;

/**
 * Represents a status update message sent from the server to the client.
 * @author jamie.bertram
 *
 */
public class ServerClientStatusUpdateMessage extends Message<ServerProtocolParameters> {
	
	/**
	 * The id of the node whose status was requested.
	 */
	public final long nodeId;
	
	/**
	 * The list of statuses (may be null or empty)
	 */
	public final List<Status<?>> statuses;
	
	/**
	 * Creates a new {@link ServerClientStatusUpdateMessage}
	 * @param parameters the protocol parameters from the server
	 * @param nodeId the id of the node
	 * @param statuses the statuses from the node
	 * @throws IllegalArgumentException if nodeId < 0
	 */
	public ServerClientStatusUpdateMessage(ServerProtocolParameters parameters, long nodeId, List<Status<?>> statuses) {
		super(parameters);
		if(nodeId < 0) {
			throw new IllegalArgumentException("nodeId invalid: " + nodeId);
		}
		this.nodeId = nodeId;
		this.statuses = statuses;
	}

	@Override
	protected void addContent() throws XmlFormatException {
		if(statuses == null || statuses.size() == 0) {
			addElement("status-update", true, new Attribute("node-id", String.valueOf(nodeId)));
		} else {
			addElement("status-update", false, new Attribute("node-id", String.valueOf(nodeId)));
			
			for(Status<?> status : statuses) {
				status.toXml(this.builder, this.indentationLevel);
			}
			
			closeElement();			
		}
	}

	@Override
	public com.automate.protocol.Message.MessageType getMessageType() {
		return MessageType.STATUS_UPDATE_NODE;
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.Message#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj)) {
			return	this.nodeId == ((ServerClientStatusUpdateMessage)obj).nodeId
					&& ((this.statuses == null || this.statuses.isEmpty()) ? 
						(((ServerClientStatusUpdateMessage)obj).statuses == null || ((ServerClientStatusUpdateMessage)obj).statuses.isEmpty()) 
						: this.statuses.equals(((ServerClientStatusUpdateMessage)obj).statuses));
		} else return false;
	}

	@Override
	public String toString() {
		String statusesString;
		if(statuses == null || statuses.isEmpty()) {
			statusesString = "";
		} else {
			StringBuilder statusesSb = new StringBuilder();
			for(Status<?> status : statuses) {
				statusesSb.append(status);
				statusesSb.append('\n');
			}
			statusesString = statusesSb.toString();
		}
		return super.toString() + "\nServerClientStatusUpdateMessage:\nnodeId: " + nodeId + "\nstatuses: " + statusesString;
	}
}
