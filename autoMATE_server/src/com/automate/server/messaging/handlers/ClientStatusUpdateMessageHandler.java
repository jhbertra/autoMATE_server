package com.automate.server.messaging.handlers;

import com.automate.protocol.Message;
import com.automate.protocol.client.messages.ClientStatusUpdateMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerClientStatusUpdateMessage;
import com.automate.protocol.server.messages.ServerNodeStatusUpdateMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.security.ISecurityManager;

public class ClientStatusUpdateMessageHandler extends ClientToNodeMessageHandler<ClientStatusUpdateMessage> {

	private long nodeId;
	
	public ClientStatusUpdateMessageHandler(IDatabaseManager dbManager, ISecurityManager securityManager) {
		super(dbManager, securityManager);
	}

	@Override
	public Message<ServerProtocolParameters> handleMessage(int majorVersion, int minorVersion, boolean sessionValid,
			ClientStatusUpdateMessage message, ClientToNodeMessageHandlerParams params) {
		nodeId = message.nodeId;

		Message<ServerProtocolParameters> retValue = super.handleMessage(majorVersion, minorVersion, sessionValid, message, params);
		
		nodeId = -1;
		
		return retValue;
	}

	@Override
	protected Message<ServerProtocolParameters> getNonExistentNodeMessage(int majorVersion, int minorVersion, boolean sessionValid,
			ClientStatusUpdateMessage message) {
		return new ServerClientStatusUpdateMessage(
				new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, message.getParameters().sessionKey), nodeId, null);
	}

	@Override
	protected Message<ServerProtocolParameters> getNotOwnedNodeMessage(int majorVersion, int minorVersion, boolean sessionValid,
			ClientStatusUpdateMessage message) {
		return new ServerClientStatusUpdateMessage(
				new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, message.getParameters().sessionKey), nodeId, null);
	}

	@Override
	protected Message<ServerProtocolParameters> getNodeOfflineMessage(int majorVersion, int minorVersion, boolean sessionValid,
			ClientStatusUpdateMessage message) {
		return new ServerClientStatusUpdateMessage(
				new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, message.getParameters().sessionKey), nodeId, null);
	}

	@Override
	protected Message<ServerProtocolParameters> getOkMessage(int majorVersion, int minorVersion, boolean sessionValid,
			ClientStatusUpdateMessage message, String nodeSessionKey) {
		return new ServerNodeStatusUpdateMessage(
				new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, nodeSessionKey), nodeId);
	}

	@Override
	protected Message<ServerProtocolParameters> getErrorMessage(int majorVersion, int minorVersion, boolean sessionValid,
			ClientStatusUpdateMessage message) {
		return new ServerClientStatusUpdateMessage(
				new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, message.getParameters().sessionKey), nodeId, null);
	}

}
