package com.automate.server.security;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.database.models.User;

public class SecurityIntegrationTest {

	private SecurityManagerImpl subject;
	private SessionManager sessionManager;
	private IDatabaseManager dbManager;
	private Mockery context;
	
	@Before
	public void setUp() throws Exception {
		context = new Mockery();
		sessionManager = new SessionManager(2, 5);
		dbManager = context.mock(IDatabaseManager.class);
		subject = new SecurityManagerImpl(sessionManager, dbManager, 2, 5);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected=NullPointerException.class)
	public void testAuthenticateClient_NullUsername() {
		subject.authenticateClient(null, "password", "session", "ipAddress");
	}

	@Test(expected=NullPointerException.class)
	public void testAuthenticateClient_NullPassword() {
		subject.authenticateClient("user", null, "session", "ipAddress");
	}

	@Test(expected=NullPointerException.class)
	public void testAuthenticateClient_NullIpAddress() {
		subject.authenticateClient("user", "password", "session", null);
	}

	@Test
	public void testAuthenticateClient_InvalidUsername() {
		context.checking(new Expectations() {
			{
				oneOf(dbManager).getUserByUsername("user"); will(returnValue(null));
			}
		});
		assertNull(subject.authenticateClient("user", "password", "session", "ipAddress"));
		context.assertIsSatisfied();
	}

	@Test
	public void testAuthenticateClient_InvalidPassword() {
		context.checking(new Expectations() {
			{
				oneOf(dbManager).getUserByUsername("user"); will(returnValue(new User(0, "user", "", "", "pass", "")));
			}
		});
		assertNull(subject.authenticateClient("user", "password", "session", "ipAddress"));
		context.assertIsSatisfied();
	}

	@Test
	public void testAuthenticateClient_DbManagerThrowsException() {
		context.checking(new Expectations() {
			{
				oneOf(dbManager).getUserByUsername("user"); will(throwException(new RuntimeException()));
			}
		});
		assertNull(subject.authenticateClient("user", "password", "session", "ipAddress"));
		context.assertIsSatisfied();
	}

	@Test
	public void testAuthenticateClient_ValidPassword_NonNullSessionKey() {
		context.checking(new Expectations() {
			{
				oneOf(dbManager).getUserByUsername("user"); will(returnValue(new User(0, "user", "", "", "password", "")));
			}
		});
		assertNull(subject.authenticateClient("user", "password", "session", "ipAddress"));
		context.assertIsSatisfied();
	}

	@Test
	public void testAuthenticateClient_ValidPassword_NullSessionKey() {
		final String expected = "05abd747ada7d432c8583f9d87ec2c38";
		context.checking(new Expectations() {
			{
				oneOf(dbManager).getUserByUsername("user"); will(returnValue(new User(0, "user", "", "", "password", "")));
			}
		});
		assertEquals(expected, subject.authenticateClient("user", "password", null, "ipAddress"));
		context.assertIsSatisfied();
	}

	@Test
	public void testAuthenticateClient_ValidPassword_EmptySessionKey() {
		final String expected = "05abd747ada7d432c8583f9d87ec2c38";
		context.checking(new Expectations() {
			{
				oneOf(dbManager).getUserByUsername("user"); will(returnValue(new User(0, "user", "", "", "password", "")));
			}
		});
		assertEquals(expected, subject.authenticateClient("user", "password", "", "ipAddress"));
		context.assertIsSatisfied();
	}

	@Test(expected=NullPointerException.class)
	public void testGetIpAddress_NullSessionKey() {
		subject.getIpAddress(null);
	}

	@Test
	public void testGetIpAddress_InvalidSessionKey() {
		assertNull(subject.getIpAddress("sessionKey"));
	}

	@Test(expected=NullPointerException.class)
	public void testGetNodeId_NullSessionKey() {
		subject.getNodeId(null);
	}

	@Test
	public void testGetNodeId_InvalidSessionKey() {
		assertEquals(-1, subject.getNodeId("sessionKey"));
		context.assertIsSatisfied();
	}

	@Test
	public void testGetNodeId_ValidSessionKey() {
		String sessionKey = sessionManager.createNewNodeSession(1, "address");
		assertEquals(1, subject.getNodeId(sessionKey));
		context.assertIsSatisfied();
	}

	@Test(expected=NullPointerException.class)
	public void testGetSessionKeyForUsername_NullUsername() {
		subject.getSessionKeyForUsername(null);
	}

	@Test
	public void testGetSessionKeyForUsername_InvalidUsername() {
		assertNull(subject.getSessionKeyForUsername("user"));
		context.assertIsSatisfied();
	}

	@Test
	public void testGetSessionKeyForUsername_ValidUsername() {
		String key = sessionManager.createNewAppSession("user", "address");
		assertEquals(key, subject.getSessionKeyForUsername("user"));
		context.assertIsSatisfied();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetSessionKeyForNode_NegativeId() {
		subject.getSessionKeyForNode(-1);
	}

	@Test
	public void testGetSessionKeyForNode_InvalidId() {
		assertNull(subject.getSessionKeyForNode(0));
		context.assertIsSatisfied();
	}

	@Test
	public void testGetSessionKeyForNode_ValidId() {
		String key = sessionManager.createNewNodeSession(0, "address");
		assertEquals(key, subject.getSessionKeyForNode(0));
		context.assertIsSatisfied();
	}
	
	@Test(expected=NullPointerException.class)
	public void testGetUsername_NullSessionKey() {
		subject.getUsername(null);
	}

	@Test
	public void testGetUsername_InvalidSessionKey() {
		assertNull(subject.getUsername("sessionKey"));
		context.assertIsSatisfied();
	}

	@Test
	public void testGetUsername_ValidSessionKey() {
		String key = sessionManager.createNewAppSession("user", "address");
		assertEquals("user", subject.getUsername(key));
		context.assertIsSatisfied();
	}
	
	@Test(expected=NullPointerException.class)
	public void testValidateParameters_NullParameters() {
		subject.validateParameters(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValidateParameters_MajorVersionNegative() {
		subject.validateParameters(new ClientProtocolParameters(-1, 0, "session"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValidateParameters_MinorVersionNegative() {
		subject.validateParameters(new ClientProtocolParameters(0, -1, "session"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValidateParameters_MajorVersionTooBig() {
		subject.validateParameters(new ClientProtocolParameters(3, 0, "session"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValidateParameters_LargerThanMaxSupportedVersion() {
		subject.validateParameters(new ClientProtocolParameters(2, 6, "session"));
	}
	
	@Test
	public void testValidateParameters_MaxSupportedVersion_sessionValid() {
		String key = sessionManager.createNewAppSession("user", "address");
		assertTrue(subject.validateParameters(new ClientProtocolParameters(2, 5, key)));
		context.assertIsSatisfied();
	}
	
	@Test
	public void testValidateParameters_MinSupportedVersion_sessionValid() {
		String key = sessionManager.createNewAppSession("user", "address");
		assertTrue(subject.validateParameters(new ClientProtocolParameters(0, 0, key)));
		context.assertIsSatisfied();
	}
	
	@Test
	public void testValidateParameters_MaxSupportedVersion_sessionNotValid() {
		assertFalse(subject.validateParameters(new ClientProtocolParameters(2, 5, "session")));
		context.assertIsSatisfied();
	}
	
	@Test
	public void testValidateParameters_MinSupportedVersion_sessionNotValid() {
		assertFalse(subject.validateParameters(new ClientProtocolParameters(0, 0, "session")));
		context.assertIsSatisfied();
	}
	
}
