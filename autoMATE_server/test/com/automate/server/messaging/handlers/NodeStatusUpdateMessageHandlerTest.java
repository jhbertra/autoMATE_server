package com.automate.server.messaging.handlers;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.node.messages.NodeStatusUpdateMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerClientStatusUpdateMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.security.ISecurityManager;

public class NodeStatusUpdateMessageHandlerTest {

	private NodeStatusUpdateMessageHandler subject;
	private IDatabaseManager dbManager;
	private ISecurityManager securityManager;
	private Mockery context;
	private ClientProtocolParameters parameters;
	
	@Before
	public void setUp() throws Exception {
		context = new Mockery();
		dbManager = context.mock(IDatabaseManager.class);
		securityManager = context.mock(ISecurityManager.class);
		subject  = new NodeStatusUpdateMessageHandler(dbManager, securityManager);
		parameters = new ClientProtocolParameters(1, 0, "nodeSession");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected=NullPointerException.class)
	public void testNullContructorArgs() {
		subject = new NodeStatusUpdateMessageHandler(null, null);
	}

	@Test(expected=NullPointerException.class)
	public void testNullDbManager() {
		subject = new NodeStatusUpdateMessageHandler(null, securityManager);
	}

	@Test(expected=NullPointerException.class)
	public void testNullSecurityManager() {
		subject = new NodeStatusUpdateMessageHandler(dbManager, null);
	}
	
	@Test
	public void testGetErrorMessage() {
		assertNull(subject.testAccess.getErrorMessage(1, 0, true, new NodeStatusUpdateMessage(parameters, 0, null)));
	}
	
	@Test
	public void testGetOkMessage() {
		Message<ServerProtocolParameters> actual = 
				subject.testAccess.getOkMessage(1, 0, true, new NodeStatusUpdateMessage(parameters, 0, null), "userSession");
		ServerClientStatusUpdateMessage expected = new ServerClientStatusUpdateMessage(
				new ServerProtocolParameters(1, 0, true, "userSession"), 0, null);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetUserNotFoundMessage() {
		assertNull(subject.testAccess.getUserNotFoundMessage(1, 0, true, new NodeStatusUpdateMessage(parameters, 0, null)));
	}
	
	@Test
	public void testGetUserOfflineMessage() {
		assertNull(subject.testAccess.getUserOfflineMessage(1, 0, true, new NodeStatusUpdateMessage(parameters, 0, null)));
	}

}
