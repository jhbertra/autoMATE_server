package com.automate.server.security;

import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.database.models.User;

public class SecurityManagerImpl implements ISecurityManager {

	
	private ISessionManager sessionManager;
	private IDatabaseManager dbManager;
	
	public SecurityManagerImpl(ISessionManager sessionManager, IDatabaseManager dbManager) {
		this.sessionManager = sessionManager;
		this.dbManager = dbManager;
	}

	@Override
	public void initialize() throws Exception {

	}

	@Override
	public void start() throws Exception {

	}

	@Override
	public void terminate() throws Exception {

	}

	@Override
	public String authenticateClient(String username, String password, String sessionKey, String ipAddress) {
		User user = dbManager.getUser(username);
		if(user == null) {
			return null;
		}
		if((sessionKey == null || sessionKey.isEmpty()) && user.password.equals(password)) {			
			return sessionManager.createNewSession(username, ipAddress);
		}
		return null;
	}

	@Override
	public String getUsername(String sessionKey) {
		return sessionManager.getUsernameForSessionKey(sessionKey);
	}

	@Override
	public String getIpAddress(String sessionKey) {
		return sessionManager.getIpAddressForSessionKey(sessionKey);
	}

	@Override
	public String getSessionKeyForNode(long nodeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSessionKeyForUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validateParameters(ClientProtocolParameters parameters) {
		return sessionManager.sessionValid(parameters.sessionKey);
	}

}
