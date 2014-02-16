package com.automate.server.messaging.handlers;

import com.automate.protocol.Message;
import com.automate.protocol.node.messages.NodeStatusUpdateMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerClientStatusUpdateMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.database.models.Node;
import com.automate.server.database.models.User;
import com.automate.server.security.ISecurityManager;

public class NodeStatusUpdateMessageHandler implements IMessageHandler<NodeStatusUpdateMessage, Void> {

	private IDatabaseManager dbManager;
	private ISecurityManager securityManager;

	public NodeStatusUpdateMessageHandler(IDatabaseManager dbManager,
			ISecurityManager securityManager) {
		super();
		this.dbManager = dbManager;
		this.securityManager = securityManager;
	}

	@Override
	public Message<ServerProtocolParameters> handleMessage(int majorVersion,
			int minorVersion, boolean sessionValid,
			NodeStatusUpdateMessage message, Void params) {
		Node node = dbManager.getNodeByUid(message.nodeId);
		User user = dbManager.getUserByUid(node.userId);
		String userSessionKey = securityManager.getSessionKeyForUsername(user.username);
		return new ServerClientStatusUpdateMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, userSessionKey), 
				message.nodeId, message.statuses);
	}

}
