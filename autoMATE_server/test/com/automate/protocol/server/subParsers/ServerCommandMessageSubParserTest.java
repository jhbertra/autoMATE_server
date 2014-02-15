package com.automate.protocol.server.subParsers;

import static org.junit.Assert.*;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerCommandMessage;

public class ServerCommandMessageSubParserTest {

	private ServerCommandMessageSubParser subject;
	
	private String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<command />\n" +
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
			"\t\t<command response-code=\"200\" message=\"command succeeded!\" />\n" +
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
			"\t\t<command command-id=\"0\" message=\"command succeeded!\" />\n" +
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
			"\t\t<command command-id=\"0\" response-code=\"200\" />\n" +
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
			"\t\t<command command-id=\"0\" response-code=\"200\" message=\"command succeeded!\" />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private ServerProtocolParameters parameters = new ServerProtocolParameters(0, 0, true, "session");
	
	@Test(expected=SAXException.class)
	public void testNoAttributes() throws Exception {
		subject = new ServerCommandMessageSubParser();
		subject.parseXml(xml1);
	}
	
	@Test(expected=SAXException.class)
	public void testNoCommandId() throws Exception {
		subject = new ServerCommandMessageSubParser();
		subject.parseXml(xml2);
	}
	
	@Test(expected=SAXException.class)
	public void testNoResponseCode() throws Exception {
		subject = new ServerCommandMessageSubParser();
		subject.parseXml(xml3);
	}

	@Test
	public void testProperlyFormattedMessageMessage() throws Exception {
		subject = new ServerCommandMessageSubParser();
		ServerCommandMessage expected = new ServerCommandMessage(parameters, 0, 200, null);
		assertEquals(expected, subject.parseXml(xml4));
	}
	
	@Test
	public void testProperlyFormattedMessageWithMessage() throws Exception {
		subject = new ServerCommandMessageSubParser();
		ServerCommandMessage expected = new ServerCommandMessage(parameters, 0, 200, "command succeeded!");
		assertEquals(expected, subject.parseXml(xml5));
	}

}
