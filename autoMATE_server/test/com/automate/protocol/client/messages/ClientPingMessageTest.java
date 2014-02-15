package com.automate.protocol.client.messages;

import static org.junit.Assert.*;

import org.junit.Test;

import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.util.xml.XmlFormatException;

public class ClientPingMessageTest {

	private ClientPingMessage subject;
	
	private ClientProtocolParameters parameters = new ClientProtocolParameters(0, 0, "");

	@Test
	public void testToXml() {
		subject = new ClientPingMessage(parameters);
		StringBuilder builder = new StringBuilder();
		try {
			subject.toXml(builder, 0);
		} catch (XmlFormatException e) {
			fail(e.getMessage());
		}
		String expected = 	"content-type:ping\n" +
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
							"<message >\n" +
							"\t<parameters >\n" +
							"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
							"\t\t<parameter name=\"session-key\" value=\"\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<ping />\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}

}
