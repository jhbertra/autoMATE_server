package com.automate.protocol.server.messages;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = {
		ServerAuthenticationMessageTest.class, 
		ServerCommandMessageTest.class, 
		ServerNodeListMessageTest.class, 
		ServerPingMessageTest.class,
		ServerClientStatusUpdateMessageTest.class,
		ServerWarningMessageTest.class})
public class ServerMessageSuite {}
