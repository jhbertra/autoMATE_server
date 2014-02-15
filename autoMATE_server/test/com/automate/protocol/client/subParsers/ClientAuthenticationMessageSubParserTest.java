package com.automate.protocol.client.subParsers;

import static org.junit.Assert.*;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.client.messages.ClientAuthenticationMessage;

public class ClientAuthenticationMessageSubParserTest {

	private ClientAuthenticationMessageSubParser subject;

	private String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<authentication />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private String xml2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<authentication password=\"password\" />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private String xml3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<authentication username=\"username\" />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private String xml4 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<authentication username=\"username\" password=\"password\" />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private ClientProtocolParameters parameters = new ClientProtocolParameters(0, 0, "session");
	
	@Test(expected=SAXException.class)
	public void testNoUsernameOrPassword() throws Exception {
		subject = new ClientAuthenticationMessageSubParser();
		subject.parseXml(xml1);
	}
	
	@Test(expected=SAXException.class)
	public void testNoUsername() throws Exception {
		subject = new ClientAuthenticationMessageSubParser();
		subject.parseXml(xml2);
	}
	
	@Test(expected=SAXException.class)
	public void testNoPassword() throws Exception {
		subject = new ClientAuthenticationMessageSubParser();
		subject.parseXml(xml3);
	}
	
	@Test
	public void testProperlyFormattedMessage() throws Exception {
		subject = new ClientAuthenticationMessageSubParser();
		ClientAuthenticationMessage expected = new ClientAuthenticationMessage(parameters, "username", "password");
		assertEquals(expected, subject.parseXml(xml4));
	}

}
