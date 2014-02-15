package com.automate.protocol.server.subParsers;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.automate.protocol.MessageFormatException;
import com.automate.protocol.server.messages.ServerNodeStatusUpdateMessage;
import com.automate.util.xml.XmlFormatException;

public class ServerNodeStatusUpdateMessageSubParser extends ServerMessageSubParser<ServerNodeStatusUpdateMessage> {

	private long nodeId;
	
	/* (non-Javadoc)
	 * @see com.automate.protocol.client.subParsers.ClientMessageSubParser#parseXml(java.lang.String)
	 */
	@Override
	public ServerNodeStatusUpdateMessage parseXml(String xml)
			throws XmlFormatException, IOException, MessageFormatException,
			SAXException, ParserConfigurationException {
		nodeId = -1;
		return super.parseXml(xml);
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.server.ServerMessageSubParser#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(qName.equals("content")) {
			this.message = new ServerNodeStatusUpdateMessage(parameters, nodeId);
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
		if(qName.equals("status-update")) {
			try {				
				nodeId = Long.parseLong(attributes.getValue("node-id"));
			} catch(NumberFormatException e) {
				throw new SAXException(e);
			}
			if(nodeId < 0) {
				throw new SAXException("Illegal value for node id: " + nodeId);
			}
		} else {
			super.startElement(uri, localName, qName, attributes);
		}
	}
	
}
