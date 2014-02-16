package com.automate.server.messaging.handlers;

import java.util.List;

import com.automate.protocol.Message;
import com.automate.protocol.client.messages.ClientCommandMessage;
import com.automate.protocol.models.CommandArgument;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerClientCommandMessage;
import com.automate.protocol.server.messages.ServerNodeCommandMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.database.models.Node;
import com.automate.server.database.models.User;
import com.automate.server.security.ISecurityManager;

/**
 * Delegate for handling Client Command Messages.
 * @author jamie.bertram
 *
 */
public class ClientCommandMessageHandler implements IMessageHandler<ClientCommandMessage, Void> {

	private IDatabaseManager dbManager;
	private ISecurityManager securityManager;

	/**
	 * Creates a new {@link ClientCommandMessageHandler}
	 * @param dbManager the {@link IDatabaseManager} to query
	 * @param securityManager the {@link ISecurityManager} that manages session data.
	 */
	public ClientCommandMessageHandler(IDatabaseManager dbManager,
			ISecurityManager securityManager) {
		super();
		if(dbManager == null) {
			throw new NullPointerException("dbManager was null.");
		} else if(securityManager == null) {
			throw new NullPointerException("securityManager was null.");
		}
		this.dbManager = dbManager;
		this.securityManager = securityManager;
	}

	/**
	 * Handles a {@link ClientCommandMessage}.
	 */
	@Override
	public Message<ServerProtocolParameters> handleMessage(int majorVersion, int minorVersion, boolean sessionValid, 
			ClientCommandMessage message, Void params) {
		if(message == null) {
			throw new NullPointerException("message was null.");
		}
		long nodeId = message.nodeId;
		String commandName = message.name;
		long commandId = message.commandId;
		List<CommandArgument<?>> args = message.args;

		try {
			String sessionKey = securityManager.getSessionKeyForNode(nodeId);
			Node node = dbManager.getNodeByUid(nodeId);

			if(node == null) {  			// the node does not exist
				return new ServerClientCommandMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, 
						message.getParameters().sessionKey), commandId, 400, "INVALID NODE ID");

			} else { 						// the node exists...
				String username = securityManager.getUsername(message.getParameters().sessionKey);
				User user = dbManager.getUserByUsername(username);

				if(node.userId != user.uid) { 		// ...but does not belong to the user.
					return new ServerClientCommandMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, 
							message.getParameters().sessionKey), commandId, 405, "NODE NOT OWNED BY USER");

				} else if(sessionKey == null || sessionKey.isEmpty()) { 	// ...belongs to the user, but is not online.
					return new ServerClientCommandMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, 
							message.getParameters().sessionKey), commandId, 404, "NODE OFFLINE");

				} else { 							// ..and is online, available for receiving commands.
					return new ServerNodeCommandMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, sessionKey), 
							nodeId, commandName, commandId, args);
				}
			}
		} catch (Exception e) {
			return new ServerClientCommandMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, 
					message.getParameters().sessionKey), commandId, 500, "INTERNAL SERVER ERROR");
		}
	}
}
