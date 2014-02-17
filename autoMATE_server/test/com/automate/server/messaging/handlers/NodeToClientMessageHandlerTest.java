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

public class NodeToClientMessageHandlerTest {

	private Stub subject;
	private IDatabaseManager dbManager;
	private ISecurityManager securityManager;
	private Mockery context;
	private ClientProtocolParameters parameters = new ClientProtocolParameters(1, 0, "nodeSession");
	
	@Before
	public void setUp() throws Exception {
		context = new Mockery();
		dbManager = context.mock(IDatabaseManager.class);
		securityManager = context.mock(ISecurityManager.class);
		subject = new Stub(dbManager, securityManager);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected=NullPointerException.class)
	public void testNullConstructorArgs() {
		subject = new Stub(null, null);
	}

	@Test(expected=NullPointerException.class)
	public void testNullDbManager() {
		subject = new Stub(null, securityManager);
	}

	@Test(expected=NullPointerException.class)
	public void testNullSecurityManager() {
		subject = new Stub(dbManager, null);
	}

	@Test(expected=NullPointerException.class)
	public void testHandleMessage_NullMessage() {
		subject.handleMessage(1, 0, true, null,	new NodeToClientMessageHandlerParams(0));
	}

	@Test(expected=NullPointerException.class)
	public void testHandleMessage_NullParams() {
		subject.handleMessage(1, 0, true, new MockNodeMessage(parameters), null);
	}

	@Test
	public void testHandleMessage_UserNotFound() {
		context.checking(new Expectations() {{
			oneOf(dbManager).getNodeByUid(0); will(returnValue(new Node(0, "node", 1, 0, "1.0")));
			oneOf(dbManager).getUserByUid(1); will(returnValue(null));
		}});
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, 
				new MockNodeMessage(parameters), new NodeToClientMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(messageUserNotFound, actual);
	}

	@Test
	public void testHandleMessage_UserOffline() {
		context.checking(new Expectations() {{
			oneOf(dbManager).getNodeByUid(0); will(returnValue(new Node(0, "node", 1, 0, "1.0")));
			oneOf(dbManager).getUserByUid(1); will(returnValue(new User(1, "user", "Jamie", "O'Bertram", "pass", "email")));
			oneOf(securityManager).getSessionKeyForUsername("user"); will(returnValue(null));
		}});
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, 
				new MockNodeMessage(parameters), new NodeToClientMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(messageUserOffline, actual);
	}

	@Test
	public void testHandleMessage_Ok() {
		context.checking(new Expectations() {{
			oneOf(dbManager).getNodeByUid(0); will(returnValue(new Node(0, "node", 1, 0, "1.0")));
			oneOf(dbManager).getUserByUid(1); will(returnValue(new User(1, "user", "Jamie", "O'Bertram", "pass", "email")));
			oneOf(securityManager).getSessionKeyForUsername("user"); will(returnValue("session"));
		}});
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, 
				new MockNodeMessage(parameters), new NodeToClientMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(messageOk, actual);
	}

	@Test
	public void testHandleMessage_Error1() {
		context.checking(new Expectations() {{
			oneOf(dbManager).getNodeByUid(0); will(throwException(new RuntimeException()));
		}});
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, 
				new MockNodeMessage(parameters), new NodeToClientMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(messageError, actual);
	}

	@Test
	public void testHandleMessage_Error2() {
		context.checking(new Expectations() {{
			oneOf(dbManager).getNodeByUid(0); will(returnValue(new Node(0, "node", 1, 0, "1.0")));
			oneOf(dbManager).getUserByUid(1); will(throwException(new RuntimeException()));
		}});
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, 
				new MockNodeMessage(parameters), new NodeToClientMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(messageError, actual);
	}

	@Test
	public void testHandleMessage_Error3() {
		context.checking(new Expectations() {{
			oneOf(dbManager).getNodeByUid(0); will(returnValue(new Node(0, "node", 1, 0, "1.0")));
			oneOf(dbManager).getUserByUid(1); will(returnValue(new User(1, "user", "Jamie", "O'Bertram", "pass", "email")));
			oneOf(securityManager).getSessionKeyForUsername("user"); will(throwException(new RuntimeException()));
		}});
		Message<ServerProtocolParameters> actual = subject.handleMessage(1, 0, true, 
				new MockNodeMessage(parameters), new NodeToClientMessageHandlerParams(0));
		context.assertIsSatisfied();
		assertEquals(messageError, actual);
	}
	
	private final MockServerMessage messageError = new  MockServerMessage(new ServerProtocolParameters(1, 0, true, "nodeSession"));
	private final MockServerMessage messageUserOffline = new  MockServerMessage(new ServerProtocolParameters(1, 0, true, "nodeSession"));
	private final MockServerMessage messageOk = new  MockServerMessage(new ServerProtocolParameters(1, 0, true, "session"));
	private final MockServerMessage messageUserNotFound = new  MockServerMessage(new ServerProtocolParameters(1, 0, true, "nodeSession"));
	
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

		@Override
		public boolean equals(Object obj) {
			return this == obj;
		}
		
	}
	
	private class MockNodeMessage extends Message<ClientProtocolParameters> {

		public MockNodeMessage(ClientProtocolParameters parameters) {
			super(parameters);
		}

		@Override
		protected void addContent() throws XmlFormatException {
		}

		@Override
		public com.automate.protocol.Message.MessageType getMessageType() {
			return null;
		}

		@Override
		public boolean equals(Object obj) {
			return this == obj;
		}
		
	}
	
	private class Stub extends NodeToClientMessageHandler<MockNodeMessage> {

		public Stub(IDatabaseManager dbManager, ISecurityManager securityManager) {
			super(dbManager, securityManager);
		}

		@Override
		protected Message<ServerProtocolParameters> getUserOfflineMessage(
				int majorVersion, int minorVersion, boolean sessionValid,
				MockNodeMessage message) {
			return messageUserOffline;
		}

		@Override
		protected Message<ServerProtocolParameters> getErrorMessage(
				int majorVersion, int minorVersion, boolean sessionValid,
				MockNodeMessage message) {
			return messageError;
		}

		@Override
		protected Message<ServerProtocolParameters> getOkMessage(
				int majorVersion, int minorVersion, boolean sessionValid,
				MockNodeMessage message, String sessionKey) {
			return messageOk;
		}

		@Override
		protected Message<ServerProtocolParameters> getUserNotFoundMessage(
				int majorVersion, int minorVersion, boolean sessionValid,
				MockNodeMessage message) {
			return messageUserNotFound;
		}
		
	}

}
