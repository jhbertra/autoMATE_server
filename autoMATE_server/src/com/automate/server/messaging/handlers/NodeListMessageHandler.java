package com.automate.server.messaging.handlers;

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
	public Message<ServerProtocolParameters> handleMessage(
			ServerProtocolParameters responseParameters,
			ClientNodeListMessage message, Void params) {
		String username = securityManager.getUsername(message.getParameters().sessionKey);
		if(username == null) {
			return new ServerNodeListMessage(responseParameters, null);
		} else {
			List<Node> nodelist = dbManager.getClientNodeList(username);
			return new ServerNodeListMessage(responseParameters, nodelist);
		}
	}

}
