package com.automate.protocol.client.subParsers;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.automate.protocol.MessageFormatException;
import com.automate.protocol.client.messages.ClientWarningMessage;
import com.automate.util.xml.XmlFormatException;

public class ClientWarningMessageSubParser extends ClientMessageSubParser<ClientWarningMessage> {

	private long warningId;
	
	/* (non-Javadoc)
	 * @see com.automate.protocol.client.subParsers.ClientMessageSubParser#parseXml(java.lang.String)
	 */
	@Override
	public ClientWarningMessage parseXml(String xml) throws XmlFormatException,
			IOException, MessageFormatException, SAXException,
			ParserConfigurationException {
		warningId = -1;
		return super.parseXml(xml);
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.server.ServerMessageSubParser#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(qName.equals("content")) {
			this.message = new ClientWarningMessage(parameters, warningId);
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
		if(qName.equals("warning")) {
			try {				
				warningId = Long.parseLong(attributes.getValue("warning-id"));
			} catch(NumberFormatException e) {
				throw new SAXException(e);
			}
			if(warningId < 0) {
				throw new SAXException("Illegal value for warning id: " + warningId);
			}
		} else {
			super.startElement(uri, localName, qName, attributes);
		}
	}
	
}
