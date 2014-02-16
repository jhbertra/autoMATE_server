package com.automate.protocol.server.messages;

import static org.junit.Assert.*;

import org.junit.Test;

import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.util.xml.XmlFormatException;

public class ServerCommandMessageTest {

	private ServerClientCommandMessage subject;
	
	private ServerProtocolParameters parameters = new ServerProtocolParameters(0, 0, true, "session");
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullArgs() {
		subject = new ServerClientCommandMessage(parameters, -1, 0, null);
	}
	
	@Test
	public void testNullMssage() {
		subject = new ServerClientCommandMessage(parameters, 0, 0, null);
	}
	
	@Test
	public void testToXmlNoNulls() {
		subject = new ServerClientCommandMessage(parameters, 0, 200, "message");
		StringBuilder builder = new StringBuilder();
		try {
			subject.toXml(builder, 0);
		} catch (XmlFormatException e) {
			fail(e.getMessage());
		}
		String expected = 	"content-type:command\n" +
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
							"<message >\n" +
							"\t<parameters >\n" +
							"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
							"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
							"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<command command-id=\"0\" response-code=\"200\" message=\"message\" />\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToXmlNullMessage() {
		subject = new ServerClientCommandMessage(parameters, 0, 200, null);
		StringBuilder builder = new StringBuilder();
		try {
			subject.toXml(builder, 0);
		} catch (XmlFormatException e) {
			fail(e.getMessage());
		}
		String expected = 	"content-type:command\n" +
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
							"<message >\n" +
							"\t<parameters >\n" +
							"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
							"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
							"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<command command-id=\"0\" response-code=\"200\" />\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}

}
