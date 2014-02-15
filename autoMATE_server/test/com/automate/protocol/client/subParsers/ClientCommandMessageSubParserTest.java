package com.automate.protocol.client.subParsers;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.client.messages.ClientCommandMessage;
import com.automate.protocol.models.CommandArgument;
import com.automate.protocol.models.Type;

public class ClientCommandMessageSubParserTest {

	private ClientCommandMessageSubParser subject;

	private String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
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
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<command name=\"shutdown\" command-id=\"0\" />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private String xml3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<command node-id=\"0\" name=\"shutdown\" />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private String xml4 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<command node-id=\"0\" command-id=\"0\" />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private String xml5 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<command node-id=\"0\" name=\"shutdown\" command-id=\"0\" />\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private String xml6 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<command node-id=\"0\" name=\"setPower\" command-id=\"0\" >\n" +
			"\t\t\t<argument name=\"on\" type=\"boolean\" value=\"true\" />\n" +
			"\t\t</command>\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private String xml7 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<command node-id=\"0\" name=\"setPower\" command-id=\"0\" >\n" +
			"\t\t\t<argument name=\"on\" type=\"boolean\" value=\"true\" />\n" +
			"\t\t\t<argument name=\"speed\" type=\"real\" value=\"2.5\" />\n" +
			"\t\t</command>\n" +
			"\t</content>\n" +
			"</message>\n";
	
	private ClientProtocolParameters parameters = new ClientProtocolParameters(0, 0, "session");
	
	@Test(expected=SAXException.class)
	public void testNoNodeIdNameOrCommandId() throws Exception {
		subject = new ClientCommandMessageSubParser();
		subject.parseXml(xml1);
	}
	
	@Test(expected=SAXException.class)
	public void testNoNodeId() throws Exception {
		subject = new ClientCommandMessageSubParser();
		subject.parseXml(xml2);
	}
	
	@Test(expected=SAXException.class)
	public void testNoCommandId() throws Exception {
		subject = new ClientCommandMessageSubParser();
		subject.parseXml(xml3);
	}
	
	@Test
	public void testProperlyFormattedMessageNoNameNoArgs() throws Exception {
		subject = new ClientCommandMessageSubParser();
		ClientCommandMessage expected = new ClientCommandMessage(parameters, 0, null, 0, null);
		assertEquals(expected, subject.parseXml(xml4));
	}
	
	@Test
	public void testProperlyFormattedMessageNoArgs() throws Exception {
		subject = new ClientCommandMessageSubParser();
		ClientCommandMessage expected = new ClientCommandMessage(parameters, 0, "shutdown", 0, null);
		assertEquals(expected, subject.parseXml(xml5));
	}
	
	@Test
	public void testProperlyFormattedMessageOneArg() throws Exception {
		subject = new ClientCommandMessageSubParser();
		List<CommandArgument<?>> args = new ArrayList<CommandArgument<?>>();
		args.add(CommandArgument.newCommandArgument("on", Type.BOOLEAN, true));
		ClientCommandMessage expected = new ClientCommandMessage(parameters, 0, "setPower", 0, args);
		assertEquals(expected, subject.parseXml(xml6));
	}
	
	@Test
	public void testProperlyFormattedMessageTwoArgs() throws Exception {
		subject = new ClientCommandMessageSubParser();
		List<CommandArgument<?>> args = new ArrayList<CommandArgument<?>>();
		args.add(CommandArgument.newCommandArgument("on", Type.BOOLEAN, true));
		args.add(CommandArgument.newCommandArgument("speed", Type.REAL, 2.5));
		ClientCommandMessage expected = new ClientCommandMessage(parameters, 0, "setPower", 0, args);
		assertEquals(expected, subject.parseXml(xml7));
	}

}
