package com.automate.server.messaging.handlers;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.client.messages.ClientCommandMessage;
import com.automate.protocol.client.messages.ClientStatusUpdateMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerClientCommandMessage;
import com.automate.protocol.server.messages.ServerClientStatusUpdateMessage;
import com.automate.protocol.server.messages.ServerNodeCommandMessage;
import com.automate.protocol.server.messages.ServerNodeStatusUpdateMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.database.models.Node;
import com.automate.server.database.models.User;
import com.automate.server.security.ISecurityManager;

public class ClientCommandMessageHandlerTest {

	private ClientCommandMessageHandler subject;
	private IDatabaseManager dbManager;
	private ISecurityManager securityManager;
	private Mockery context;
	private ClientProtocolParameters parameters;
	
	@Before
	public void setUp() throws Exception {
		context = new Mockery();
		dbManager = context.mock(IDatabaseManager.class);
		securityManager = context.mock(ISecurityManager.class);
		parameters = new ClientProtocolParameters(1, 0, "session");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetErrorMessage() {
		subject = new ClientCommandMessageHandler(dbManager, securityManager);
		context.checking(new Expectations() {{
		}});
		Message<ServerProtocolParameters>actual = subject.testAccess.getErrorMessage(1, 0, true, new ClientCommandMessage(parameters, 0, "turn off", 0, null));
		ServerClientCommandMessage expected = new ServerClientCommandMessage(new ServerProtocolParameters(1, 0, true, "session"), 0, 500, 
				"INTERNAL SERVER ERROR"); 
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetNodeOfflineMessage() {
		subject = new ClientCommandMessageHandler(dbManager, securityManager);
		context.checking(new Expectations() {{
		}});
		Message<ServerProtocolParameters>actual = subject.testAccess.getNodeOfflineMessage(1, 0, true, new ClientCommandMessage(parameters, 0, "turn off", 0, null));
		ServerClientCommandMessage expected = new ServerClientCommandMessage(new ServerProtocolParameters(1, 0, true, "session"), 0, 404, 
				"NODE OFFLINE"); 
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetNonExistentNodeMessage() {
		subject = new ClientCommandMessageHandler(dbManager, securityManager);
		context.checking(new Expectations() {{
		}});
		Message<ServerProtocolParameters>actual = subject.testAccess.getNonExistentNodeMessage(1, 0, true, new ClientCommandMessage(parameters, 0, "turn off", 0, null));
		ServerClientCommandMessage expected = new ServerClientCommandMessage(new ServerProtocolParameters(1, 0, true, "session"), 0, 400, 
				"INVALID NODE ID");  
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetNotOwnedNodeMessage() {
		subject = new ClientCommandMessageHandler(dbManager, securityManager);
		context.checking(new Expectations() {{
		}});
		Message<ServerProtocolParameters>actual = subject.testAccess.getNotOwnedNodeMessage(1, 0, true, new ClientCommandMessage(parameters, 0, "turn off", 0, null));
		ServerClientCommandMessage expected = new ServerClientCommandMessage(new ServerProtocolParameters(1, 0, true, "session"), 0, 405, 
				"NODE NOT OWNED BY USER"); 
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetOkMessage() {
		subject = new ClientCommandMessageHandler(dbManager, securityManager);
		context.checking(new Expectations() {{
		}});
		Message<ServerProtocolParameters>actual = subject.testAccess.getOkMessage(1, 0, true, new ClientCommandMessage(parameters, 0, "turn off", 0, null), "nodeSession");
		ServerNodeCommandMessage expected = new ServerNodeCommandMessage(new ServerProtocolParameters(1, 0, true, "nodeSession"), 0, "turn off", 0, null);
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}
	
}
