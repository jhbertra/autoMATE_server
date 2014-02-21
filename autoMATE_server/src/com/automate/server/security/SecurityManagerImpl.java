package com.automate.server.security;

import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.server.InitializationException;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.database.models.User;

/**
 * The main implementation of the {@link ISecurityManager} interface.
 * @author jamie.bertram
 *
 */
public class SecurityManagerImpl implements ISecurityManager {

	private ISessionManager sessionManager;
	private IDatabaseManager dbManager;
	
	private int majorVersion;
	private int minorVersion;

	/**
	 * Creates a new SecurityManagerImpl
	 * @param sessionManager the manager for session data
	 * @param dbManager the manager for database queries and connections
	 * @param majorVersion the major part of the supported protocol version
	 * @param minorVersion the minor part of the supported protocol version
	 * @throws NullPointerException if dbManager is null
	 * @throws NullPointerException if sessionManager is null
	 */
	public SecurityManagerImpl(ISessionManager sessionManager,
			IDatabaseManager dbManager, int majorVersion, int minorVersion) {
		if(dbManager == null) {
			throw new NullPointerException("SecurityManagerImpl reuires non-null IDatabaseManager.");
		} else if(sessionManager == null) {
			throw new NullPointerException("SecurityManagerImpl reuires non-null ISessionManager.");
		}
		this.sessionManager = sessionManager;
		this.dbManager = dbManager;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}

	@Override
	public void initialize()  throws InitializationException {

	}

	@Override
	public void start() throws Exception {

	}

	@Override
	public void terminate() throws Exception {

	}

	@Override
	public String authenticateClient(String username, String password, String sessionKey, String ipAddress) {
		if(username == null) {
			throw new NullPointerException("username was null.");
		} else if(password == null) {
			throw new NullPointerException("password was null.");
		} else if(ipAddress == null) {
			throw new NullPointerException("ipAddress was null.");
		}
		try {
			User user = dbManager.getUserByUsername(username);
			if(user == null) {
				return null;
			}
			if((sessionKey == null || sessionKey.isEmpty()) && user.password.equals(password)) {			
				return sessionManager.createNewAppSession(username, ipAddress);
			}
		} catch(Exception e) {
			// allow method to return null TODO log error.
		}
		return null;
	}

	@Override
	public String getUsername(String sessionKey) {
		if(sessionKey == null) {
			throw new NullPointerException("sessionKey was null in getUsername");
		}
		return sessionManager.getUsernameForSessionKey(sessionKey);
	}

	@Override
	public String getIpAddress(String sessionKey) {
		if(sessionKey == null) {
			throw new NullPointerException("sessionKey was null in getIpAddress");
		}
		return sessionManager.getIpAddressForSessionKey(sessionKey);
	}

	@Override
	public String getSessionKeyForNode(long nodeId) {
		if(nodeId < 0) {
			throw new IllegalArgumentException("Invalid nodeId: " + nodeId);
		}
		return sessionManager.getSessionKeyForNodeId(nodeId);
	}

	@Override
	public String getSessionKeyForUsername(String username) {
		if(username == null) {
			throw new NullPointerException("username was null in getSessionKeyForUsername.");
		}
		return sessionManager.getSessionKeyForUsername(username);
	}

	@Override
	public boolean validateParameters(ClientProtocolParameters parameters) {
		if(parameters == null) {
			throw new NullPointerException("parameters was null.");
		} else if(	parameters.majorVersion < 0 
				|| 	parameters.majorVersion > this.majorVersion 
				|| 	parameters.minorVersion < 0
				||	(parameters.majorVersion == this.majorVersion && parameters.minorVersion > this.minorVersion)) { 
			throw new IllegalArgumentException("Invalid protocol version specified by ClientProtocolParameters: " + parameters.majorVersion + 
					"." + parameters.minorVersion);
		}
		return sessionManager.sessionValid(parameters.sessionKey);
	}

	@Override
	public long getNodeId(String sessionKey) {
		if(sessionKey == null) {
			throw new NullPointerException("sessionKey was null in getNodeId");
		}
		return sessionManager.getNodeIdForSessionKey(sessionKey);
	}

}
