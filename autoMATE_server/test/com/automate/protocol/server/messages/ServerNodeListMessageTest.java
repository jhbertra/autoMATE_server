package com.automate.protocol.server.messages;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.automate.protocol.models.Node;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.util.xml.XmlFormatException;

public class ServerNodeListMessageTest {

	public ServerNodeListMessage subject;
	
	private ServerProtocolParameters parameters = new ServerProtocolParameters(0, 0, true, "session");

	@Test
	public void testToXmlNoNodes() {
		subject = new ServerNodeListMessage(parameters, null);
		StringBuilder builder = new StringBuilder();
		try {
			subject.toXml(builder, 0);
		} catch (XmlFormatException e) {
			fail(e.getMessage());
		}
		String expected = 	"content-type:node-list\n" +
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
							"<message >\n" +
							"\t<parameters >\n" +
							"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
							"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
							"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<node-list />\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}

	@Test
	public void testToXmlOneNode() {
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(new Node("Fridge", 0, "Whirlpool", "BF-192", "1.0", "infoUrl1", "commandListUrl1"));
		subject = new ServerNodeListMessage(parameters, nodes);
		StringBuilder builder = new StringBuilder();
		try {
			subject.toXml(builder, 0);
		} catch (XmlFormatException e) {
			fail(e.getMessage());
		}
		String expected = 	"content-type:node-list\n" +
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
							"<message >\n" +
							"\t<parameters >\n" +
							"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
							"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
							"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<node-list >\n" +
							"\t\t\t<node name=\"Fridge\" id=\"0\" manufacturer=\"Whirlpool\" model=\"BF-192\" max-version=\"1.0\" " +
							"info-url=\"infoUrl1\" command-list-url=\"commandListUrl1\" />\n" +
							"\t\t</node-list>\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}

	@Test
	public void testToXmlTwoNodes() {
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(new Node("Fridge", 0, "Whirlpool", "BF-192", "1.0", "infoUrl1", "commandListUrl1"));
		nodes.add(new Node("Couch", 1, "Lazyboy", "wtf", "1.1", "infoUrl2", "commandListUrl2"));
		subject = new ServerNodeListMessage(parameters, nodes);
		StringBuilder builder = new StringBuilder();
		try {
			subject.toXml(builder, 0);
		} catch (XmlFormatException e) {
			fail(e.getMessage());
		}
		String expected = 	"content-type:node-list\n" +
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
							"<message >\n" +
							"\t<parameters >\n" +
							"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
							"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
							"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<node-list >\n" +
							"\t\t\t<node name=\"Fridge\" id=\"0\" manufacturer=\"Whirlpool\" model=\"BF-192\" max-version=\"1.0\" " +
							"info-url=\"infoUrl1\" command-list-url=\"commandListUrl1\" />\n" +
							"\t\t\t<node name=\"Couch\" id=\"1\" manufacturer=\"Lazyboy\" model=\"wtf\" max-version=\"1.1\" " +
							"info-url=\"infoUrl2\" command-list-url=\"commandListUrl2\" />\n" +
							"\t\t</node-list>\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}
	
}
