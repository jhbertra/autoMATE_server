package com.automate.server.messaging.handlers;

import com.automate.protocol.Message;
import com.automate.protocol.client.messages.ClientStatusUpdateMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerNodeStatusUpdateMessage;
import com.automate.server.security.ISecurityManager;

public class ClientStatusUpdateMessageHandler implements
		IMessageHandler<ClientStatusUpdateMessage, Void> {

	ISecurityManager securityManager;
	
	@Override
	public Message<ServerProtocolParameters> handleMessage(int minorVersion,
			int majorVersion, boolean sessionValid,
			ClientStatusUpdateMessage message, Void params) {
		long nodeId = message.nodeId;

		String sessionKey = securityManager.getSessionKeyForNode(nodeId);
		
		return new ServerNodeStatusUpdateMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, sessionKey), nodeId);
	}

}
