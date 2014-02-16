package com.automate.protocol.client.messages;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlFormatException;

public class ClientStatusUpdateMessage extends Message<ClientProtocolParameters> {

	public final long nodeId;
	
	public ClientStatusUpdateMessage(ClientProtocolParameters parameters, long nodeId) {
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
			return 	this.nodeId == ((ClientStatusUpdateMessage)obj).nodeId;
		} else return false;
	}

	@Override
	public String toString() {
		return super.toString() + "\nClientStatusUpdateMessage:\nnodeId: " + nodeId;
	}

}
