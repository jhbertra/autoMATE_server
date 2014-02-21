package com.automate.protocol;

import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.automate.util.xml.XmlFormatException;

public class IncomingMessageParser<P extends ProtocolParameters> implements IIncomingMessageParser<P> {

	private HashMap<String, MessageSubParser<Message<P>, P>> subParsers;
	
	public IncomingMessageParser(
			HashMap<String, MessageSubParser<Message<P>, P>> subParsers) {
		super();
		this.subParsers = subParsers;
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.IIncomingMessageParser#parse(java.lang.String)
	 */
	@Override
	public Message<P> parse(String xml) 
			throws XmlFormatException, IOException, MessageFormatException, SAXException, ParserConfigurationException {
		if(xml == null) {
			throw new NullPointerException("xml was null.");
		}
		if(xml.isEmpty()) {
			throw new IllegalArgumentException("xml was empty.");
		}
		String messageHeader;
		try {
			messageHeader = xml.substring(0, xml.indexOf('\n')).trim().toLowerCase();
		} catch (IndexOutOfBoundsException e) {
			throw new MessageFormatException("Malformed message contains no new lines.");
		}
		String contentType = null;
		if(messageHeader.startsWith("content-type:")) {
			contentType = messageHeader.split(":")[1].trim();
		}
		else {
			throw new MessageFormatException("All messages are required to start with a content-type header.");
		}
		try {
			return getSubParser(contentType).parseXml(xml);
		} catch (NoSuchElementException e) {
			throw new MessageFormatException("Unrecognized type header: " + messageHeader);
		}
	}
	
	private MessageSubParser<Message<P>, P> getSubParser(String contentType)  throws NoSuchElementException {
		 MessageSubParser<Message<P>, P> parser = subParsers.get(contentType);
		if(parser == null) {
			throw new NoSuchElementException();
		}
		return parser;
	}
	
}
