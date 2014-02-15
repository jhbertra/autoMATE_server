package com.automate.protocol.client.subParsers;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.automate.protocol.MessageFormatException;
import com.automate.protocol.client.messages.ClientAuthenticationMessage;
import com.automate.util.xml.XmlFormatException;

public class ClientAuthenticationMessageSubParser extends ClientMessageSubParser<ClientAuthenticationMessage> {

	private String username;
	private String password;
	
	/* (non-Javadoc)
	 * @see com.automate.protocol.server.ServerMessageSubParser#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(qName.equals("content")) {
			this.message = new ClientAuthenticationMessage(parameters, username, password);
		} else {
			super.endElement(uri, localName, qName);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.automate.protocol.client.subParsers.ClientMessageSubParser#parseXml(java.lang.String)
	 */
	@Override
	public ClientAuthenticationMessage parseXml(String xml)
			throws XmlFormatException, IOException, MessageFormatException,
			SAXException, ParserConfigurationException {
		username = null;
		password = null;
		return super.parseXml(xml);
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.server.ServerMessageSubParser#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(qName.equals("authentication")) {
			username = attributes.getValue("username");
			password = attributes.getValue("password");
			if(username == null || username.isEmpty()) {
				throw new SAXException("username was null.");
			} else if(password == null || password.isEmpty()) {
				throw new SAXException("password was null.");
			}
		} else {
			super.startElement(uri, localName, qName, attributes);
		}
	}
}
