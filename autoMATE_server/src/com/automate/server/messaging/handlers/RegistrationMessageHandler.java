package com.automate.server.messaging.handlers;

import com.automate.protocol.Message;
import com.automate.protocol.client.messages.ClientRegistrationMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerAuthenticationMessage;
import com.automate.server.database.models.User;
import com.automate.server.security.ISecurityManager;

public class RegistrationMessageHandler implements
		IMessageHandler<ClientRegistrationMessage, AuthenticationMessageHandlerParams> {

	/**
	 * The security manager that manages client sessions.
	 */
	private ISecurityManager securityManager;
	
	public RegistrationMessageHandler(ISecurityManager securityManager) {
		if(securityManager == null) {
			throw new NullPointerException("Authentication message handler requires an instance of ISecurityManager.");
		}
		this.securityManager = securityManager;
	}
	
	@Override
	public Message<ServerProtocolParameters> handleMessage(int majorVersion, int minorVersion, boolean sessionValid, 
			ClientRegistrationMessage message, AuthenticationMessageHandlerParams params) {
		if(message == null) {
			throw new NullPointerException("message was null.");
		} else if (params == null) {
			throw new NullPointerException("params was null.");
		}
		try {
			int result = securityManager.registerNewUser(message.username, message.password, message.name, message.email);
			if(result == 200) {
				String sessionKey = securityManager.authenticateClient(message.username, 
						message.password, message.getParameters().sessionKey, params.clientSocket);
				if(sessionKey != null && !sessionKey.isEmpty()) {
					return new ServerAuthenticationMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, sessionKey), 
							message.username, 200, "OK", sessionKey);
				} else {
					return new ServerAuthenticationMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, ""), 
							message.username, 400, "DENIED", "");
				}
			} else if (result == 401) {
				return new ServerAuthenticationMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, ""), 
						message.username, 401, "USER_ALREADY_EXISTS", "");
			} else {
				return new ServerAuthenticationMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, ""), 
						message.username, 500, "INTERNAL SERVER ERROR", "");
			}
		} catch(Throwable t) {
			return new ServerAuthenticationMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, ""), 
					message.username, 500, "INTERNAL SERVER ERROR", "");
		}
	}

}
