package com.automate.protocol.client.messages;

import java.util.List;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.models.CommandArgument;
import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlFormatException;

public class ClientCommandMessage extends Message<ClientProtocolParameters> {

	public final long nodeId;
	public final String name;
	public final long commandId;
	
	public final List<CommandArgument<?>> args;
	
	public ClientCommandMessage(ClientProtocolParameters parameters, long nodeId, String name, long commandId, List<CommandArgument<?>> args) {
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

}
