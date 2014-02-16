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
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerClientCommandMessage;
import com.automate.protocol.server.messages.ServerNodeCommandMessage;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.database.models.Node;
import com.automate.server.database.models.User;
import com.automate.server.security.ISecurityManager;

public class ClientCommandMessageHandlerTest {

	private ClientToNodeMessageHandler subject;
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

	@Test(expected = NullPointerException.class)
	public void testNullContructorArgs() {
		subject = new ClientCommandMessageHandler(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void testNullDbManager() {
		subject = new ClientCommandMessageHandler(null, securityManager);
	}

	@Test(expected = NullPointerException.class)
	public void testNullSecurityManager() {
		subject = new ClientCommandMessageHandler(dbManager, null);
	}

	@Test(expected = NullPointerException.class)
	public void testHandleMessage_NullMessage() {
		subject = new ClientCommandMessageHandler(dbManager, securityManager);
		context.checking(new Expectations() {{
		}});
		try {
			subject.handleMessage(1, 0, true, null, new ClientToNodeMessageHandlerParams(0));
		} finally {
			context.assertIsSatisfied();
		}
	}

	@Test
	public void testHandleMessage_NodeNonExistent() {
		subject = new ClientCommandMessageHandler(dbManager, securityManager);
		ClientCommandMessage message = new ClientCommandMessage(parameters, 0, "Turn on", 0, null);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(returnValue(null));
			oneOf(dbManager).getNodeByUid(0);					will(returnValue(null));
		}});
		
