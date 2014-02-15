package com.automate.protocol;

import java.io.IOException;
import java.util.NoSuchElementException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.automate.util.xml.XmlFormatException;

public abstract class IncomingMessageParser<P extends ProtocolParameters> {

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
	
	protected abstract MessageSubParser<Message<P>, P> getSubParser(String contentType) throws NoSuchElementException;
	
}
