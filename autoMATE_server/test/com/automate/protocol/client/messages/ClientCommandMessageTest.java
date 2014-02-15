package com.automate.protocol.client.messages;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.models.CommandArgument;
import com.automate.protocol.models.Type;
import com.automate.util.xml.XmlFormatException;

public class ClientCommandMessageTest {

	private ClientCommandMessage subject;
	
	private ClientProtocolParameters parameters = new ClientProtocolParameters(0, 0, "");
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvallidNodeId() {
		List<CommandArgument<?>> args = new ArrayList<CommandArgument<?>>();
		subject = new ClientCommandMessage(parameters, -1, "command", 0, args);
	}
	
	@Test
	public void testNullCommandName() {
		List<CommandArgument<?>> args = new ArrayList<CommandArgument<?>>();
		subject = new ClientCommandMessage(parameters, 0, null, 0, args);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidCommandId() {
		List<CommandArgument<?>> args = new ArrayList<CommandArgument<?>>();
		subject = new ClientCommandMessage(parameters, 0, "command", -1, args);
	}
	
	@Test
	public void testNullArgs() {
		subject = new ClientCommandMessage(parameters, 0, "command", 0, null);
	}
	
	@Test
	public void testToXmlNoNullsNoArgs() {
		List<CommandArgument<?>> args = new ArrayList<CommandArgument<?>>();
		subject = new ClientCommandMessage(parameters, 0, "command", 0, args);
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
							"\t\t<parameter name=\"session-key\" value=\"\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<command node-id=\"0\" name=\"command\" command-id=\"0\" />\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToXmlNoNullsOneArg() {
		List<CommandArgument<?>> args = new ArrayList<CommandArgument<?>>();
		args.add(CommandArgument.newCommandArgument("arg", Type.INTEGER, 10));
		subject = new ClientCommandMessage(parameters, 0, "command", 0, args);
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
							"\t\t<parameter name=\"session-key\" value=\"\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<command node-id=\"0\" name=\"command\" command-id=\"0\" >\n" +
							"\t\t\t<argument name=\"arg\" type=\"integer\" value=\"10\" />\n" +
							"\t\t</command>\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToXmlNoNullsTwoArgs() {
		List<CommandArgument<?>> args = new ArrayList<CommandArgument<?>>();
		args.add(CommandArgument.newCommandArgument("arg1", Type.INTEGER, 10));
		args.add(CommandArgument.newCommandArgument("arg2", Type.PERCENT, "50%"));
		subject = new ClientCommandMessage(parameters, 0, "command", 0, args);
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
							"\t\t<parameter name=\"session-key\" value=\"\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<command node-id=\"0\" name=\"command\" command-id=\"0\" >\n" +
							"\t\t\t<argument name=\"arg1\" type=\"integer\" value=\"10\" />\n" +
							"\t\t\t<argument name=\"arg2\" type=\"percent\" value=\"50.0%\" />\n" +
							"\t\t</command>\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToXmlNullCommandName() {
		List<CommandArgument<?>> args = new ArrayList<CommandArgument<?>>();
		args.add(CommandArgument.newCommandArgument("arg1", Type.INTEGER, 10));
		args.add(CommandArgument.newCommandArgument("arg2", Type.PERCENT, "50%"));
		subject = new ClientCommandMessage(parameters, 0, null, 0, args);
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
							"\t\t<parameter name=\"session-key\" value=\"\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<command node-id=\"0\" command-id=\"0\" >\n" +
							"\t\t\t<argument name=\"arg1\" type=\"integer\" value=\"10\" />\n" +
							"\t\t\t<argument name=\"arg2\" type=\"percent\" value=\"50.0%\" />\n" +
							"\t\t</command>\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToXmlNullArgs() {
		subject = new ClientCommandMessage(parameters, 0, "command", 0, null);
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
							"\t\t<parameter name=\"session-key\" value=\"\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<command node-id=\"0\" name=\"command\" command-id=\"0\" />\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}

}
