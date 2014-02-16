package com.automate.protocol.server.messages;

import java.util.List;

import com.automate.protocol.Message;
import com.automate.protocol.models.Node;
import com.automate.protocol.models.Status;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.util.xml.XmlFormatException;

public class ServerNodeListMessage extends Message <ServerProtocolParameters> {

	public final List<Node> nodes;

	public ServerNodeListMessage(ServerProtocolParameters parameters, List<Node> nodes) {
		super(parameters);
		this.nodes = nodes;
	}

	@Override
	protected void addContent() throws XmlFormatException {
		if(nodes == null || nodes.size() == 0) {
			addElement("node-list", true);
		} else {			
			addElement("node-list", false);

			for(Node node : nodes) {
				node.toXml(this.builder, this.indentationLevel);
			}

			closeElement();
		}
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.NODE_LIST;
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.Message#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj)) {
			return	(this.nodes == null ? 
						(((ServerNodeListMessage)obj).nodes == null || ((ServerNodeListMessage)obj).nodes.isEmpty()) 
						: this.nodes.equals(((ServerNodeListMessage)obj).nodes));
		} else return false;
	}

	@Override
	public String toString() {
		String nodesString;
		if(nodes == null || nodes.isEmpty()) {
			nodesString = "";
		} else {
			StringBuilder nodesSb = new StringBuilder();
			for(Node node : nodes) {
				nodesSb.append(node);
				nodesSb.append('\n');
			}
			nodesString = nodesSb.toString();
		}
		return super.toString() + "\nServerNodeListMessage:\nnodes:\n" + nodesString;
	}

}
