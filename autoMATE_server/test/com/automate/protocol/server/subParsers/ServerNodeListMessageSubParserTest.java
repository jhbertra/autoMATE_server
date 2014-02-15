package com.automate.protocol.server.subParsers;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.automate.protocol.models.Node;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerNodeListMessage;

public class ServerNodeListMessageSubParserTest {

	private ServerNodeListMessageSubParser subject;
	
	private String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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
	
	private String xml2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<message >\n" +
			"\t<parameters >\n" +
			"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
			"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
			"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
			"\t</parameters>\n" +
			"\t<content >\n" +
			"\t\t<node-list >\n" +
			"\t\t\t<node name=\"Fridge\" id=\"0\" manufacturer=\"Whirlpool\" model=\"BF-192\" max-version=\"1.0\" info-url=\"info\" " +
			"command-list-url=\"command\" />\n" +
			"\t\t</node-list>\n" +
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
			"\t\t<node-list >\n" +
			"\t\t\t<node name=\"Fridge\" id=\"0\" manufacturer=\"Whirlpool\" model=\"BF-192\" max-version=\"1.0\" info-url=\"info\" " +
			"command-list-url=\"command\" />\n" +
			"\t\t\t<node name=\"Couch\" id=\"1\" manufacturer=\"Lazyboy\" model=\"wtf\" max-version=\"1.1\" info-url=\"info2\" " +
			"command-list-url=\"command2\" />\n" +
			"\t\t</node-list>\n" +
			"\t</content>\n" +
			"</message>\n";
	
	
	private ServerProtocolParameters parameters = new ServerProtocolParameters(0, 0, true, "session");
	
	@Test
	public void testNoNodes() throws Exception {
		subject = new ServerNodeListMessageSubParser();
		ServerNodeListMessage expected = new ServerNodeListMessage(parameters, null);
		assertEquals(expected, subject.parseXml(xml1));
	}
	
	@Test
	public void testOneNode() throws Exception {
		subject = new ServerNodeListMessageSubParser();
		ArrayList<Node> nodes = new ArrayList<Node>();
		nodes.add(new Node("Fridge", 0, "Whirlpool", "BF-192", "1.0", "info", "command"));
		ServerNodeListMessage expected = new ServerNodeListMessage(parameters, nodes);
		assertEquals(expected, subject.parseXml(xml2));
	}
	
	@Test
	public void testTwoNodes() throws Exception {
		subject = new ServerNodeListMessageSubParser();
		ArrayList<Node> nodes = new ArrayList<Node>();
		nodes.add(new Node("Fridge", 0, "Whirlpool", "BF-192", "1.0", "info", "command"));
		nodes.add(new Node("Couch", 1, "Lazyboy", "wtf", "1.1", "info2", "command2"));
		ServerNodeListMessage expected = new ServerNodeListMessage(parameters, nodes);
		assertEquals(expected, subject.parseXml(xml3));
	}

}
