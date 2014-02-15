package com.automate.server.security;

public interface ISessionManager {

	String createNewSession(String username, String clientIpAddress);
	String getUsernameForSessionKey(String sessionKey);
	String getIpAddressForSessionKey(String sessionKey);
	boolean sessionValid(String sessionKey);

}
