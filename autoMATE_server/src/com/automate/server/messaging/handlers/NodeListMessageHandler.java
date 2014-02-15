package com.automate.server.messaging.handlers;

import java.util.ArrayList;
import java.util.List;

import com.automate.protocol.Message;
import com.automate.protocol.client.messages.ClientNodeListMessage;
import com.automate.protocol.models.Node;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerNodeListMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.security.ISecurityManager;

public class NodeListMessageHandler implements
		IMessageHandler<ClientNodeListMessage, Void> {

	private IDatabaseManager dbManager;
	private ISecurityManager securityManager;
	
	public NodeListMessageHandler(IDatabaseManager dbManager,
			ISecurityManager securityManager) {
		this.dbManager = dbManager;
		this.securityManager = securityManager;
	}

	@Override
	public Message<ServerProtocolParameters> handleMessage(int minorVersion,
			int majorVersion, boolean sessionValid,
			ClientNodeListMessage message, Void params) {
		ServerProtocolParameters responseParameters = new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, 
				message.getParameters().sessionKey);
		String username = securityManager.getUsername(message.getParameters().sessionKey);
		if(username == null) {
			return new ServerNodeListMessage(responseParameters, null);
		} else {
			List<com.automate.server.database.models.Node> nodes = dbManager.getClientNodeList(username);
			
			List<Node> nodelist = new ArrayList<Node>(nodes.size());
			for(com.automate.server.database.models.Node node : nodes) {
				nodelist.add(node.toProtocolNode(dbManager));
			}
			return new ServerNodeListMessage(responseParameters, nodelist);
		}
	}

}
