package com.automate.server.security;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerPingMessage;
import com.automate.server.connectivity.EngineCallback;
import com.automate.server.messaging.IMessageManager;
import com.automate.server.messaging.IMessageManager.MessageSentListener;

public class SessionManager implements ISessionManager, EngineCallback {
	
	enum ClientType {
		APP,
		NODE
	}
	private class SessionData {
		final String username;
		final String ipAddress;
		final ClientType clientType;
		
		public SessionData(String username, String ipAddress, ClientType clientType) {
			this.username = username;
			this.ipAddress = ipAddress;
			this.clientType = clientType;
		}
	}
	
	private HashMap<String, SessionData> activeSessions = new HashMap<String, SessionManager.SessionData>();
	
	private HashMap<String, SessionData> pingedClients = new HashMap<String, SessionManager.SessionData>();
	private HashMap<String, String> connectedClients = new HashMap<String, String>();
	private final Object lock = new Object();
	
	private int majorVersion;

	private int minorVersion;
	
	private static final String SALT = "jf849jow84u384hw83487w43w904fgsi874yfs";

	private static final Logger logger = LogManager.getLogger();
	
	public SessionManager(int majorVersion, int minorVersion) {
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
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

	@Override
	public void connectionLost(String sessionKey) {
		synchronized (lock) {
			logger.trace("Lost connection for session {}.", sessionKey);
			pingedClients.remove(sessionKey);
			activeSessions.remove(sessionKey);
		}
	}

	@Override
	public String createNewAppSession(String username, String clientIpAddress) {
		return createNewSession(username, clientIpAddress, ClientType.APP);
	}

	@Override
	public String createNewNodeSession(long nodeId, String nodeIpAddress) {
		if(nodeId < 0) {
			throw new IllegalArgumentException("nodeId invalid " + nodeId);
		}
		return createNewSession("$" + String.valueOf(nodeId), nodeIpAddress, ClientType.NODE);
	}

	private String createNewSession(String clientId, String clientIpAddress, ClientType type) {
		if(clientId == null) {
			throw new NullPointerException("username was null.");
		}
		if(clientIpAddress == null) {
			throw new NullPointerException("clientIpAddress was null.");
		}
		synchronized (lock) {			
			if(connectedClients.containsKey(clientId)) return null;
		}
    	String key = DigestUtils.md5Hex(clientId + ":" + clientIpAddress + ":" + SALT + ":" + type);		
    	synchronized (lock) {
    		if(activeSessions.containsKey(key)) return null;
    		activeSessions.put(key, new SessionData(clientId, clientIpAddress, type));
    		connectedClients.put(clientId, key);
    	}
		return key;
	}

	@Override
	public String getIpAddressForSessionKey(String sessionKey) {
		if(sessionKey == null) {
			throw new NullPointerException("sessionKey was null.");
		}
		SessionData data;
		synchronized (lock) {				
			data = activeSessions.get(sessionKey);
		}
		if(data != null) {
			return data.ipAddress;
		} else {				
			return null;
		}
	}
	
	@Override
	public long getNodeIdForSessionKey(String sessionKey) {
		if(sessionKey == null) {
			throw new NullPointerException("sessionKey was null.");
		}
		SessionData data;
		synchronized (lock) {			
			data = activeSessions.get(sessionKey);
		}
		if(data == null) {
			return -1;
		} else if (data.clientType == ClientType.NODE) {
			return Long.parseLong(data.username.substring(1));
		} else {
			return -1;
		}
	}

	@Override
	public String getSessionKeyForNodeId(long nodeId) {
		if(nodeId < 0) {
			throw new IllegalArgumentException("nodeId < 0");
		}
		synchronized (lock) {			
			return connectedClients.get("$" + String.valueOf(nodeId));
		}
	}

	@Override
	public String getSessionKeyForUsername(String username) {
		if(username == null) {
			throw new NullPointerException("username was null");
		}
		synchronized (lock) {			
			return connectedClients.get(username);
		}
	}

	@Override
	public String getUsernameForSessionKey(String sessionKey) {
		if(sessionKey == null) {
			throw new NullPointerException("sessionKey was null.");
		}
		SessionData data;
		synchronized (lock) {				
			data = activeSessions.get(sessionKey);
		}
		if(data == null) {
			return null;
		} else if(data.clientType == ClientType.APP) {
			return data.username;
		} else {
			return null;
		}
	}

	@Override
	public int pingAllClients(final ClientPingListener listener, IMessageManager messageManager) {
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
			return activeSessions.size() - pingedClients.size();
		}
	}

	@Override
	public boolean sessionValid(String sessionKey) {
		if(sessionKey == null) {
			 throw new NullPointerException("sessionKey was null.");
		}
		synchronized (lock) {			
			return activeSessions.containsKey(sessionKey);
		}
	}
	
}