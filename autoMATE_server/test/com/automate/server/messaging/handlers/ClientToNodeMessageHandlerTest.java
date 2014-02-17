package com.automate.server.messaging.handlers;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.database.models.Node;
import com.automate.server.database.models.User;
import com.automate.server.security.ISecurityManager;
import com.automate.util.xml.XmlFormatException;

public class ClientToNodeMessageHandlerTest {

	private Stub subject;
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
		subject = new Stub(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void testNullDbManager() {
		subject = new Stub(null, securityManager);
	}

	@Test(expected = NullPointerException.class)
	public void testNullSecurityManager() {
		subject = new Stub(dbManager, null);
	}

	@Test(expected = NullPointerException.class)
	public void testHandleMessage_NullMessage() {
		subject = new Stub(dbManager, securityManager);
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
		subject = new Stub(dbManager, securityManager);
		MockClientMessage message = new MockClientMessage(parameters);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(returnValue(null));
			oneOf(dbManager).getNodeByUid(0);					will(returnValue(null));
		}});
		
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(messageNonExistent, actual);
	}

	@Test
	public void testHandleMessage_NodeNonExistent_SecurityManagerException() {
		subject = new Stub(dbManager, securityManager);
		MockClientMessage message = new MockClientMessage(parameters);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(throwException(new RuntimeException()));
		}});
		
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(messageError, actual);
	}

	@Test
	public void testHandleMessage_NodeNonExistent_DbManagerException() {
		subject = new Stub(dbManager, securityManager);
		MockClientMessage message = new MockClientMessage(parameters);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(returnValue(null));
			oneOf(dbManager).getNodeByUid(0);					will(throwException(new RuntimeException()));
		}});
		
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(messageError, actual);
	}

	@Test
	public void testHandleMessage_NodeExistent_Online_DoenstBelongToUser() {
		subject = new Stub(dbManager, securityManager);
		MockClientMessage message = new MockClientMessage(parameters);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(returnValue("nodeSession"));
			oneOf(dbManager).getNodeByUid(0);					will(returnValue(new Node(0, "Fan", 0, 0, "1.0")));
			oneOf(securityManager).getUsername("session");		will(returnValue("user2"));
			oneOf(dbManager).getUserByUsername("user2");		will(returnValue(new User(1, "user2", "Jim", "Bim", "pass", "email")));
		}});
		
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(messageNotOwned, actual);
	}

	@Test
	public void testHandleMessage_NodeExistent_Online_DbManagerException2() {
		subject = new Stub(dbManager, securityManager);
		MockClientMessage message = new MockClientMessage(parameters);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(returnValue("nodeSession"));
			oneOf(dbManager).getNodeByUid(0);					will(returnValue(new Node(0, "Fan", 0, 0, "1.0")));
			oneOf(securityManager).getUsername("session");		will(returnValue("user2"));
			oneOf(dbManager).getUserByUsername("user2");		will(returnValue(throwException(new RuntimeException())));
		}});
		
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(messageError, actual);
	}

	@Test
	public void testHandleMessage_NodeExistent_Online_SecurityManagerException2() {
		subject = new Stub(dbManager, securityManager);
		MockClientMessage message = new MockClientMessage(parameters);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(returnValue("nodeSession"));
			oneOf(dbManager).getNodeByUid(0);					will(returnValue(new Node(0, "Fan", 0, 0, "1.0")));
			oneOf(securityManager).getUsername("session");		will(returnValue(throwException(new RuntimeException())));
		}});
		
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(messageError, actual);
	}

	@Test
	public void testHandleMessage_NodeExistent_Offline_DoenstBelongToUser() {
		subject = new Stub(dbManager, securityManager);
		MockClientMessage message = new MockClientMessage(parameters);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(returnValue(null));
			oneOf(dbManager).getNodeByUid(0);					will(returnValue(new Node(0, "Fan", 0, 0, "1.0")));
			oneOf(securityManager).getUsername("session");		will(returnValue("user2"));
			oneOf(dbManager).getUserByUsername("user2");		will(returnValue(new User(1, "user2", "Jim", "Bim", "pass", "email")));
		}});
		
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(messageNotOwned, actual);
	}

	@Test
	public void testHandleMessage_NodeExistent_Online_BelongsToUser() {
		subject = new Stub(dbManager, securityManager);
		MockClientMessage message = new MockClientMessage(parameters);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(returnValue("nodeSession"));
			oneOf(dbManager).getNodeByUid(0);					will(returnValue(new Node(0, "Fan", 0, 0, "1.0")));
			oneOf(securityManager).getUsername("session");		will(returnValue("user2"));
			oneOf(dbManager).getUserByUsername("user2");		will(returnValue(new User(0, "user1", "Jim", "Bim", "pass", "email")));
		}});
		
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(messageOk, actual);
	}

	@Test
	public void testHandleMessage_NodeExistent_Offline_BelongsToUser() {
		subject = new Stub(dbManager, securityManager);
		MockClientMessage message = new MockClientMessage(parameters);
		context.checking(new Expectations() {{
			oneOf(securityManager).getSessionKeyForNode(0); 	will(returnValue(""));
			oneOf(dbManager).getNodeByUid(0);					will(returnValue(new Node(0, "Fan", 0, 0, "1.0")));
			oneOf(securityManager).getUsername("session");		will(returnValue("user2"));
			oneOf(dbManager).getUserByUsername("user2");		will(returnValue(new User(0, "user1", "Jim", "Bim", "pass", "email")));
		}});
		
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, message, new ClientToNodeMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(messageOffline, actual);
	}
	
	ServerProtocolParameters serverParameters = new ServerProtocolParameters(1, 0, true, "session");
	
	private MockServerMessage messageNonExistent = new MockServerMessage(serverParameters);
	private MockServerMessage messageNotOwned = new MockServerMessage(serverParameters);
	private MockServerMessage messageOffline = new MockServerMessage(serverParameters);
	private MockServerMessage messageOk = new MockServerMessage(serverParameters);
	private MockServerMessage messageError = new MockServerMessage(serverParameters);
	
	private class MockServerMessage extends Message<ServerProtocolParameters> {

		public MockServerMessage(ServerProtocolParameters parameters) {
			super(parameters);
		}

		@Override
		protected void addContent() throws XmlFormatException {
		}

		@Override
		public com.automate.protocol.Message.MessageType getMessageType() {
			return null;
		}
		
	}
	
	private class MockClientMessage extends Message<ClientProtocolParameters> {

		public MockClientMessage(ClientProtocolParameters parameters) {
			super(parameters);
		}

		@Override
		protected void addContent() throws XmlFormatException {
		}

		@Override
		public com.automate.protocol.Message.MessageType getMessageType() {
			return null;
		}
		
	}
	
	private class Stub extends ClientToNodeMessageHandler<MockClientMessage> {

		public Stub(IDatabaseManager dbManager, ISecurityManager securityManager) {
			super(dbManager, securityManager);
		}

		@Override
		protected Message<ServerProtocolParameters> getNonExistentNodeMessage(
				int majorVersion, int minorVersion, boolean sessionValid,
				MockClientMessage message) {
			return messageNonExistent;
		}

		@Override
		protected Message<ServerProtocolParameters> getNotOwnedNodeMessage(
				int majorVersion, int minorVersion, boolean sessionValid,
				MockClientMessage message) {
			return messageNotOwned;
		}

		@Override
		protected Message<ServerProtocolParameters> getNodeOfflineMessage(
				int majorVersion, int minorVersion, boolean sessionValid,
				MockClientMessage message) {
			return messageOffline;
		}

		@Override
		protected Message<ServerProtocolParameters> getOkMessage(
				int majorVersion, int minorVersion, boolean sessionValid,
				MockClientMessage message, String nodeSessionKey) {
			return messageOk;
		}

		@Override
		protected Message<ServerProtocolParameters> getErrorMessage(
				int majorVersion, int minorVersion, boolean sessionValid,
				MockClientMessage message) {
			return messageError;
		}

		
	}

}
