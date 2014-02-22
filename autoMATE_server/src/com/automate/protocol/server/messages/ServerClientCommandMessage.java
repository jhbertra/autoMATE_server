package com.automate.protocol.server.messages;

import com.automate.protocol.Message;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlFormatException;

/**
 * Represents a Command message sent from the server to the clinet.
 * @author jamie.bertram
 *
 */
public class ServerClientCommandMessage extends Message<ServerProtocolParameters> {

	/**
	 * The uid of the command originally sent by the client.
	 */
	public final long commandId;
	
	/**
	 * The numeric response code.  one of:
	 * 
	 * <table border="1">
	 * <th>Code</th><th>Message</th><th>Description
	 * <tr>
	 * <td>200</td><td>OK 						</td><td>command executed successfully</td>
	 * </tr>
	 * <tr>
	 * <td>400</td><td>INVALID_NODE_ID	 		</td><td>no node with the given id exists</td>
	 * </tr>
	 * <tr>
	 * <td>401</td><td>INVALID_COMMAND 			</td><td>node did not recognise the command</td>
	 * </tr>
	 * <tr>
	 * <td>402</td><td>INVALID_ARGS 			</td><td>the arguments received were not expected for the given command</td>
	 * </tr>
	 * <tr>
	 * <td>403</td><td>DUPLICATE_COMMAND_ID 	</td><td>the node has serviced a command with the commandId already</td>
	 * </tr>
	 * <tr>
	 * <td>404</td><td>NODE_OFFLINE 			</td><td>the node cannot be reached by the server</td>
	 * </tr>
	 * <tr>
	 * <td>405</td><td>NODE_NOT_OWNED_BY_USER 	</td><td>the node specified does not belong to the user that sent the command</td>
	 * </tr>
	 * <tr>
	 * <td>500</td><td>INTERNAL_SERVER_ERROR	</td><td>An error occurred in the server while handling the message</td>
	 * </tr>
	 * </table>
	 */
	public final int responseCode;
	
	/**
	 * The result message (may be null or empty).
	 */
	public final String message;
	
	/**
	 * Creates a new {@link ServerClientCommandMessage}
	 * @param parameters the protocol parameters sent by the server
	 * @param commandId the ID of the command sent by the client
	 * @param responseCode the numeric response code
	 * @param message the possibly null or empty response message.
	 */
	public ServerClientCommandMessage(ServerProtocolParameters parameters, long commandId, int responseCode, String message) {
		super(parameters);
		if(commandId < 0) {
			throw new IllegalArgumentException("command-id is negative.");
		}
		this.commandId = commandId;
		this.responseCode = responseCode;
		this.message = message;
	}

	@Override
	protected void addContent() throws XmlFormatException {
		if(message == null) {
			addElement("command", true
					, new Attribute("command-id", String.valueOf(commandId))
					, new Attribute("response-code", String.valueOf(responseCode)));
		} else {
			addElement("command", true
					, new Attribute("command-id", String.valueOf(commandId))
					, new Attribute("response-code", String.valueOf(responseCode))
					, new Attribute("message", message));
		}
	}

	@Override
	public com.automate.protocol.Message.MessageType getMessageType() {
		return MessageType.COMMAND_NODE;
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.Message#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj)) {
			return	this.commandId == ((ServerClientCommandMessage)obj).commandId
					&& this.responseCode == ((ServerClientCommandMessage)obj).responseCode
					&& (this.message == null ? 
						((ServerClientCommandMessage)obj).message == null 
						: this.message.equals(((ServerClientCommandMessage)obj).message));
		} else return false;
	}

	@Override
	public String toString() {
		return super.toString() + "\nServerClientCommandMessage:\n" + "\ncommandId: " + commandId + "\nresponseCode: " + 
					responseCode + "\nmessage: " + message;
	}

}
