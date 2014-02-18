package com.automate.protocol.server.messages;

import static org.junit.Assert.*;

import org.junit.Test;

import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.util.xml.XmlFormatException;

public class ServerAuthenticationMessageTest {

	private ServerAuthenticationMessage subject;
	
	private ServerProtocolParameters parameters = new ServerProtocolParameters(0, 0, true, "session");
	
	@Test(expected=NullPointerException.class)
	public void testNullArgs() {
		subject = new ServerAuthenticationMessage(parameters, null, 0, null, null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullUsername() {
		subject = new ServerAuthenticationMessage(parameters, null, 200, "OK", "key");
	}
	
	@Test// should be fine
	public void testNullResponse() {
		subject = new ServerAuthenticationMessage(parameters, "username", 200, null, "key");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullSessionKey_Response200() {
		subject = new ServerAuthenticationMessage(parameters, "username", 200, "OK", null);
	}
	
	@Test// should be fine
	public void testNullSessionKey_Response400() {
		subject = new ServerAuthenticationMessage(parameters, "username", 400, "OK", null);
	}
	
	@Test// should be fine
	public void testNullSessionKey_Response500() {
		subject = new ServerAuthenticationMessage(parameters, "username", 500, "OK", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidResponse() {
		subject = new ServerAuthenticationMessage(parameters, "username", 600, "OK", null);
	}
	
	@Test
	public void testToXmlNoNulls() {
		subject = new ServerAuthenticationMessage(parameters, "username", 200, "OK", "key");
		StringBuilder builder = new StringBuilder();
		try {
			subject.toXml(builder, 0);
		} catch (XmlFormatException e) {
			fail(e.getMessage());
		}
		String expected = 	"content-type:authentication\n" +
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
							"<message >\n" +
							"\t<parameters >\n" +
							"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
							"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
							"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<authentication username=\"username\" response=\"200 OK\" session-key=\"key\" />\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}

	@Test
	public void testToXmlNullResponse() {
		subject = new ServerAuthenticationMessage(parameters, "username", 200, null, "key");
		StringBuilder builder = new StringBuilder();
		try {
			subject.toXml(builder, 0);
		} catch (XmlFormatException e) {
			fail(e.getMessage());
		}
		String expected = 	"content-type:authentication\n" +
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
							"<message >\n" +
							"\t<parameters >\n" +
							"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
							"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
							"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<authentication username=\"username\" response=\"200\" session-key=\"key\" />\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}

	@Test
	public void testToXmlNullSessionKey() {
		subject = new ServerAuthenticationMessage(parameters, "username", 400, "DENIED", null);
		StringBuilder builder = new StringBuilder();
		try {
			subject.toXml(builder, 0);
		} catch (XmlFormatException e) {
			fail(e.getMessage());
		}
		String expected = 	"content-type:authentication\n" +
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
							"<message >\n" +
							"\t<parameters >\n" +
							"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
							"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
							"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<authentication username=\"username\" response=\"400 DENIED\" />\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}
	
}
