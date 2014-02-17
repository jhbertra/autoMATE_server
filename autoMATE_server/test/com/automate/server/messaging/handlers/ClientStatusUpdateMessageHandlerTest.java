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
import com.automate.protocol.client.messages.ClientStatusUpdateMessage;
import com.automate.protocol.models.Status;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerClientCommandMessage;
import com.automate.protocol.server.messages.ServerClientStatusUpdateMessage;
import com.automate.protocol.server.messages.ServerNodeStatusUpdateMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.security.ISecurityManager;

public class ClientStatusUpdateMessageHandlerTest {

	private ClientStatusUpdateMessageHandler subject;
	private IDatabaseManager dbManager;
	private ISecurityManager securityManager;
	private Mockery context;
	private ClientProtocolParameters parameters;
	
	@Before
	public void setUp() throws Exception {
		context = new Mockery();
		dbManager = context.mock(IDatabaseManager.class);
		securityManager = context.mock(ISecurityManager.class);
		parameters = new ClientProtocolParameters(1, 0, "clientSession");
	}

	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testGetErrorMessage() {
		subject = new ClientStatusUpdateMessageHandler(dbManager, securityManager);
		context.checking(new Expectations() {{
		}});
		Message<ServerProtocolParameters>actual = subject.testAccess.getErrorMessage(1, 0, true, new ClientStatusUpdateMessage(parameters, 0));
		ServerClientStatusUpdateMessage expected = new ServerClientStatusUpdateMessage(new ServerProtocolParameters(1, 0, true, "clientSession"), 
				0, null);
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetNodeOfflineMessage() {
		subject = new ClientStatusUpdateMessageHandler(dbManager, securityManager);
		context.checking(new Expectations() {{
		}});
		Message<ServerProtocolParameters>actual = subject.testAccess.getNodeOfflineMessage(1, 0, true, new ClientStatusUpdateMessage(parameters, 0));
		ServerClientStatusUpdateMessage expected = new ServerClientStatusUpdateMessage(new ServerProtocolParameters(1, 0, true, "clientSession"), 
				0, null);
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetNonExistentNodeMessage() {
		subject = new ClientStatusUpdateMessageHandler(dbManager, securityManager);
		context.checking(new Expectations() {{
		}});
		Message<ServerProtocolParameters>actual = subject.testAccess.getNonExistentNodeMessage(1, 0, true, 
				new ClientStatusUpdateMessage(parameters, 0));
		ServerClientStatusUpdateMessage expected = new ServerClientStatusUpdateMessage(new ServerProtocolParameters(1, 0, true, "clientSession"), 
				0, null);
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetNotOwnedNodeMessage() {
		subject = new ClientStatusUpdateMessageHandler(dbManager, securityManager);
		context.checking(new Expectations() {{
		}});
		Message<ServerProtocolParameters>actual = subject.testAccess.getNotOwnedNodeMessage(1, 0, true, 
				new ClientStatusUpdateMessage(parameters, 0));
		ServerClientStatusUpdateMessage expected = new ServerClientStatusUpdateMessage(new ServerProtocolParameters(1, 0, true, "clientSession"), 
				0, null);
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetOkMessage() {
		subject = new ClientStatusUpdateMessageHandler(dbManager, securityManager);
		context.checking(new Expectations() {{
		}});
		Message<ServerProtocolParameters>actual = subject.testAccess.getOkMessage(1, 0, true, 
				new ClientStatusUpdateMessage(parameters, 0), "nodeSession");
		ServerNodeStatusUpdateMessage expected = new ServerNodeStatusUpdateMessage(new ServerProtocolParameters(1, 0, true, "nodeSession"), 0);
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}

}
