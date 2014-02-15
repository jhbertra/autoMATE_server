package com.automate.protocol.server.subParsers;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.automate.protocol.Message;
import com.automate.protocol.MessageFormatException;
import com.automate.protocol.MessageSubParser;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.util.xml.XmlFormatException;

public abstract class ServerMessageSubParser <M extends Message<ServerProtocolParameters>> 
extends MessageSubParser<M, ServerProtocolParameters> {
	
	protected ServerProtocolParameters parameters;
	private int majorVersion, minorVersion;
	private boolean sessionValid;
	private String sessionKey;

	/* (non-Javadoc)
	 * @see com.automate.protocol.MessageSubParser#parseXml(java.lang.String)
	 */
	@Override
	public M parseXml(String xml) throws XmlFormatException, IOException,
			MessageFormatException, SAXException, ParserConfigurationException {
		parameters = null;
		majorVersion = 0;
		minorVersion = 0;
		sessionValid = false;
		sessionKey = null;
		return super.parseXml(xml);
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(qName.equals("parameters")) {
			parameters = new ServerProtocolParameters(majorVersion, minorVersion, sessionValid, sessionKey);
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(qName.equals("parameter")) {
			String name = attributes.getValue("name");
			String value = attributes.getValue("value");
			if(value == null || value.isEmpty()) {
				throw new SAXException("parameter tag had no value.");
			}
			if(name.equals("version")) {
				String [] versionParts = value.split("\\.");
				if(versionParts.length != 2) {
					throw new SAXException("Version parameter value malformed: " + value);
				}
				try {
					majorVersion = Integer.parseInt(versionParts[0]);
					minorVersion = Integer.parseInt(versionParts[1]);
				} catch(NumberFormatException e) {
					throw new SAXException("Version parameter value not numeric: " + value, e);
				}
			} else if(name.equals("session-valid")) {
				if(!value.equalsIgnoreCase("false") && !value.equalsIgnoreCase("true")) {
					throw new SAXException("session-valid expects a boolean value, got " + value);
				}
				sessionValid = Boolean.parseBoolean(value);
			} else if(name.equals("session-key")) {
				sessionKey = value;
			} else {
				throw new SAXException("parameter " + name + " invalid.");
			}
		}
	}

}
