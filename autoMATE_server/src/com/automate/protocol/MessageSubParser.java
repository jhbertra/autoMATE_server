package com.automate.protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.automate.util.xml.XmlFormatException;

public abstract class MessageSubParser<M extends Message<?>, P extends ProtocolParameters> extends DefaultHandler {
	
	protected M message;
	
	public M parseXml(String xml) throws XmlFormatException, IOException, MessageFormatException, SAXException, ParserConfigurationException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		parser.parse(new ByteArrayInputStream(xml.getBytes()), this);		
		return this.message;
	}
	
}
