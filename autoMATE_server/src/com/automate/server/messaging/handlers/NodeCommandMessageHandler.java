package com.automate.server.messaging.handlers;

import com.automate.protocol.Message;
import com.automate.protocol.node.messages.NodeCommandMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerClientCommandMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.security.ISecurityManager;

public class NodeCommandMessageHandler extends NodeToClientMessageHandler<NodeCommandMessage> {

	public NodeCommandMessageHandler(IDatabaseManager dbManager, ISecurityManager securityManager) {
		super(dbManager, securityManager);
	}

	@Override
	protected Message<ServerProtocolParameters> getUserOfflineMessage(int majorVersion, int minorVersion, boolean sessionValid,
			NodeCommandMessage message) {
		return null;
	}

	@Override
	protected Message<ServerProtocolParameters> getErrorMessage(int majorVersion, int minorVersion, boolean sessionValid,
			NodeCommandMessage message) {
		return null;
	}

	@Override
	protected Message<ServerProtocolParameters> getOkMessage(int majorVersion,int minorVersion, boolean sessionValid, 
			NodeCommandMessage message, String sessionKey) {
		return new ServerClientCommandMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, sessionKey), 
				message.commandId, message.responseCode, message.message);
	}

	@Override
	protected Message<ServerProtocolParameters> getUserNotFoundMessage(int majorVersion, int minorVersion, boolean sessionValid,
			NodeCommandMessage message) {
		return null;
	}

}