		ServerProtocolParameters responseParameters = new ServerProtocolParameters(1, 0, true, "session");
		ServerClientCommandMessage expected = new ServerClientCommandMessage(responseParameters, 0, 400, "INVALID NODE ID");
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}

	@Test
	public void testHandleMessage_NodeNonExistent_SecurityManagerException() {
		subject = new ClientCommandMessageHandler(dbManager, securityManager);
		ClientCommandMessage message = new ClientCommandMessage(parameters, 0, "Turn on", 0, null);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(throwException(new RuntimeException()));
		}});
		
		ServerProtocolParameters responseParameters = new ServerProtocolParameters(1, 0, true, "session");
		ServerClientCommandMessage expected = new ServerClientCommandMessage(responseParameters, 0, 500, "INTERNAL SERVER ERROR");
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}

	@Test
	public void testHandleMessage_NodeNonExistent_DbManagerException() {
		subject = new ClientCommandMessageHandler(dbManager, securityManager);
		ClientCommandMessage message = new ClientCommandMessage(parameters, 0, "Turn on", 0, null);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(returnValue(null));
			oneOf(dbManager).getNodeByUid(0);					will(throwException(new RuntimeException()));
		}});
		
		ServerProtocolParameters responseParameters = new ServerProtocolParameters(1, 0, true, "session");
		ServerClientCommandMessage expected = new ServerClientCommandMessage(responseParameters, 0, 500, "INTERNAL SERVER ERROR");
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}

	@Test
	public void testHandleMessage_NodeExistent_Online_DoenstBelongToUser() {
		subject = new ClientCommandMessageHandler(dbManager, securityManager);
		ClientCommandMessage message = new ClientCommandMessage(parameters, 0, "Turn on", 0, null);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(returnValue("nodeSession"));
			oneOf(dbManager).getNodeByUid(0);					will(returnValue(new Node(0, "Fan", 0, 0, "1.0")));
			oneOf(securityManager).getUsername("session");		will(returnValue("user2"));
			oneOf(dbManager).getUserByUsername("user2");		will(returnValue(new User(1, "user2", "Jim", "Bim", "pass", "email")));
		}});
		
		ServerProtocolParameters responseParameters = new ServerProtocolParameters(1, 0, true, "session");
		ServerClientCommandMessage expected = new ServerClientCommandMessage(responseParameters, 0, 405, "NODE NOT OWNED BY USER");
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}

	@Test
	public void testHandleMessage_NodeExistent_Online_DbManagerException2() {
		subject = new ClientCommandMessageHandler(dbManager, securityManager);
		ClientCommandMessage message = new ClientCommandMessage(parameters, 0, "Turn on", 0, null);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(returnValue("nodeSession"));
			oneOf(dbManager).getNodeByUid(0);					will(returnValue(new Node(0, "Fan", 0, 0, "1.0")));
			oneOf(securityManager).getUsername("session");		will(returnValue("user2"));
			oneOf(dbManager).getUserByUsername("user2");		will(returnValue(throwException(new RuntimeException())));
		}});
		
		ServerProtocolParameters responseParameters = new ServerProtocolParameters(1, 0, true, "session");
		ServerClientCommandMessage expected = new ServerClientCommandMessage(responseParameters, 0, 500, "INTERNAL SERVER ERROR");
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}

	@Test
	public void testHandleMessage_NodeExistent_Online_SecurityManagerException2() {
		subject = new ClientCommandMessageHandler(dbManager, securityManager);
		ClientCommandMessage message = new ClientCommandMessage(parameters, 0, "Turn on", 0, null);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(returnValue("nodeSession"));
			oneOf(dbManager).getNodeByUid(0);					will(returnValue(new Node(0, "Fan", 0, 0, "1.0")));
			oneOf(securityManager).getUsername("session");		will(returnValue(throwException(new RuntimeException())));
		}});
		
		ServerProtocolParameters responseParameters = new ServerProtocolParameters(1, 0, true, "session");
		ServerClientCommandMessage expected = new ServerClientCommandMessage(responseParameters, 0, 500, "INTERNAL SERVER ERROR");
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}

	@Test
	public void testHandleMessage_NodeExistent_Offline_DoenstBelongToUser() {
		subject = new ClientCommandMessageHandler(dbManager, securityManager);
		ClientCommandMessage message = new ClientCommandMessage(parameters, 0, "Turn on", 0, null);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(returnValue(null));
			oneOf(dbManager).getNodeByUid(0);					will(returnValue(new Node(0, "Fan", 0, 0, "1.0")));
			oneOf(securityManager).getUsername("session");		will(returnValue("user2"));
			oneOf(dbManager).getUserByUsername("user2");		will(returnValue(new User(1, "user2", "Jim", "Bim", "pass", "email")));
		}});
		
		ServerProtocolParameters responseParameters = new ServerProtocolParameters(1, 0, true, "session");
		ServerClientCommandMessage expected = new ServerClientCommandMessage(responseParameters, 0, 405, "NODE NOT OWNED BY USER");
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}

	@Test
	public void testHandleMessage_NodeExistent_Online_BelongsToUser() {
		subject = new ClientCommandMessageHandler(dbManager, securityManager);
		ClientCommandMessage message = new ClientCommandMessage(parameters, 0, "Turn on", 0, null);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(returnValue("nodeSession"));
			oneOf(dbManager).getNodeByUid(0);					will(returnValue(new Node(0, "Fan", 0, 0, "1.0")));
			oneOf(securityManager).getUsername("session");		will(returnValue("user2"));
			oneOf(dbManager).getUserByUsername("user2");		will(returnValue(new User(0, "user1", "Jim", "Bim", "pass", "email")));
		}});
		
		ServerProtocolParameters responseParameters = new ServerProtocolParameters(1, 0, true, "nodeSession");
		ServerNodeCommandMessage expected = new ServerNodeCommandMessage(responseParameters, 0, "Turn on", 0, null);
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}

	@Test
	public void testHandleMessage_NodeExistent_Offline_BelongsToUser() {
		subject = new ClientCommandMessageHandler(dbManager, securityManager);
		ClientCommandMessage message = new ClientCommandMessage(parameters, 0, "Turn on", 0, null);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(returnValue(""));
			oneOf(dbManager).getNodeByUid(0);					will(returnValue(new Node(0, "Fan", 0, 0, "1.0")));
			oneOf(securityManager).getUsername("session");		will(returnValue("user2"));
			oneOf(dbManager).getUserByUsername("user2");		will(returnValue(new User(0, "user1", "Jim", "Bim", "pass", "email")));
		}});
		
		ServerProtocolParameters responseParameters = new ServerProtocolParameters(1, 0, true, "session");
		ServerClientCommandMessage expected = new ServerClientCommandMessage(responseParameters, 0, 404, "NODE OFFLINE");
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(expected, actual);
	}

}
