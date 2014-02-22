package com.automate.protocol.client.subParsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.automate.protocol.client.messages.ClientNodeRegistrationMessage;

public class ClientNodeRegistrationMessageSubParser extends ClientMessageSubParser<ClientNodeRegistrationMessage> {

	private long manufacturerId;
	private long modelId;
	private int maxVersionMajor;
	private int maxVersionMinor;
	private String name;

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(qName.equals("content")) {
			this.message = new ClientNodeRegistrationMessage(parameters, manufacturerId, modelId, name, maxVersionMajor, maxVersionMinor);
		} else {
			super.endElement(uri, localName, qName);
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(qName.equals("register-node")) {
			String manufacturerIdString = attributes.getValue("manufacturer-id");
			String modelIdString = attributes.getValue("model-id");
			String maxVersionString = attributes.getValue("max-version");
			name = attributes.getValue("name");
			if(manufacturerIdString == null) {
				throw new SAXException("manufacturer-id was null.");
			} else if(modelIdString == null) {
				throw new SAXException("model-id was null.");
			} else if(maxVersionString == null) {
				throw new SAXException("max-version was null.");
			}
			try {
				manufacturerId = Long.parseLong(manufacturerIdString);
			} catch(NumberFormatException e) {
				throw new SAXException("Error parsing manufacturer-id", e);
			}
			try {
				modelId = Long.parseLong(manufacturerIdString);
			} catch(NumberFormatException e) {
				throw new SAXException("Error parsing manufacturer-id", e);
			}
			try {
				maxVersionMajor = Integer.parseInt(manufacturerIdString.substring(0, manufacturerIdString.indexOf(".")));
				maxVersionMinor = Integer.parseInt(manufacturerIdString.substring(manufacturerIdString.indexOf(".") + 1));
			} catch(RuntimeException e) {
				throw new SAXException("Error parsing manufacturer-id", e);
			}
		} else {
			super.startElement(uri, localName, qName, attributes);
		}
	}
	
}
