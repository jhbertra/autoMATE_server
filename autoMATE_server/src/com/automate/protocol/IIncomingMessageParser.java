package com.automate.protocol;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.automate.util.xml.XmlFormatException;

public interface IIncomingMessageParser<P extends ProtocolParameters> {

	public abstract Message<P> parse(String xml) throws XmlFormatException,
			IOException, MessageFormatException, SAXException,
			ParserConfigurationException;

}