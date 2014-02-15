package com.automate.protocol.server.subParsers;

import static org.junit.Assert.*;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerWarningMessage;

public class ServerWarningMessageSubParserTest {

	private ServerWarningMessageSubParser subject;
	
	private String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<warning />\n" +
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
			"\t\t<warning warning-id=\"0\" message=\"I'm gonna asplode.\" />\n" +
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
			"\t\t<warning node-id=\"0\" message=\"I'm gonna asplode.\" />\n" +
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
			"\t\t<warning node-id=\"0\" warning-id=\"0\" />\n" +
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
			"\t\t<warning node-id=\"0\" warning-id=\"0\" message=\"I'm gonna asplode.\" />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private ServerProtocolParameters parameters = new ServerProtocolParameters(0, 0, true, "session");
	
	@Test(expected=SAXException.class)
	public void testNoAttributes() throws Exception {
		subject = new ServerWarningMessageSubParser();
		subject.parseXml(xml1);
	}
	
	@Test(expected=SAXException.class)
	public void testNoNodeId() throws Exception {
		subject = new ServerWarningMessageSubParser();
		subject.parseXml(xml2);
	}
	
	@Test(expected=SAXException.class)
	public void testNoWarningId() throws Exception {
		subject = new ServerWarningMessageSubParser();
		subject.parseXml(xml3);
	}

	@Test(expected=SAXException.class)
	public void testNoMessage() throws Exception {
		subject = new ServerWarningMessageSubParser();
		subject.parseXml(xml4);
	}
	
	@Test
	public void testProperlyFormattedMessage() throws Exception {
		subject = new ServerWarningMessageSubParser();
		ServerWarningMessage expected = new ServerWarningMessage(parameters, 0, 0, "I'm gonna asplode.");
		assertEquals(expected, subject.parseXml(xml5));
	}

}
