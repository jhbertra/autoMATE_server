package com.automate.server.messaging.handlers;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.database.models.Node;
import com.automate.server.database.models.User;
import com.automate.server.security.ISecurityManager;

public abstract class ClientToNodeMessageHandler <M extends Message<ClientProtocolParameters>> implements IMessageHandler<M, ClientToNodeMessageHandlerParams> {

	protected IDatabaseManager dbManager;
	protected ISecurityManager securityManager;

	TestAccess testAccess = new TestAccess();
	
	/**
	 * Creates a new {@link ClientToNodeMessageHandler} that uses the given dbManager and securityManager.
	 * @param dbManager the dbManager to query.
	 * @param securityManager the securityManager that manages session information.
	 */
	public ClientToNodeMessageHandler(IDatabaseManager dbManager, ISecurityManager securityManager) {
		if(dbManager == null) {
			throw new NullPointerException("dbManager was null.");
		} else if(securityManager == null) {
			throw new NullPointerException("securityManager was null.");
		}
		this.dbManager = dbManager;
		this.securityManager = securityManager;
	}
	
	/**
	 * Returns the message that is sent when the node does not exist.
	 */
	protected abstract Message<ServerProtocolParameters> getNonExistentNodeMessage(int majorVersion, int minorVersion, 
			boolean sessionValid,M message);

	/**
	 * Returns the message that is sent when the node does not belong to the user.
	 */
	protected abstract Message<ServerProtocolParameters> getNotOwnedNodeMessage(int majorVersion, int minorVersion, 
			boolean sessionValid,M message);

	/**
	 * Returns the message that is sent when the node is offline.
	 */
	protected abstract Message<ServerProtocolParameters> getNodeOfflineMessage(int majorVersion, int minorVersion, 
			boolean sessionValid,M message);

	/**
	 * Returns the message that is sent when the node is owned by the user and is online.
	 */
	protected abstract Message<ServerProtocolParameters> getOkMessage(int majorVersion, int minorVersion, 
			boolean sessionValid, M message, String nodeSessionKey);

	/**
	 * Returns the message that is sent when an internal error occurs.
	 */
	protected abstract Message<ServerProtocolParameters> getErrorMessage(int majorVersion, int minorVersion, 
			boolean sessionValid,M message);
	

	/**
	 * Returns the following messages under the corresponding circumstances:<br />
	 * <table border="1">
	 * <tr>
	 *	<th>Condition</th>
	 *	<th>Returned message</th>
	 * </tr>
	 * <tr>
	 *	<td>The node specified doesn't exist</td>
	 *	<td>subclass implementation of getNonExistentNodeMessage</td>
	 * </tr>
	 * <tr>
	 *	<td>The node doesn't belong to the user</td>
	 *	<td>subclass implementation of getNotOwnedNodeMessage</td>
	 * </tr>
	 * <tr>
	 *	<td>The node is owned by the user, but is offline</td>
	 *	<td>subclass implementation of getNodeOfflineMessage</td>
	 * </tr>
	 * <tr>
	 *	<td>The node is owned by the user, and is online</td>
	 *	<td>subclass implementation of getOkMessage</td>
	 * </tr>
	 * <tr>
	 *	<td>An error occurs while processing the message</td>
	 *	<td>subclass implementation of getErrorMessage</td>
	 * </tr>
	 * </table>
	 * 
	 * @return a response message, as specified above.
	 * @throws NullPointerException if message is null.
	 */
	@Override
	public Message<ServerProtocolParameters> handleMessage(int majorVersion, int minorVersion, boolean sessionValid,
			M message, ClientToNodeMessageHandlerParams params) {
		if(message == null) {
			throw new NullPointerException("message was null.");
		}
		if(params == null) {
			throw new NullPointerException("params was null.");
		}
		long nodeId = params.nodeId;

		try {
			String sessionKey = securityManager.getSessionKeyForNode(nodeId);
			Node node = dbManager.getNodeByUid(nodeId);

			if(node == null) {  			// the node does not exist
				return getNonExistentNodeMessage(majorVersion, minorVersion, sessionValid, message);
				
			} else { 						// the node exists...
				String username = securityManager.getUsername(message.getParameters().sessionKey);
				User user = dbManager.getUserByUsername(username);

				if(node.userId != user.uid) { 		// ...but does not belong to the user.
					return getNotOwnedNodeMessage(majorVersion, minorVersion, sessionValid, message);

				} else if(sessionKey == null || sessionKey.isEmpty()) { 	// ...belongs to the user, but is not online.
					return getNodeOfflineMessage(majorVersion, minorVersion, sessionValid, message);

				} else { 							// ..and is online, available for receiving commands.
					return getOkMessage(majorVersion, minorVersion, sessionValid, message, sessionKey);
				}
			}
		} catch (Exception e) {
			return getErrorMessage(majorVersion, minorVersion, sessionValid, message);
		}
	}
	
	class TestAccess {
		
		public Message<ServerProtocolParameters> getNonExistentNodeMessage(int majorVersion, int minorVersion, 
				boolean sessionValid,M message) {
			return ClientToNodeMessageHandler.this.getNonExistentNodeMessage(majorVersion, minorVersion, sessionValid, message);
		}
		
		public Message<ServerProtocolParameters> getNotOwnedNodeMessage(int majorVersion, int minorVersion, 
				boolean sessionValid,M message) {
			return ClientToNodeMessageHandler.this.getNotOwnedNodeMessage(majorVersion, minorVersion, sessionValid, message);
		}
		
		public Message<ServerProtocolParameters> getNodeOfflineMessage(int majorVersion, int minorVersion, 
				boolean sessionValid,M message) {
			return ClientToNodeMessageHandler.this.getNodeOfflineMessage(majorVersion, minorVersion, sessionValid, message);
		}
		
		public Message<ServerProtocolParameters> getOkMessage(int majorVersion, int minorVersion, 
				boolean sessionValid, M message, String nodeSessionKey){
			return ClientToNodeMessageHandler.this.getOkMessage(majorVersion, minorVersion, sessionValid, message, nodeSessionKey);
		}
		
		public Message<ServerProtocolParameters> getErrorMessage(int majorVersion, int minorVersion, 
				boolean sessionValid,M message) {
			return ClientToNodeMessageHandler.this.getErrorMessage(majorVersion, minorVersion, sessionValid, message);
		}
	}
	
}