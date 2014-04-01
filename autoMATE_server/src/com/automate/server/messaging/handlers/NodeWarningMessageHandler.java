package com.automate.server.messaging.handlers;

import java.util.HashMap;

import com.automate.protocol.Message;
import com.automate.protocol.node.messages.NodeWarningMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerClientWarningMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.security.ISecurityManager;

public class NodeWarningMessageHandler extends NodeToClientMessageHandler<NodeWarningMessage>{

	private final HashMap<Long, String> pendingWarnings;
	private long nextWarningId = 0;
	
	public NodeWarningMessageHandler(HashMap<Long, String> pendingWarnings, IDatabaseManager dbManager, ISecurityManager securityManager) {
		super(dbManager, securityManager);
		this.pendingWarnings = pendingWarnings;
	}

	@Override
	protected Message<ServerProtocolParameters> getUserOfflineMessage(int majorVersion, int minorVersion, boolean sessionValid,
			NodeWarningMessage message) {
		return null;
	}

	@Override
	protected Message<ServerProtocolParameters> getErrorMessage(int majorVersion, int minorVersion, boolean sessionValid,
			NodeWarningMessage message) {
		return null;
	}

	@Override
	protected Message<ServerProtocolParameters> getOkMessage(int majorVersion,int minorVersion, boolean sessionValid, 
			NodeWarningMessage message, String sessionKey, long nodeId) {
		long warningId;
		synchronized (pendingWarnings) {
			warningId = nextWarningId++;
			pendingWarnings.put(warningId, message.message);
		}
		return new ServerClientWarningMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, sessionKey), 
				warningId, nodeId, message.message);
	}

	@Override
	protected Message<ServerProtocolParameters> getUserNotFoundMessage(int majorVersion, int minorVersion, boolean sessionValid,
			NodeWarningMessage message) {
		return null;
	}

}
