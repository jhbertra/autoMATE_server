package com.automate.protocol.server.subParsers;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.automate.protocol.MessageFormatException;
import com.automate.protocol.server.messages.ServerWarningMessage;
import com.automate.util.xml.XmlFormatException;

public class ServerWarningMessageSubParser extends ServerMessageSubParser<ServerWarningMessage> {

	private long warningId;
	private long nodeId;
	private String warningMessage;
	
	/* (non-Javadoc)
	 * @see com.automate.protocol.server.ServerMessageSubParser#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(qName.equals("content")) {
			this.message = new ServerWarningMessage(parameters, warningId, nodeId, warningMessage);
		} else {
			super.endElement(uri, localName, qName);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.automate.protocol.server.subParsers.ServerMessageSubParser#parseXml(java.lang.String)
	 */
	@Override
	public ServerWarningMessage parseXml(String xml) throws XmlFormatException,
			IOException, MessageFormatException, SAXException,
			ParserConfigurationException {
		warningId = -1;
		nodeId = -1;
		warningMessage = null;
		return super.parseXml(xml);
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
				nodeId = Long.parseLong(attributes.getValue("node-id"));
			} catch(NumberFormatException e) {
				throw new SAXException(e);
			}
			if(warningId < 0) {
				throw new SAXException("Illegal value for warning id: " + warningId);
			}
			if(nodeId < 0) {
				throw new SAXException("Illegal value for node id: " + warningId);
			}
			warningMessage = attributes.getValue("message");
			if(warningMessage == null) {
				throw new SAXException("warning message was null.");
			}
		} else {
			super.startElement(uri, localName, qName, attributes);
		}
	}
	
}
