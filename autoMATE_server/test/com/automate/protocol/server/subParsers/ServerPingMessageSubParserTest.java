package com.automate.protocol.server.subParsers;

import static org.junit.Assert.*;


import org.junit.Test;

import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerPingMessage;

public class ServerPingMessageSubParserTest {

	private ServerPingMessageSubParser subject;
	
	private String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<ping />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private ServerProtocolParameters parameters = new ServerProtocolParameters(0, 0, true, "session");
	
	@Test
	public void testParse() throws Exception {
		subject = new ServerPingMessageSubParser();
		ServerPingMessage expected = new ServerPingMessage(parameters);
		assertEquals(expected, subject.parseXml(xml1));
	}
	
}
