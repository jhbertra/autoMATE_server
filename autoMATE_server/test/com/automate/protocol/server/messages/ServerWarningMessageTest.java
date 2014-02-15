package com.automate.protocol.server.messages;

import static org.junit.Assert.*;

import org.junit.Test;

import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.util.xml.XmlFormatException;

public class ServerWarningMessageTest {

	private ServerWarningMessage subject;
	private ServerProtocolParameters parameters = new ServerProtocolParameters(0, 0, true, "session");
	
	@Test(expected = NullPointerException.class)
	public void testNullMessage() {
		subject = new ServerWarningMessage(parameters, 0, 0, null);
	}
	
	@Test
	public void testToXml() {
		subject = new ServerWarningMessage(parameters, 0, 0, "I'm about to blow up!");
		StringBuilder builder = new StringBuilder();
		try {
			subject.toXml(builder, 0);
		} catch (XmlFormatException e) {
			fail(e.getMessage());
		}
		String expected = 	"content-type:warning\n" +
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
							"<message >\n" +
							"\t<parameters >\n" +
							"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
							"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
							"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<warning warning-id=\"0\" node-id=\"0\" message=\"I'm about to blow up!\" />\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}

}
