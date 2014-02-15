package com.automate.protocol.client.subParsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.automate.protocol.client.messages.ClientNodeListMessage;

public class ClientNodeListMessageSubParser extends ClientMessageSubParser<ClientNodeListMessage> {

	/* (non-Javadoc)
	 * @see com.automate.protocol.server.ServerMessageSubParser#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(qName.equals("node-list")) {
			this.message = new ClientNodeListMessage(parameters);
		} else {
			super.startElement(uri, localName, qName, attributes);
		}
	}
}
