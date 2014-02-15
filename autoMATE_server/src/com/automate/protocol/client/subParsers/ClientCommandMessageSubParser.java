package com.automate.protocol.client.subParsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.automate.protocol.MessageFormatException;
import com.automate.protocol.client.messages.ClientCommandMessage;
import com.automate.protocol.models.CommandArgument;
import com.automate.protocol.models.Type;
import com.automate.protocol.models.Type.TypeFormatException;
import com.automate.util.xml.XmlFormatException;

public class ClientCommandMessageSubParser extends ClientMessageSubParser<ClientCommandMessage> {
	
	private long nodeId;
	private String name;
	private long commandId;
	private List<CommandArgument<?>> args = new ArrayList<CommandArgument<?>>();
	
	/* (non-Javadoc)
	 * @see com.automate.protocol.client.subParsers.ClientMessageSubParser#parseXml(java.lang.String)
	 */
	@Override
	public ClientCommandMessage parseXml(String xml) throws XmlFormatException,
			IOException, MessageFormatException, SAXException,
			ParserConfigurationException {
		nodeId = -1;
		name = null;
		commandId = -1;
		args = new ArrayList<CommandArgument<?>>();
		return super.parseXml(xml);
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.server.ServerMessageSubParser#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(qName.equals("content")) {
			this.message = new ClientCommandMessage(parameters, nodeId, name, commandId, args);
		} else {
			super.endElement(uri, localName, qName);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.automate.protocol.server.ServerMessageSubParser#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(qName.equals("argument")) {
			String name = attributes.getValue("name");
			String typeString = attributes.getValue("type");
			String value = attributes.getValue("value");
			if(name == null) {
				throw new SAXException("name was null.");
			} else if(typeString == null) {
				throw new SAXException("type was null.");
			} else if(value == null) {
				throw new SAXException("value was null.");
			}
			Type type;
			try {
				type = Type.parseType(typeString);
			} catch (TypeFormatException e) {
				throw new SAXException("Unable to parse status data type.");
			}
			args.add(CommandArgument.newCommandArgument(name, type, value));
		} else if(qName.equals("command")) {
			String nodeIdString = attributes.getValue("node-id");
			name = attributes.getValue("name");
			String commandIdString = attributes.getValue("command-id");
			if(nodeIdString == null) {
				throw new SAXException("node-id was null");
			}
			if(commandIdString == null) {
				throw new SAXException("command-id was null");
			}
			nodeId = Long.parseLong(nodeIdString);
			commandId = Long.parseLong(commandIdString);
		} else {
			super.startElement(uri, localName, qName, attributes);
		}
	}
	
}
