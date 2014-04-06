package com.automate.server.security;

import java.net.Socket; 

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.server.InitializationException;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.database.models.Model;
import com.automate.server.database.models.Node;
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

	private static final Logger logger = LogManager.getLogger();
	
	private static final String SALT = "E47doFVRgNw1xX9a";
	
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

	/* (non-Javadoc)
	 * @see com.automate.server.security.ISecurityManager#authenticateNode(java.lang.String, java.lang.String, java.lang.String, java.net.Socket)
	 */
	@Override
	public String authenticateNode(String username, String password, String sessionKey, Socket socket) {
		if(username == null) {
			throw new NullPointerException("username was null.");
		} else if(password == null) {
			throw new NullPointerException("password was null.");
		} else if(socket == null) {
			throw new NullPointerException("socket was null.");
		}
		try {
			long nodeId = Long.parseLong(username.substring(1));
			Node node = dbManager.getNodeByUid(nodeId);
			if(node == null) {
				System.out.println("node not in database.");
				return null;
			}
			String validPassword = generateNodePassword(nodeId);
			if((sessionKey == null || sessionKey.isEmpty() || sessionKey.equalsIgnoreCase("null")) && password.equals(validPassword)) {
				return sessionManager.createNewNodeSession(nodeId, socket);
			}
		} catch(Exception e) {
			logger.error("Error authenticating node" + username + ".", e);
		}
		return null;
	}

	@Override
	public String authenticateClient(String username, String password, String sessionKey, Socket socket) {
		if(username == null) {
			throw new NullPointerException("username was null.");
		} else if(password == null) {
			throw new NullPointerException("password was null.");
		} else if(socket == null) {
			throw new NullPointerException("socket was null.");
		}
		try {
			User user = dbManager.getUserByUsername(username);
			if(user == null) {
				return null;
			}
			if((sessionKey == null || sessionKey.isEmpty() || sessionKey.equalsIgnoreCase("null")) && user.password.equals(password)) {
				return sessionManager.createNewAppSession(username, socket);
			}
		} catch(Exception e) {
			logger.error("Error authenticating client" + username + ".", e);
		}
		return null;
	}

	@Override
	public int registerNewUser(String username, String password, String name, String email) {
		User user = dbManager.getUserByUsername(username);
		if(user != null) return 401;
		user = dbManager.addUser(username, password, name, email);
		if(user != null) return 200;
		else return 500;
	}

	@Override
	public long registerNodeForUser(String maxVersion, long modelId, String name, String username) {
		Model model = dbManager.getModelByUid(modelId);
		if(model == null) return -1;
		User user = dbManager.getUserByUsername(username);
		Node node = dbManager.addNode(maxVersion, modelId, name, user.uid);
		if(node == null) return -1;
		return node.uid;
	}
	
	/* (non-Javadoc)
	 * @see com.automate.server.security.ISecurityManager#generateNodePassword(long)
	 */
	@Override
	public String generateNodePassword(long nodeId) {
		return DigestUtils.md5Hex(nodeId + ":" + SALT);
	}

	@Override
	public String getUsername(String sessionKey) {
		if(sessionKey == null) {
			throw new NullPointerException("sessionKey was null in getUsername");
		}
		return sessionManager.getUsernameForSessionKey(sessionKey);
	}

	@Override
	public Socket getSocket(String sessionKey) {
		if(sessionKey == null) {
			throw new NullPointerException("sessionKey was null in getIpAddress");
		}
		return sessionManager.borrowSocketForSessionKey(sessionKey);
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

	@Override
	public void returnSocket(String sessionKey) {
		sessionManager.returnSocket(sessionKey);
	}

	@Override
	public void updateSocket(String sessionKey, Socket socket) {
		sessionManager.updateClientSocket(sessionKey, socket);
	}
	
}
