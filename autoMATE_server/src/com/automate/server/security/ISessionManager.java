package com.automate.server.security;

public interface ISessionManager {

	/**
	 * Creates a new session for connectivity to an autoMATE application.
	 * @param username the username to sign in.
	 * @param clientIpAddress the ip address to create a session for.
	 * @return the new session key created, or null if the session could not be created.
	 * @throws NullPointerException if username is null or empty
	 * @throws NullPointerException if clientIpAddress is null or empty
	 */
	String createNewAppSession(String username, String clientIpAddress);

	/**
	 * Creates a new session for connectivity to an autoMATE node.
	 * @param nodeId the id of the node 
	 * @param nodeIpAddress the ip address of the node 
	 * @return the new session key created, or null if the session could not be created.
	 * @throws IllegalArgumentException if nodeId is < 0
	 */
	String createNewNodeSession(long nodeId, String nodeIpAddress);
	String getIpAddressForSessionKey(String sessionKey);
	long getNodeIdForSessionKey(String sessionKey);
	String getSessionKeyForNodeId(long nodeId);
	String getSessionKeyForUsername(String username);
	String getUsernameForSessionKey(String sessionKey);
	boolean sessionValid(String sessionKey);

}
