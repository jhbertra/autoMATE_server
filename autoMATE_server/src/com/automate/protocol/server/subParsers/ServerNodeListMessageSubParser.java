package com.automate.protocol.server.subParsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.automate.protocol.MessageFormatException;
import com.automate.protocol.models.Node;
import com.automate.protocol.server.messages.ServerNodeListMessage;
import com.automate.util.xml.XmlFormatException;

public class ServerNodeListMessageSubParser extends ServerMessageSubParser<ServerNodeListMessage> {

	private List<Node> nodes = new ArrayList<Node>();
	
	/* (non-Javadoc)
	 * @see com.automate.protocol.server.ServerMessageSubParser#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(qName.equals("content")) {
			this.message = new ServerNodeListMessage(parameters, nodes);
		} else {
			super.endElement(uri, localName, qName);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.automate.protocol.server.subParsers.ServerMessageSubParser#parseXml(java.lang.String)
	 */
	@Override
	public ServerNodeListMessage parseXml(String xml)
			throws XmlFormatException, IOException, MessageFormatException,
			SAXException, ParserConfigurationException {
		nodes = new ArrayList<Node>();
		return super.parseXml(xml);
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.server.ServerMessageSubParser#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(qName.equals("node")) {
			String name = attributes.getValue("name");
			String idString = attributes.getValue("id");
			String manufacturerCode = attributes.getValue("manufacturer");
			String model = attributes.getValue("model");
			String maxVersion = attributes.getValue("max-version");
			String infoUrl = attributes.getValue("info-url");
			String commandListUrl = attributes.getValue("command-list-url");
			if(name == null) {
				throw new SAXException("name was null.");
			} else if(idString == null) {
				throw new SAXException("id was null.");
			} else if(manufacturerCode == null) {
				throw new SAXException("manufacturer was null.");
			} else if(model == null) {
				throw new SAXException("model was null.");
			} else if(maxVersion == null) {
				throw new SAXException("max-version was null.");
			} else if(infoUrl == null) {
				throw new SAXException("info-url was null.");
			} else if(commandListUrl == null) {
				throw new SAXException("command-list-url was null.");
			}
			long id;
			try {
				id = Long.parseLong(idString);
			} catch (RuntimeException e) {
				throw new SAXException("Unable to parse node id.");
			}
			nodes.add(new Node(name, id, manufacturerCode, model, maxVersion, infoUrl, commandListUrl));
		} else {
			super.startElement(uri, localName, qName, attributes);
		}
	}

}
