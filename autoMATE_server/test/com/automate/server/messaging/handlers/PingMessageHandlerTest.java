package com.automate.server.messaging.handlers;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.client.messages.ClientPingMessage;
import com.automate.server.connectivity.IConnectivityManager;

public class PingMessageHandlerTest {

	private PingMessageHandler subject;
	private IConnectivityManager connectivityManager;
	private Mockery context;
	private ClientProtocolParameters parameters = new ClientProtocolParameters(1, 0, "session");
	
	@Before
	public void setUp() throws Exception {
		context = new Mockery();
		connectivityManager = context.mock(IConnectivityManager.class);
		subject = new PingMessageHandler(connectivityManager);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected=NullPointerException.class)
	public void testNullConnectivityManagaer() {
		subject = new PingMessageHandler(null);
	}

	@Test(expected=NullPointerException.class)
	public void testHandleMessage_NullMessage() {
		subject.handleMessage(1, 0, true, null, null);
	}

	@Test
	public void testHandleMessage() {
		context.checking(new Expectations() {{
			oneOf(connectivityManager).handleClientPing("session");
		}});
		assertNull(subject.handleMessage(1, 0, true, new ClientPingMessage(parameters), null));
		context.assertIsSatisfied();
	}

}
