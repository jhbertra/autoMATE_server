package com.automate.server.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Set;

import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerPingMessage;
import com.automate.server.connectivity.EngineCallback;
import com.automate.server.messaging.IMessageManager;
import com.automate.server.messaging.IMessageManager.MessageSentListener;

public class SessionManager implements ISessionManager, EngineCallback {
	
	private HashMap<String, SessionData> activeSessions;
	private HashMap<String, SessionData> pingedClients;
	
	private final Object lock = new Object();
	private int majorVersion;
	private int minorVersion;
	
	private static final String SALT = "jf849jow84u384hw83487w43w904fgsi874yfs";

	public SessionManager(int majorVersion, int minorVersion) {
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}

	@Override
	public String createNewSession(String username, String clientIpAddress) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update((username + ":" + clientIpAddress + ":" + SALT).getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		byte [] hashData = md.digest();
		
		//convert the byte to hex format
        StringBuilder hexString = new StringBuilder();
    	for (int i = 0; i < hashData.length; ++i) {
    		String hex=Integer.toHexString(0xff & hashData[i]);
   	     	if(hex.length()==1) hexString.append('0');
   	     	hexString.append(hex);
    	}
    	
    	String key = hexString.toString();
		
    	synchronized (lock) {
    		if(activeSessions.containsKey(key)) return null;
    		activeSessions.put(key, new SessionData(username, clientIpAddress));
    	}
		return key;
	}

	@Override
	public String getUsernameForSessionKey(String sessionKey) {
		if(sessionKey != null) {
			SessionData data;
			synchronized (lock) {				
				data = activeSessions.get(sessionKey);
			}
			if(data != null) {
				return data.username;
			}
		}
		return null;
	}

	@Override
	public String getIpAddressForSessionKey(String sessionKey) {
		if(sessionKey != null) {
			SessionData data;
			synchronized (lock) {				
				data = activeSessions.get(sessionKey);
			}
			if(data != null) {
				return data.ipAddress;
			}
		}
		return null;
	}

	@Override
	public void connectionLost(String sessionKey) {
		synchronized (lock) {			
			pingedClients.remove(sessionKey);
			activeSessions.remove(sessionKey);
		}
	}

	@Override
	public void pingAllClients(final ClientPingListener listener, IMessageManager messageManager) {
		synchronized (lock) {			
			Set<String> activeSessions = this.activeSessions.keySet();
			Set<String> pingedClients = this.pingedClients.keySet();
			for(final String data : activeSessions) {
				if(!pingedClients.contains(data)) {
					messageManager.sendMessage(new ServerPingMessage(
							new ServerProtocolParameters(majorVersion, minorVersion, true, data)), new MessageSentListener() {
								@Override
								public void messageSent() {
									listener.clientPinged(data);
								}
							});
				}
			}
		}
	}

	private class SessionData {
		final String username;
		final String ipAddress;
		
		public SessionData(String username, String ipAddress) {
			this.username = username;
			this.ipAddress = ipAddress;
		}
	}

	@Override
	public boolean sessionValid(String sessionKey) {
		synchronized (lock) {			
			return activeSessions.containsKey(sessionKey);
		}
	}

	@Override
	public boolean clientPingReceived(String client) {
		synchronized (lock) {
			if(pingedClients.containsKey(client)) {
				pingedClients.remove(client);
				return true;
			} else {
				return false;
			}
		}
	}
	
}
