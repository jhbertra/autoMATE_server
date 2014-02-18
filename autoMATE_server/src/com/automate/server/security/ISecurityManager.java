package com.automate.server.security;


import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.server.IManager;

/**
 * Manager that handles security. 
 * @author jamie.bertram
 *
 */
public interface ISecurityManager extends IManager {

	/**
	 * Authenticates a user's credentials and generates a new session key.
	 * 
	 * Authentication will fail under the following conditions:
	 * 
	 * 	-	the username is invalid (not in database)
	 * 	-	the password is incorrect
	 * 	-	the sessionKey is non-null and non-empty (the sending client already has a session)
	 * 
	 * @param username - the user's username
	 * @param password - the user's password
	 * @param sessionKey - the session key of the message that arrived with the user's credentails
	 * @return the new session key, null if authentication failed.
	 * @throws NullPointerException if username is null
	 * @throws NullPointerException if password is null
	 * @throws NullPointerException if ipAddress is null
	 */
	public String authenticateClient(String username, String password, String sessionKey, String ipAddress);

	/**
	 * Returns the username of the account associated with a given session key.
	 * @param sessionKey the session key
	 * @return the username that owns the session key, or null if no account owns the session key.
	 * @throws NullPointerException if sessionKey is null
	 */
	public String getUsername(String sessionKey);

	/**
	 * Returns the nodeId of the node associated with the given session key.
	 * @param sessionKey the session key
	 * @return the nodeId of the node that owns the session key, or -1 if no node owns the session key;
	 * @throws NullPointerException if sessionKey is null
	 */
	public long getNodeId(String sessionKey);

	/**
	 * Returns the ip address for the given session.
	 * @param sessionKey the session key
	 */
	public String getIpAddress(String sessionKey);
	
	/**
	 * Returns the session key owned by the node with uid <code>sessionKey</code>
	 * @param nodeId the id of the node whose session key to return
	 * @return the session key of the node id'd by sessionKey.  Null if the node is not connected (does not have a session)
	 * @throw {@link IllegalArgumentException} if nodeId < 0
	 */
	public String getSessionKeyForNode(long nodeId);

	/**
	 * Returns the session key owned by the user account with the given username.
	 * @param username the username of the account
	 * @return the session key the active session registered under the account with username <code>username</code>.  Null if the 
	 * account is not connected.
	 */
	public String getSessionKeyForUsername(String username);
	
	/**
	 * Validates client parameters.
	 * 
	 * @param parameters the parameters that were received.
	 * @return session-valid
	 */
	public boolean validateParameters(ClientProtocolParameters parameters);

	
}
