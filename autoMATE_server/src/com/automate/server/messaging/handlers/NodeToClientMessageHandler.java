package com.automate.server.messaging.handlers;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.database.models.Node;
import com.automate.server.database.models.User;
import com.automate.server.security.ISecurityManager;

/**
 * Handles generic messages that pass from Node > Server > Client
 *
 */
public abstract class NodeToClientMessageHandler<M extends Message<ClientProtocolParameters>> 
implements IMessageHandler<M, NodeToClientMessageHandlerParams> {

	private IDatabaseManager dbManager;
	private ISecurityManager securityManager;
	
	TestAccess testAccess = new TestAccess();
	
	public NodeToClientMessageHandler(IDatabaseManager dbManager, ISecurityManager securityManager) {
		this.dbManager = dbManager;
		this.securityManager = securityManager;

	}

	@Override
	public Message<ServerProtocolParameters> handleMessage(int majorVersion, int minorVersion, boolean sessionValid, 
			M message, NodeToClientMessageHandlerParams params) {
		long nodeId = params.nodeId;

		try {
			Node node = dbManager.getNodeByUid(nodeId);
			if(node == null) { // The node is not registered.
				return null;
			} else {
				User user = dbManager.getUserByUid(node.userId);
				if(user == null) {
					return getUserNotFoundMessage(majorVersion, minorVersion, sessionValid, message);
				} else {
					String sessionKey = securityManager.getSessionKeyForUsername(user.username);
					if(sessionKey == null) {
						return getUserOfflineMessage(majorVersion, minorVersion, sessionValid, message);
					} else {
						return getOkMessage(majorVersion, minorVersion, sessionValid, message, sessionKey);
					}
				}
			}
		} catch(Exception e) {			
			return getErrorMessage(majorVersion, minorVersion, sessionValid, message);
		}
	}

	protected abstract Message<ServerProtocolParameters> getUserOfflineMessage(int majorVersion, int minorVersion, boolean sessionValid, M message);

	protected abstract Message<ServerProtocolParameters> getErrorMessage(int majorVersion, int minorVersion, boolean sessionValid, M message);

	protected abstract Message<ServerProtocolParameters> getOkMessage(int majorVersion, int minorVersion, boolean sessionValid, M message, String sessionKey);

	protected abstract Message<ServerProtocolParameters> getUserNotFoundMessage(int majorVersion, int minorVersion, boolean sessionValid, M message);

	class TestAccess {
	
		public Message<ServerProtocolParameters> getUserOfflineMessage(int majorVersion, int minorVersion, boolean sessionValid, M message) {
			return NodeToClientMessageHandler.this.getUserOfflineMessage(majorVersion, minorVersion, sessionValid, message);
		}

		public Message<ServerProtocolParameters> getErrorMessage(int majorVersion, int minorVersion, boolean sessionValid, M message) {
			return NodeToClientMessageHandler.this.getErrorMessage(majorVersion, minorVersion, sessionValid, message);
		}

		public Message<ServerProtocolParameters> getOkMessage(int majorVersion, int minorVersion, boolean sessionValid, M message, String sessionKey) {
			return NodeToClientMessageHandler.this.getOkMessage(majorVersion, minorVersion, sessionValid, message, sessionKey);
		}

		public Message<ServerProtocolParameters> getUserNotFoundMessage(int majorVersion, int minorVersion, boolean sessionValid, M message) {
			return NodeToClientMessageHandler.this.getUserNotFoundMessage(majorVersion, minorVersion, sessionValid, message);
		}
		
	}
	
}
