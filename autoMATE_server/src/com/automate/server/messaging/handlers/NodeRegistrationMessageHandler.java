package com.automate.server.messaging.handlers;

import com.automate.protocol.Message;
import com.automate.protocol.client.messages.ClientNodeRegistrationMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerNodeRegistrationMessage;
import com.automate.server.security.ISecurityManager;

public class NodeRegistrationMessageHandler implements
		IMessageHandler<ClientNodeRegistrationMessage, Void> {

	/**
	 * The security manager.
	 */
	private ISecurityManager securityManager;
	
	public NodeRegistrationMessageHandler(ISecurityManager securityManager) {
		if(securityManager == null) {
			throw new NullPointerException("Authentication message handler requires an instance of ISecurityManager.");
		}
		this.securityManager = securityManager;
	}
	
	@Override
	public Message<ServerProtocolParameters> handleMessage(int majorVersion, int minorVersion, boolean sessionValid,
			ClientNodeRegistrationMessage message, Void params) {
		if(message == null) {
			throw new NullPointerException("message was null.");
		}
		String username = securityManager.getUsername(message.getParameters().sessionKey);
		long nodeId = -1;
		String password = null;
		if(username != null) {
			nodeId = securityManager.registerNodeForUser(message.maxVersion, message.modelId, message.name, username);
			if(nodeId != -1) {
				password = securityManager.generateNodePassword(nodeId);
			}
		}
		return new ServerNodeRegistrationMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, 
				message.getParameters().sessionKey), nodeId, password);
	}

}
