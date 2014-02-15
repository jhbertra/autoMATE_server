package com.automate.protocol.server.subParsers;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.automate.protocol.MessageFormatException;
import com.automate.protocol.server.messages.ServerCommandMessage;
import com.automate.util.xml.XmlFormatException;

public class ServerCommandMessageSubParser extends ServerMessageSubParser<ServerCommandMessage> {
	
	private long commandId;
	private int responseCode;
	private String commandMessage;
	
	/* (non-Javadoc)
	 * @see com.automate.protocol.server.subParsers.ServerMessageSubParser#parseXml(java.lang.String)
	 */
	@Override
	public ServerCommandMessage parseXml(String xml) throws XmlFormatException,
			IOException, MessageFormatException, SAXException,
			ParserConfigurationException {
		commandId = 0;
		responseCode = 0;
		commandMessage = null;
		return super.parseXml(xml);
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.server.ServerMessageSubParser#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(qName.equals("content")) {
			this.message = new ServerCommandMessage(parameters, commandId, responseCode, commandMessage);
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
		if(qName.equals("command")) {
			String commandIdString = attributes.getValue("command-id");
			String responseString = attributes.getValue("response-code");
			commandMessage = attributes.getValue("message");
			if(commandIdString == null || commandIdString.isEmpty()) {
				throw new SAXException("command-id was null.");
			} else if(responseString == null || responseString.isEmpty()) {
				throw new SAXException("response-code was null.");
			}
			try {
				responseCode = Integer.parseInt(responseString);
			} catch(NumberFormatException e) {
				throw new SAXException("response code malformed, unable to parse code.");
			}
			try {
				commandId = Long.parseLong(commandIdString);
			} catch(NumberFormatException e) {
				throw new SAXException("command-id malformed, unable to parse command-id.");
			}
		} else {
			super.startElement(uri, localName, qName, attributes);
		}
	}
}
