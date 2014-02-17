package com.automate.server.messaging.handlers;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.node.messages.NodeCommandMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerClientCommandMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.security.ISecurityManager;

public class NodeCommandMessageHandlerTest {

	private NodeCommandMessageHandler subject;
	private NodeCommandMessageHandler.TestAccess testAccess;
	private ClientProtocolParameters parameters = new ClientProtocolParameters(1, 0, "nodeSession");
	
	@Before
	public void setUp() throws Exception {
		Mockery context = new Mockery();
		subject = new NodeCommandMessageHandler(context.mock(IDatabaseManager.class), context.mock(ISecurityManager.class));
		testAccess = subject.testAccess;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetErrorMessage() {
		assertNull(testAccess.getErrorMessage(1, 0, true, new NodeCommandMessage(parameters , 0, 200, "OK")));
	}

	@Test
	public void testGetOkMessage() {
		ServerClientCommandMessage expected = new ServerClientCommandMessage(new ServerProtocolParameters(1, 0, true, "session"), 0, 200, "OK");
		assertEquals(expected, testAccess.getOkMessage(1, 0, true, new NodeCommandMessage(parameters , 0, 200, "OK"), "session"));
	}

	@Test
	public void testGetUserNotFoundMessage() {
		assertNull(testAccess.getUserNotFoundMessage(1, 0, true, new NodeCommandMessage(parameters , 0, 200, "OK")));
	}

	@Test
	public void testGetUserOfflineMessage() {
		assertNull(testAccess.getUserOfflineMessage(1, 0, true, new NodeCommandMessage(parameters , 0, 200, "OK")));
	}

}
