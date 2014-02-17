package com.automate.server.messaging.handlers;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.client.messages.ClientWarningMessage;

public class ClientWarningMessageHandlerTest {

	private ClientWarningMessageHandler subject;
	private HashMap<Long, String> pendingWarnings;
	private ClientProtocolParameters parameters;
	
	@Before
	public void setUp() throws Exception {
		parameters = new ClientProtocolParameters(1, 0, "session");
		pendingWarnings = new HashMap<Long, String>();
	}

	@After
	public void tearDown() throws Exception {
		pendingWarnings.clear();
	}

	@Test(expected = NullPointerException.class)
	public void testNullMap() {
		subject = new ClientWarningMessageHandler(null);
	}
	
	@Test
	public void testHandleMessage_WarningNotPending() {
		subject = new ClientWarningMessageHandler(pendingWarnings);
		assertNull(subject.handleMessage(1, 0, true, new ClientWarningMessage(parameters, 0), null));
	}
	
	@Test
	public void testHandleMessage_WarningPending() {
		subject = new ClientWarningMessageHandler(pendingWarnings);
		pendingWarnings.put(0L, "test");
		assertNull(subject.handleMessage(1, 0, true, new ClientWarningMessage(parameters, 0), null));
		assertFalse(pendingWarnings.containsKey(0L));
	}
	
}
