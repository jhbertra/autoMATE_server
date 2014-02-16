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

public class ClientCommandMessageHandler implements IMessageHandler<ClientCommandMessage, Void> {

	private IDatabaseManager dbManager;
	private ISecurityManager securityManager;
	
	@Override
	public Message<ServerProtocolParameters> handleMessage(int minorVersion, int majorVersion, boolean sessionValid, 
			ClientCommandMessage message, Void params) {
		 long nodeId = message.nodeId;
		 String commandName = message.name;
		 long commandId = message.commandId;
		 List<CommandArgument<?>> args = message.args;
		 
		 String sessionKey = securityManager.getSessionKeyForNode(nodeId);
		 Node node = dbManager.getNodeByUid(nodeId);
		 
		 if(node == null) {  			// the node does not exist
			 return new ServerClientCommandMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, 
					 message.getParameters().sessionKey), commandId, 400, "The specified node-id does not identify a node.");
			 
		 } else { 						// the node exists...
			 String username = securityManager.getUsername(message.getParameters().sessionKey);
			 User user = dbManager.getUserByUsername(username);
			 
			 if(node.userId != user.uid) { 		// ...but does not belong to the user.
				 return new ServerClientCommandMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, 
						 message.getParameters().sessionKey), commandId, 405, "The specified node does not belong to this user.");
				 
			 } else if(sessionKey == null) { 	// ...belongs to the user, but is not online.
				 return new ServerClientCommandMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, 
						 message.getParameters().sessionKey), commandId, 400, "The specified node is offline.");
				 
			 } else { 							// ..and is online, available for receiving commands.
				 return new ServerNodeCommandMessage(new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, sessionKey), 
						 nodeId, commandName, commandId, args);
			 }
		 }
	}
}
