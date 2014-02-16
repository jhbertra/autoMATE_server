package com.automate.server.messaging.handlers;

import com.automate.protocol.Message;
import com.automate.protocol.node.messages.NodeCommandMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerClientCommandMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.database.models.Node;
import com.automate.server.database.models.User;
import com.automate.server.security.ISecurityManager;

public class NodeCommandMessageHandler implements IMessageHandler<NodeCommandMessage, Void> {

	private IDatabaseManager dbManager;
	private ISecurityManager securityManager;
	
	@Override
	public Message<ServerProtocolParameters> handleMessage(int minorVersion, int majorVersion, boolean sessionValid, 
			NodeCommandMessage message, Void params) {
		long commandId = message.commandId;
		String resultMessage = message.message;
		int responseCode = message.responseCode;
		
		long nodeId = securityManager.getNodeId(message.getParameters().sessionKey);
		Node node = dbManager.getNodeByUid(nodeId);
		User user = dbManager.getUserByUid(node.userId);
		String userSessionKey = securityManager.getSessionKeyForUsername(user.username);
		
		return new ServerClientCommandMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, userSessionKey), 
				commandId, responseCode, resultMessage);
	}

}
