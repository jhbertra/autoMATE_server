package com.automate.server.messaging.handlers;

import com.automate.protocol.Message;
import com.automate.protocol.node.messages.NodeStatusUpdateMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerClientStatusUpdateMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.security.ISecurityManager;

public class NodeStatusUpdateMessageHandler extends NodeToClientMessageHandler<NodeStatusUpdateMessage> {

	public NodeStatusUpdateMessageHandler(IDatabaseManager dbManager, ISecurityManager securityManager) {
		super(dbManager, securityManager);
	}

	@Override
	protected Message<ServerProtocolParameters> getUserOfflineMessage(int majorVersion, int minorVersion, boolean sessionValid,
			NodeStatusUpdateMessage message) {
		return null;
	}

	@Override
	protected Message<ServerProtocolParameters> getErrorMessage(int majorVersion, int minorVersion, boolean sessionValid,
			NodeStatusUpdateMessage message) {
		return null;
	}

	@Override
	protected Message<ServerProtocolParameters> getOkMessage(int majorVersion, int minorVersion, boolean sessionValid,
			NodeStatusUpdateMessage message, String sessionKey, long nodeId) {
		return new ServerClientStatusUpdateMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, sessionKey),
				nodeId, message.statuses);
	}

	@Override
	protected Message<ServerProtocolParameters> getUserNotFoundMessage(int majorVersion, int minorVersion, boolean sessionValid,
			NodeStatusUpdateMessage message) {
		return null;
	}

}
