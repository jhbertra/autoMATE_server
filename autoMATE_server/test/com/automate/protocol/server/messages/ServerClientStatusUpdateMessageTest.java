package com.automate.protocol.server.messages;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.automate.protocol.models.Status;
import com.automate.protocol.models.Type;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.util.xml.XmlFormatException;

public class ServerClientStatusUpdateMessageTest {
	
	private ServerClientStatusUpdateMessage subject;
	private ServerProtocolParameters parameters = new ServerProtocolParameters(0, 0, true, "session");
	
	@Test
	public void testNoStatuses() {
		subject = new ServerClientStatusUpdateMessage(parameters, 0, null);
		StringBuilder builder = new StringBuilder();
		try {
			subject.toXml(builder, 0);
		} catch (XmlFormatException e) {
			fail(e.getMessage());
		}
		String expected = 	"content-type:status-update-node\n" +
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
							"<message >\n" +
							"\t<parameters >\n" +
							"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
							"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
							"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<status-update node-id=\"0\" />\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testOneStatus() {
		List<Status<?>> statuses = new ArrayList<Status<?>>();
		statuses.add(Status.newStatus("Temperature", Type.INTEGER, 375));
		subject = new ServerClientStatusUpdateMessage(parameters, 0, statuses);
		StringBuilder builder = new StringBuilder();
		try {
			subject.toXml(builder, 0);
		} catch (XmlFormatException e) {
			fail(e.getMessage());
		}
		String expected = 	"content-type:status-update-node\n" +
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
							"<message >\n" +
							"\t<parameters >\n" +
							"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
							"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
							"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<status-update node-id=\"0\" >\n" +
							"\t\t\t<status component-name=\"Temperature\" type=\"integer\" value=\"375\" />\n" +
							"\t\t</status-update>\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testTwoStatuses() {
		List<Status<?>> statuses = new ArrayList<Status<?>>();
		statuses.add(Status.newStatus("Temperature", Type.INTEGER, 375));
		statuses.add(Status.newStatus("Mode", Type.STRING, "Bake"));
		subject = new ServerClientStatusUpdateMessage(parameters, 0, statuses);
		StringBuilder builder = new StringBuilder();
		try {
			subject.toXml(builder, 0);
		} catch (XmlFormatException e) {
			fail(e.getMessage());
		}
		String expected = 	"content-type:status-update-node\n" +
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
							"<message >\n" +
							"\t<parameters >\n" +
							"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
							"\t\t<parameter name=\"session-valid\" value=\"true\" />\n" +
							"\t\t<parameter name=\"session-key\" value=\"session\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<status-update node-id=\"0\" >\n" +
							"\t\t\t<status component-name=\"Temperature\" type=\"integer\" value=\"375\" />\n" +
							"\t\t\t<status component-name=\"Mode\" type=\"string\" value=\"Bake\" />\n" +
							"\t\t</status-update>\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}

}
