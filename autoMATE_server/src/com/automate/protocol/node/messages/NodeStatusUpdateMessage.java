package com.automate.protocol.node.messages;

import java.util.List;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.models.CommandArgument;
import com.automate.protocol.models.Status;
import com.automate.protocol.server.messages.ServerClientStatusUpdateMessage;
import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlFormatException;

public class NodeStatusUpdateMessage extends Message<ClientProtocolParameters> {
	
	public final long nodeId;
	
	public final List<Status<?>> statuses;
	
	public NodeStatusUpdateMessage(ClientProtocolParameters parameters, long nodeId, List<Status<?>> statuses) {
		super(parameters);
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
					&& (this.statuses == null ? 
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
		return super.toString() + "\nNodeStatusUpdateMessage:\nnodeId: " + nodeId + "\nstatuses: " + statusesString;
	}
	
}
