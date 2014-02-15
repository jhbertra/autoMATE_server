package com.automate.protocol.client.subParsers;

import static org.junit.Assert.*;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.client.messages.ClientWarningMessage;

public class ClientWarningMessageSubParserTest {

	private ClientWarningMessageSubParser subject;

	private String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<warning />\n" +
			"\t</content>\n" +
			"</message>\n";

	private String xml2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<warning warning-id=\"0\" />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private ClientProtocolParameters parameters = new ClientProtocolParameters(0, 0, "session");
	
	@Test(expected=SAXException.class)
	public void testNoNodeId() throws Exception {
		subject = new ClientWarningMessageSubParser();
		subject.parseXml(xml1);
	}
	
	@Test
	public void testValidMessage() throws Exception {
		subject = new ClientWarningMessageSubParser();
		ClientWarningMessage expected = new ClientWarningMessage(parameters, 0);
		assertEquals(expected, subject.parseXml(xml2));
	}
	
}
