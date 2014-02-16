package com.automate.protocol.server.messages;

import java.util.List;

import com.automate.protocol.Message;
import com.automate.protocol.models.CommandArgument;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlFormatException;

public class ServerNodeCommandMessage extends Message<ServerProtocolParameters> {

	public final long nodeId;
	public final String name;
	public final long commandId;
	
	public final List<CommandArgument<?>> args;
	
	public ServerNodeCommandMessage(ServerProtocolParameters parameters, long nodeId, String name, long commandId, List<CommandArgument<?>> args) {
		super(parameters);
		if(nodeId < 0) {
			throw new IllegalArgumentException("nodeId less than zero. " + nodeId);
		}
		if(commandId < 0) {
			throw new IllegalArgumentException("commandId less than zero. " + nodeId);
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
			return 	this.nodeId == ((ServerNodeCommandMessage)obj).nodeId
					&& (this.name == null ?
							((ServerNodeCommandMessage)obj).name == null
							: this.name.equals(((ServerNodeCommandMessage)obj).name))
					&& this.commandId == ((ServerNodeCommandMessage)obj).commandId
					&& (this.args == null ?
						(((ServerNodeCommandMessage)obj).args == null || ((ServerNodeCommandMessage)obj).args.isEmpty())
						: this.args.equals(((ServerNodeCommandMessage)obj).args));
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
		return super.toString() + "\nServerNodeCommandMessage:\nnodeId: " + nodeId + "\nname: " + name + "\ncommandId: " 
		+ commandId + "\nnargs: " + argsString;
	}

}
