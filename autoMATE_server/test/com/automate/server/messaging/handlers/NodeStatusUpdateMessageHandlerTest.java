package com.automate.server.messaging.handlers;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.server.database.IDatabaseManager;
import com.automate.server.security.ISecurityManager;

public class NodeStatusUpdateMessageHandlerTest {

	private NodeStatusUpdateMessageHandler subject;
	private IDatabaseManager dbManager;
	private ISecurityManager securityManager;
	private Mockery context;
	private ClientProtocolParameters paramters;
	
	@Before
	public void setUp() throws Exception {
		context = new Mockery();
		dbManager = context.mock(IDatabaseManager.class);
		securityManager = context.mock(ISecurityManager.class);
		subject  = new NodeStatusUpdateMessageHandler(dbManager, securityManager);
		paramters = new ClientProtocolParameters(1, 0, "nodeSession");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNullContructorArgs() {
		
	}

}
