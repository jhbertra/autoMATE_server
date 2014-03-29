package com.automate.server.security;

import java.net.Socket;

public interface ISessionManager {

	/**
	 * Creates a new session for connectivity to an autoMATE application.
	 * @param username the username to sign in.
	 * @param clientSocket the socket connected to the client.
	 * @return the new session key created, or null if the session could not be created.
	 * @throws NullPointerException if username is null or empty
	 * @throws IllegalArgumentException if clientSocket is closed
	 */
	String createNewAppSession(String username, Socket clientSocket);

	/**
	 * Creates a new session for connectivity to an autoMATE node.
	 * @param nodeId the id of the node 
	 * @param nodeSocket the socket for the node.
	 * @return the new session key created, or null if the session could not be created.
	 * @throws IllegalArgumentException if nodeId is < 0
	 * @throws IllegalArgumentException if nodeSocket is closed.
	 */
	String createNewNodeSession(long nodeId, Socket nodeSocket);
	Socket borrowSocketForSessionKey(String sessionKey);
	long getNodeIdForSessionKey(String sessionKey);
	String getSessionKeyForNodeId(long nodeId);
	String getSessionKeyForUsername(String username);
	String getUsernameForSessionKey(String sessionKey);
	boolean sessionValid(String sessionKey);
	void returnSocket(String sessionKey);
	
	/**
	 * Update the socket that connects to the client with the given session key.
	 * @param the session key of the client.
	 * @param socket the socket that connects to the client.
	 * @throws IllegalArgumentException if no client is registered for the sessionKey
	 */
	public void updateClientSocket(String sessionKey, Socket socket);
}
