package com.automate.server.security;

import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.server.IManager;

/**
 * Manager that handles security. 
 * @author jamie.bertram
 *
 */
public interface ISecurityManager extends IManager {

	/**
	 * Authenticates a user's credentials and generates a new session key.
	 * @param username - the user's username
	 * @param password - the user's password
	 * @param sessionKey - the session key of the message that arrived with the user's credentails
	 * @return the new session key, null if authentication failed.
	 */
	public String authenticateClient(String username, String password, String sessionKey, String ipAddress);

	/**
	 * Returns the username of the account associated with a given session key.
	 * @param sessionKey the session key
	 * @return the username that owns the session key, or null if no account owns the session key.
	 */
	public String getUsername(String sessionKey);

	/**
	 * Returns the ip address for the given session.
	 * @param sessionKey the session key
	 */
	public String getIpAddress(String sessionKey);
	
	public String getSessionKeyForNode(long nodeId);

	public String getSessionKeyForUsername(String username);

	/**
	 * Validates client parameters.
	 * 
	 * @param parameters the parameters that were received.
	 * @return session-valid
	 */
	public boolean validateParameters(ClientProtocolParameters parameters);
	
}
