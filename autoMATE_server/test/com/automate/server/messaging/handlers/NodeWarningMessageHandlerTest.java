package com.automate.server.messaging.handlers;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.node.messages.NodeWarningMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerClientWarningMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.security.ISecurityManager;

public class NodeWarningMessageHandlerTest {

	private NodeWarningMessageHandler subject;
	private HashMap<Long, String> pendingWarnings = new HashMap<Long, String>();
	private IDatabaseManager dbManager;
	private ISecurityManager securityManager;
	private Mockery context;
	private ClientProtocolParameters parameters = new ClientProtocolParameters(1, 0, "nodeSession");
	
	@Before
	public void setUp() throws Exception {
		context = new Mockery();
		dbManager = context.mock(IDatabaseManager.class);
		securityManager = context.mock(ISecurityManager.class);
		subject = new NodeWarningMessageHandler(pendingWarnings, dbManager, securityManager);
	}
	
	@After
	public void tearDown() throws Exception {
		pendingWarnings.clear();
	}

	@Test
	public void testGetOkMessage() {
		Message<ServerProtocolParameters> actual = subject.getOkMessage(1, 0, true, new NodeWarningMessage(parameters, 0, "warn"), "session");
		ServerClientWarningMessage expected = new ServerClientWarningMessage(new ServerProtocolParameters(1, 0, true, "session"), 0, 0, "warn");
		assertEquals(expected, actual);
		assertEquals("warn", pendingWarnings.get(0L));
	}

	@Test
	public void testGetErrorMessage() {
		Message<ServerProtocolParameters> actual = subject.getErrorMessage(1, 0, true, new NodeWarningMessage(parameters, 0, "warn"));
		assertNull(actual);
		assertFalse(pendingWarnings.containsKey(0L));
	}

	@Test
	public void testGetUserOfflineMessage() {
		Message<ServerProtocolParameters> actual = subject.getErrorMessage(1, 0, true, new NodeWarningMessage(parameters, 0, "warn"));
		assertNull(actual);
		assertFalse(pendingWarnings.containsKey(0L));
	}

	@Test
	public void testGetUserNotFoundMessage() {
		Message<ServerProtocolParameters> actual = subject.getErrorMessage(1, 0, true, new NodeWarningMessage(parameters, 0, "warn"));
		assertNull(actual);
		assertFalse(pendingWarnings.containsKey(0L));
	}

}
