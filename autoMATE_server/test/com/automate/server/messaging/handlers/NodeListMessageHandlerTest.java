package com.automate.server.messaging.handlers;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.client.messages.ClientNodeListMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerNodeListMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.database.models.Manufacturer;
import com.automate.server.database.models.Model;
import com.automate.server.database.models.Node;
import com.automate.server.security.ISecurityManager;

public class NodeListMessageHandlerTest {

	private NodeListMessageHandler subject;
	private IDatabaseManager dbManager;
	private ISecurityManager securityManager;
	private Mockery context;
	private ClientProtocolParameters parameters;
	
	@Before
	public void setUp() throws Exception {
		context = new Mockery();
		dbManager = context.mock(IDatabaseManager.class);
		securityManager = context.mock(ISecurityManager.class);
		subject = new NodeListMessageHandler(dbManager, securityManager);
		parameters = new ClientProtocolParameters(1, 0, "session");
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullArgs() {
		subject = new NodeListMessageHandler(null, null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullDbManager() {
		subject = new NodeListMessageHandler(dbManager, null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullSecurityManager() {
		subject = new NodeListMessageHandler(null, securityManager);
	}
	
	@Test(expected = NullPointerException.class)
	public void testHandleMessage_NullMessage() {
		subject.handleMessage(1, 0, true, null, null);
	}
	
	@Test
	public void testHandleMessage_NullNodes() {
		context.checking(new Expectations() {{
			oneOf(securityManager).getUsername("session"); will(returnValue("user"));
			oneOf(dbManager).getClientNodeList("user"); will(returnValue(null));
		}});
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, new ClientNodeListMessage(parameters), null);
		ServerNodeListMessage expected = new ServerNodeListMessage(new ServerProtocolParameters(1, 0, true, "session"), null);
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testHandleMessage_NoNodes() {
		context.checking(new Expectations() {{
			oneOf(securityManager).getUsername("session"); will(returnValue("user"));
			oneOf(dbManager).getClientNodeList("user"); will(returnValue(new ArrayList<Node>()));
		}});
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, new ClientNodeListMessage(parameters), null);
		ServerNodeListMessage expected = new ServerNodeListMessage(new ServerProtocolParameters(1, 0, true, "session"), null);
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testHandleMessage_InvalidUser() {
		context.checking(new Expectations() {{
			oneOf(securityManager).getUsername("session"); will(returnValue(null));
		}});
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, new ClientNodeListMessage(parameters), null);
		ServerNodeListMessage expected = new ServerNodeListMessage(new ServerProtocolParameters(1, 0, true, "session"), null);
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testHandleMessage_UserHasNodes() {
		final ArrayList<Node> dbNodes = new ArrayList<Node>();
		dbNodes.add(new Node(0, "node", 0, 0, "1.0"));
		ArrayList<com.automate.protocol.models.Node> protocolNodes = new ArrayList<com.automate.protocol.models.Node>();
		protocolNodes.add(new com.automate.protocol.models.Node("node", 0, "Samsung", "Fridge", "1.0", "infoUrl", "commandListUrl"));
		context.checking(new Expectations() {{
			oneOf(securityManager).getUsername("session"); will(returnValue("user"));
			oneOf(dbManager).getClientNodeList("user"); will(returnValue(dbNodes));
			oneOf(dbManager).getModelByUid(0); will(returnValue(new Model(0, 1, "infoUrl", "commandListUrl", "Fridge")));
			oneOf(dbManager).getManufacturerByUid(1); will(returnValue(new Manufacturer(1, "Samsung", "blah")));
		}});
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, new ClientNodeListMessage(parameters), null);
		ServerNodeListMessage expected = new ServerNodeListMessage(new ServerProtocolParameters(1, 0, true, "session"), protocolNodes);
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}
}
