package com.automate.protocol.client.messages;

import java.util.List;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.models.CommandArgument;
import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlFormatException;

/**
 * Message type that represents a client-side command message.
 * @author jamie.bertram
 *
 */
public class ClientCommandMessage extends Message<ClientProtocolParameters> {

	/**
	 * The id of the node to whicht he command is being sent
	 */
	public final long nodeId;
	/**
	 * The name of the command.
	 */
	public final String name;
	/**
	 * The command message's unique id (for tracking and acknowledgement purposes). 
	 */
	public final long commandId;
	
	/**
	 * The list of command arguments (may be null or empty).
	 */
	public final List<CommandArgument<?>> args;
	
	/**
	 * Creates a new {@link ClientCommandMessage}
	 * @param parameters the protocol parameters from the client
	 * @param nodeId the id of the node the command is being sent to
	 * @param name the name of the command
	 * @param commandId the id of the command message (for tracking purposes - should be unique for every message)
	 * @param args the list of command arguments (may be null or empty).
	 * @throws IllegalArgumentException if nodeId < 0
	 * @throws IllegalArgumentException if commandId < 0
	 * @throws IllegalArgumentException if name is null or empty
	 */
	public ClientCommandMessage(ClientProtocolParameters parameters, long nodeId, String name, long commandId, List<CommandArgument<?>> args) {
		super(parameters);
		if(nodeId < 0) {
			throw new IllegalArgumentException("nodeId less than zero. " + nodeId);
		}
		if(commandId < 0) {
			throw new IllegalArgumentException("commandId less than zero. " + nodeId);
		}
		if(name == null || name.isEmpty()) {
			throw new IllegalArgumentException("name is null or empty! " + name);
		}
		this.nodeId = nodeId;
		this.name = name;
		this.commandId = commandId;
		this.args = args;
	}

	@Override
	protected void addContent() throws XmlFormatException {
		if(args == null || args.isEmpty()) {
			if(name == null) {
				addElement("command", true
						, new Attribute("node-id", String.valueOf(nodeId))
						, new Attribute("command-id", String.valueOf(commandId)));				
			} else {
				addElement("command", true
						, new Attribute("node-id", String.valueOf(nodeId))
						, new Attribute("name", name)
						, new Attribute("command-id", String.valueOf(commandId)));
			}
		} else {
			if(name == null) {
				addElement("command", false
						, new Attribute("node-id", String.valueOf(nodeId))
						, new Attribute("command-id", String.valueOf(commandId)));				
			} else {
				addElement("command", false
						, new Attribute("node-id", String.valueOf(nodeId))
						, new Attribute("name", name)
						, new Attribute("command-id", String.valueOf(commandId)));
			}

			for(CommandArgument<?> arg : args) {
				arg.toXml(this.builder, this.indentationLevel);
			}
			
			closeElement();
		}
	}

	@Override
	public com.automate.protocol.Message.MessageType getMessageType() {
		return MessageType.COMMAND;
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.Message#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj)) {
			return 	this.nodeId == ((ClientCommandMessage)obj).nodeId
					&& (this.name == null ?
							((ClientCommandMessage)obj).name == null
							: this.name.equals(((ClientCommandMessage)obj).name))
					&& this.commandId == ((ClientCommandMessage)obj).commandId
					&& (this.args == null ?
						(((ClientCommandMessage)obj).args == null || ((ClientCommandMessage)obj).args.isEmpty())
						: this.args.equals(((ClientCommandMessage)obj).args));
		} else return false;
	}

	@Override
	public String toString() {
		String argsString;
		if(args == null || args.isEmpty()) {
			argsString = "";
		} else {
			StringBuilder argsSb = new StringBuilder();
			for(CommandArgument<?> arg : args) {
				argsSb.append(arg);
				argsSb.append('\n');
			}
			argsString = argsSb.toString();
		}
		return super.toString() + "\nClientCommandMessage:\nnodeId: " + nodeId + "\nname: " + name + "\ncommandId: " + commandId + "\nargs: " + argsString;
	}

}
