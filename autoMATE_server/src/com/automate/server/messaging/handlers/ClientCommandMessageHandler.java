package com.automate.server.messaging.handlers;

import java.util.List;

import com.automate.protocol.Message;
import com.automate.protocol.client.messages.ClientCommandMessage;
import com.automate.protocol.models.CommandArgument;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerClientCommandMessage;
import com.automate.protocol.server.messages.ServerNodeCommandMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.security.ISecurityManager;

/**
 * Delegate for handling Client Command Messages.
 * @author jamie.bertram
 *
 */
public class ClientCommandMessageHandler extends ClientToNodeMessageHandler<ClientCommandMessage> {

	/**
	 * Creates a new {@link ClientCommandMessageHandler}
	 * @param dbManager the {@link IDatabaseManager} to query
	 * @param securityManager the {@link ISecurityManager} that manages session data.
	 */
	public ClientCommandMessageHandler(IDatabaseManager dbManager,
			ISecurityManager securityManager) {
		super(dbManager, securityManager);
	}

	/**
	 * @return a {@link ServerClientCommandMessage} with responseCode == 400, message == "INVALID NODE ID"
	 */
	@Override
	protected Message<ServerProtocolParameters> getNonExistentNodeMessage(int majorVersion, int minorVersion, boolean sessionValid, 
			ClientCommandMessage message) {
		return new ServerClientCommandMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, 
				message.getParameters().sessionKey), message.commandId, 400, "INVALID NODE ID");
	}

	/**
	 * @return a {@link ServerClientCommandMessage} with responseCode == 405, message == "NODE NOT OWNED BY USER"
	 */
	@Override
	protected Message<ServerProtocolParameters> getNotOwnedNodeMessage(int majorVersion,	int minorVersion, boolean sessionValid, 
			ClientCommandMessage message) {
		return new ServerClientCommandMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, 
				message.getParameters().sessionKey), message.commandId, 405, "NODE NOT OWNED BY USER");
	}

	/**
	 * @return a {@link ServerClientCommandMessage} with responseCode == 404, message == "NODE OFFLINE"
	 */
	@Override
	protected Message<ServerProtocolParameters> getNodeOfflineMessage(int majorVersion, int minorVersion, boolean sessionValid, 
			ClientCommandMessage message) {
		return new ServerClientCommandMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, 
				message.getParameters().sessionKey), message.commandId, 404, "NODE OFFLINE");
	}

	/**
	 * @return a {@link ServerNodeCommandMessage} that forwards the contents of the original message to the node.
	 */
	@Override
	protected Message<ServerProtocolParameters> getOkMessage(int majorVersion, int minorVersion, boolean sessionValid, 
			ClientCommandMessage message, String nodeSessionKey) {
		return new ServerNodeCommandMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, nodeSessionKey), 
				message.nodeId, message.name, message.commandId, message.args);
	}

	/**
	 * @return a {@link ServerClientCommandMessage} with responseCode == 500, message == "INTERNAL SERVER ERROR"
	 */
	@Override
	protected Message<ServerProtocolParameters> getErrorMessage(int majorVersion, int minorVersion, boolean sessionValid, 
			ClientCommandMessage message) {
		return new ServerClientCommandMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, 
				message.getParameters().sessionKey), message.commandId, 500, "INTERNAL SERVER ERROR");
	}
}
