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
	
	@Test// should be fine
	public void testNullSessionKey() {
		subject = new ServerAuthenticationMessage(parameters, "username", 200, "OK", null);
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
		subject = new ServerAuthenticationMessage(parameters, "username", 200, "OK", null);
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
							"\t\t<authentication username=\"username\" response=\"200 OK\" />\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}
	
}
