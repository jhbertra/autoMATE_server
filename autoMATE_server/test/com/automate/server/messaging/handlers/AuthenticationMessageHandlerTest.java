package com.automate.server.messaging.handlers;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.client.messages.ClientAuthenticationMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerAuthenticationMessage;
import com.automate.server.security.ISecurityManager;

public class AuthenticationMessageHandlerTest {

	private AuthenticationMessageHandler subject;
	private Mockery context;
	private ISecurityManager securityManager;
	
	@Before
	public void setUp() {
		context = new Mockery();
		securityManager = context.mock(ISecurityManager.class);
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullSecurityManager() {
		subject = new AuthenticationMessageHandler(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testHandleMessage_NullMessage() {
		ClientProtocolParameters parameters = new ClientProtocolParameters(1, 0, "");
		context.checking(new Expectations() {{
		}});
		subject = new AuthenticationMessageHandler(securityManager);
		AuthenticationMessageHandlerParams params = new AuthenticationMessageHandlerParams("ipAddress");
		try {
			subject.handleMessage(1, 0, true, null, params);
		} finally {
			context.assertIsSatisfied();
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void testHandleMessage_NullParams() {
		ClientProtocolParameters parameters = new ClientProtocolParameters(1, 0, "");
		ClientAuthenticationMessage message = new ClientAuthenticationMessage(parameters, "username", "pas5word");
		context.checking(new Expectations() {{
		}});
		subject = new AuthenticationMessageHandler(securityManager);
		try {
			subject.handleMessage(1, 0, true, message, null);
		} finally {
			context.assertIsSatisfied();
		}
	}
	
	@Test
	public void testHandleMessage_ValidCredentials() {
		ClientProtocolParameters parameters = new ClientProtocolParameters(1, 0, "");
		ClientAuthenticationMessage message = new ClientAuthenticationMessage(parameters, "username", "pas5word");
		context.checking(new Expectations() {{
			oneOf(securityManager).authenticateClient("username", "pas5word", "", "ipAddress"); will(returnValue("sessionKey"));
		}});
		subject = new AuthenticationMessageHandler(securityManager);
		AuthenticationMessageHandlerParams params = new AuthenticationMessageHandlerParams("ipAddress");
		Message<ServerProtocolParameters> response = subject.handleMessage(1, 0, true, message, params);
		context.assertIsSatisfied();
		
		ServerAuthenticationMessage expected = new ServerAuthenticationMessage(
				new ServerProtocolParameters(1, 0, true, "sessionKey"), "username", 200, "OK", "sessionKey");
		
		assertEquals(expected, response);
	}
	
	@Test
	public void testHandleMessage_InvalidCredentials() {
		ClientProtocolParameters parameters = new ClientProtocolParameters(1, 0, "");
		ClientAuthenticationMessage message = new ClientAuthenticationMessage(parameters, "username", "pas5word");
		context.checking(new Expectations() {{
			oneOf(securityManager).authenticateClient("username", "pas5word", "", "ipAddress"); will(returnValue(""));
		}});
		subject = new AuthenticationMessageHandler(securityManager);
		AuthenticationMessageHandlerParams params = new AuthenticationMessageHandlerParams("ipAddress");
		
		Message<ServerProtocolParameters> response = subject.handleMessage(1, 0, true, message, params);
		
		context.assertIsSatisfied();
		ServerAuthenticationMessage expected = new ServerAuthenticationMessage(
				new ServerProtocolParameters(1, 0, true, ""), "username", 400, "DENIED", "");
		assertEquals(expected, response);
	}
	
	@Test
	public void testHandleMessage_InternalError() {
		ClientProtocolParameters parameters = new ClientProtocolParameters(1, 0, "");
		ClientAuthenticationMessage message = new ClientAuthenticationMessage(parameters, "username", "pas5word");
		context.checking(new Expectations() {{
			oneOf(securityManager).authenticateClient("username", "pas5word", "", "ipAddress"); will(throwException(new RuntimeException()));
		}});
		subject = new AuthenticationMessageHandler(securityManager);
		AuthenticationMessageHandlerParams params = new AuthenticationMessageHandlerParams("ipAddress");
		
		Message<ServerProtocolParameters> response = subject.handleMessage(1, 0, true, message, params);
		
		context.assertIsSatisfied();
		ServerAuthenticationMessage expected = new ServerAuthenticationMessage(
				new ServerProtocolParameters(1, 0, true, ""), "username", 500, "INTERNAL SERVER ERROR", "");
		assertEquals(expected, response);
	}

}
