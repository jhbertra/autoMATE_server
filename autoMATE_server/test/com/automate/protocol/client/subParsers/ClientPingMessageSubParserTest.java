package com.automate.protocol.client.subParsers;

import static org.junit.Assert.*;

import org.junit.Test;

import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.client.messages.ClientPingMessage;

public class ClientPingMessageSubParserTest {

	private ClientPingMessageSubParser subject;

	private String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<ping />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private ClientProtocolParameters parameters = new ClientProtocolParameters(0, 0, "session");
	
	@Test
	public void testParse() throws Exception {
		subject = new ClientPingMessageSubParser();
		ClientPingMessage expected = new ClientPingMessage(parameters);
		assertEquals(expected, subject.parseXml(xml1));
	}
	
}
