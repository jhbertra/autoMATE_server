package com.automate.protocol.server.subParsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.automate.protocol.MessageFormatException;
import com.automate.protocol.models.Status;
import com.automate.protocol.models.Type;
import com.automate.protocol.models.Type.TypeFormatException;
import com.automate.protocol.server.messages.ServerClientStatusUpdateMessage;
import com.automate.util.xml.XmlFormatException;

public class ServerClientStatusUpdateMessageSubParser extends ServerMessageSubParser<ServerClientStatusUpdateMessage> {
	
	private int nodeId;
	private List<Status<?>> statuses = new ArrayList<Status<?>>();
	
	/* (non-Javadoc)
	 * @see com.automate.protocol.server.ServerMessageSubParser#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(qName.equals("content")) {
			this.message = new ServerClientStatusUpdateMessage(parameters, nodeId, statuses);
		} else {
			super.endElement(uri, localName, qName);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.automate.protocol.server.subParsers.ServerMessageSubParser#parseXml(java.lang.String)
	 */
	@Override
	public ServerClientStatusUpdateMessage parseXml(String xml)
			throws XmlFormatException, IOException, MessageFormatException,
			SAXException, ParserConfigurationException {
		this.nodeId = -1;
		this.statuses = new ArrayList<Status<?>>();
		return super.parseXml(xml);
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.server.ServerMessageSubParser#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(qName.equals("status")) {
			String componentName = attributes.getValue("component-name");
			String typeString = attributes.getValue("type");
			String value = attributes.getValue("value");
			if(componentName == null) {
				throw new SAXException("component-name was null.");
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
			statuses.add(Status.newStatus(componentName, type, value));
		} else if(qName.equals("status-update")) {
			try {				
				nodeId = Integer.parseInt(attributes.getValue("node-id"));
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
