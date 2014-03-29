package com.automate.server.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automate.protocol.Message;
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
		final Socket socket;
		final ClientType clientType;
		int references = 0;

		public SessionData(String username, Socket socket, ClientType clientType) {
			this.username = username;
			this.socket = socket;
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
		System.out.println("Lost connection for session " + sessionKey);
		synchronized (lock) {
			pingedClients.remove(sessionKey);
			SessionData data = activeSessions.remove(sessionKey);
			if(data != null) {
				connectedClients.remove(data.username);
				try {
					data.socket.close();
				} catch (IOException e) {}
			}
		}
		System.out.println("Removed session");
	}

	@Override
	public String createNewAppSession(String username, Socket clientSocket) {
		return createNewSession(username, clientSocket, ClientType.APP);
	}

	@Override
	public String createNewNodeSession(long nodeId, Socket nodeSocket) {
		if(nodeId < 0) {
			throw new IllegalArgumentException("nodeId invalid " + nodeId);
		}
		return createNewSession("$" + String.valueOf(nodeId), nodeSocket, ClientType.NODE);
	}

	private String createNewSession(String clientId, Socket clientSocket, ClientType type) {
		if(clientId == null) {
			throw new NullPointerException("username was null.");
		}
		if(clientSocket == null) {
			throw new NullPointerException("clientSocket was null.");
		}
		if(clientSocket.isClosed()) {
			throw new IllegalArgumentException("Closed socket passed to create new session.");
		}
		synchronized (lock) {			
			if(connectedClients.containsKey(clientId)) {
				System.out.println("User already connected");
				return null;
			}
		}
		String key = DigestUtils.md5Hex(clientId + ":" + clientSocket.getInetAddress().getHostAddress() + ":" + SALT + ":" + type);		
		synchronized (lock) {
			if(activeSessions.containsKey(key)) return null;
			activeSessions.put(key, new SessionData(clientId, clientSocket, type));
			connectedClients.put(clientId, key);
		}
		logger.info("Created new session with session key: " + key);
		return key;
	}

	@Override
	public Socket borrowSocketForSessionKey(String sessionKey) {
		if(sessionKey == null) {
			throw new NullPointerException("sessionKey was null.");
		}
		while(true) {
			SessionData data;
			synchronized (lock) {				
				logger.info("Getting address for session kye: " + sessionKey);
				data = activeSessions.get(sessionKey);
			}
			if(data != null) {
				synchronized (data) {
					if(data.references == -1) {
						try {
							data.wait();
						} catch (InterruptedException e) {}
						continue;
					} else {
						++data.references;
						return data.socket;
					}
				}
			} else {				
				return null;
			}
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

						@Override
						public void messageDeliveryFailed(Message<ServerProtocolParameters> message) {
							connectionLost(message.getParameters().sessionKey);
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

	@Override
	public void updateClientSocket(String sessionKey, Socket socket) {
		SessionData oldData;
		synchronized (lock) {
			oldData = activeSessions.remove(sessionKey);
		}
		if(oldData != null) {
			synchronized (oldData) {
				while(oldData.references > 0) {
					try {
						oldData.wait();
					} catch (InterruptedException e) {}
				}
				oldData.references = -1; // flag that the data is not to be used anymore
				try {
					PrintWriter writer = new PrintWriter(oldData.socket.getOutputStream());
					writer.println("EOF");
					writer.close();
					System.out.println("Socket EOF");
				} catch (IOException e) {}
				activeSessions.put(sessionKey, new SessionData(oldData.username, socket, oldData.clientType));
				oldData.notify();
			}
		}
	}

	@Override
	public void returnSocket(String sessionKey) {
		SessionData data = activeSessions.get(sessionKey);
		if(data != null) {
			synchronized (data) {
				if(data.references == 0) {
					throw new IllegalStateException("Thread " + Thread.currentThread().getName() + " Attempted to return a socket"
							+ " that was not borrowed.");
				}
				--data.references;
				data.notify();
			}
		}
	}

}