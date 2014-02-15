package com.automate.protocol.client.messages;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = {
		ClientAuthenticationMessageTest.class, 
		ClientCommandMessageTest.class, 
		ClientNodeListMessageTest.class, 
		ClientPingMessageTest.class,
		ClientStatusUpdateMessageTest.class,
		ClientWarningMessageTest.class})
public class ClientMessageSuite {}
