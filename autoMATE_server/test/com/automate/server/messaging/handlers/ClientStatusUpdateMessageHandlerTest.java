package com.automate.server.messaging.handlers;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.automate.protocol.client.ClientProtocolParameters;
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
		parameters = new ClientProtocolParameters(1, 0, "clinetSession");
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test(expected = NullPointerException.class)
	public void testNullContructorArgs() {
		subject = new ClientStatusUpdateMessageHandler(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void testNullDbManager() {
		subject = new ClientStatusUpdateMessageHandler(null, securityManager);
	}

	@Test(expected = NullPointerException.class)
	public void testNullSecurityManager() {
		subject = new ClientStatusUpdateMessageHandler(dbManager, null);
	}
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
