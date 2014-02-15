package com.automate.server.messaging.handlers;

import java.util.HashMap;

import com.automate.protocol.Message;
import com.automate.protocol.node.messages.NodeWarningMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerClientWarningMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.database.models.Node;
import com.automate.server.database.models.User;
import com.automate.server.security.ISecurityManager;

public class NodeWarningMessageHandler	implements IMessageHandler<NodeWarningMessage, Void> {

	private final HashMap<Long, String> pendingWarnings;
	private long nextWarningId = 0;
	private IDatabaseManager dbManager;
	private ISecurityManager securityManager;
	
	public NodeWarningMessageHandler(HashMap<Long, String> pendingWarnings, IDatabaseManager dbManager, ISecurityManager securityManager) {
		super();
		this.pendingWarnings = pendingWarnings;
		this.dbManager = dbManager;
		this.securityManager = securityManager;
	}

	@Override
	public Message<ServerProtocolParameters> handleMessage(int minorVersion,
			int majorVersion, boolean sessionValid, NodeWarningMessage message,
			Void params) {
		long warningId;
		synchronized (pendingWarnings) {
			warningId = nextWarningId++;
			pendingWarnings.put(warningId, message.message);
		}
		Node node = dbManager.getNodeByUid(message.nodeId);
		User user = dbManager.getUserByUid(node.userId);
		String userSessionKey = securityManager.getSessionKeyForUsername(user.username);
		return new ServerClientWarningMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, userSessionKey), 
				warningId, message.nodeId, message.message);
	}

}
