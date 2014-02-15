package com.automate.protocol.client.subParsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.automate.protocol.client.messages.ClientPingMessage;

public class ClientPingMessageSubParser extends ClientMessageSubParser<ClientPingMessage>{

	/* (non-Javadoc)
	 * @see com.automate.protocol.server.ServerMessageSubParser#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(qName.equals("ping")) {
			this.message = new ClientPingMessage(parameters);
		} else {
			super.startElement(uri, localName, qName, attributes);
		}
	}

}
