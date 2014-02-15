package com.automate.protocol.server.subParsers;

import static org.junit.Assert.*;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerAuthenticationMessage;

public class ServerAuthenticationMessageSubParserTest {

	private ServerAuthenticationMessageSubParser subject;

	private String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
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
			"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<authentication response=\"200 OK\" session-key=\"session\" />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private String xml3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<authentication username=\"username\" session-key=\"session\" />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private String xml4 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<authentication username=\"username\" response=\"200 OK\" />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private String xml5 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<authentication username=\"username\" response=\"200\" session-key=\"session\" />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private String xml6 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<authentication username=\"username\" response=\"200 OK\" session-key=\"session\" />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private ServerProtocolParameters parameters = new ServerProtocolParameters(0, 0, true, "session");
	
	@Test(expected=SAXException.class)
	public void testNoUsernameOrResponseOrSessionKey() throws Exception {
		subject = new ServerAuthenticationMessageSubParser();
		subject.parseXml(xml1);
	}
	
	@Test(expected=SAXException.class)
	public void testNoUsername() throws Exception {
		subject = new ServerAuthenticationMessageSubParser();
		subject.parseXml(xml2);
	}
	
	@Test(expected=SAXException.class)
	public void testNoResponse() throws Exception {
		subject = new ServerAuthenticationMessageSubParser();
		subject.parseXml(xml3);
	}
	
	@Test(expected=SAXException.class)
	public void testNoSessionKey() throws Exception {
		subject = new ServerAuthenticationMessageSubParser();
		subject.parseXml(xml4);
	}

	@Test
	public void testProperlyFormattedMessageNoResponse() throws Exception {
		subject = new ServerAuthenticationMessageSubParser();
		ServerAuthenticationMessage expected = new ServerAuthenticationMessage(parameters, "username", 200, null, "session");
		assertEquals(expected, subject.parseXml(xml5));
	}
	
	@Test
	public void testProperlyFormattedMessageWithResponse() throws Exception {
		subject = new ServerAuthenticationMessageSubParser();
		ServerAuthenticationMessage expected = new ServerAuthenticationMessage(parameters, "username", 200, "OK", "session");
		assertEquals(expected, subject.parseXml(xml6));
	}

}
