package com.automate.server.messaging.handlers;

import com.automate.protocol.Message;
import com.automate.protocol.client.messages.ClientAuthenticationMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerAuthenticationMessage;
import com.automate.server.messaging.IMessageHandler;
import com.automate.server.security.ISecurityManager;

public class AuthenticationMessageHandler implements IMessageHandler<ClientAuthenticationMessage, AuthenticationMessageHandlerParams> {

	private ISecurityManager securityManager;
	
	public AuthenticationMessageHandler(ISecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	@Override
	public Message<ServerProtocolParameters> handleMessage(ServerProtocolParameters responseParameters, 
			ClientAuthenticationMessage message, AuthenticationMessageHandlerParams params) {
		try {
			String sessionKey = securityManager.authenticateClient(message.username, 
					message.password, message.getParameters().sessionKey, params.clientIpAddress);
			if(sessionKey != null) {
				return new ServerAuthenticationMessage(responseParameters, message.username, 200, "OK", sessionKey);
			} else {
				return new ServerAuthenticationMessage(responseParameters, message.username, 400, "DENIED", "");
			}
		} catch(Throwable t) {
			return new ServerAuthenticationMessage(responseParameters, message.username, 500, "INTERNAL SERVER ERROR", "");
		}
	}
	
}
