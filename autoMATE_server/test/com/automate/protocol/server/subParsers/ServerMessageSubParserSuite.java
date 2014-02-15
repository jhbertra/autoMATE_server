package com.automate.protocol.server.subParsers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = {
		ServerAuthenticationMessageSubParserTest.class, 
		ServerCommandMessageSubParserTest.class, 
		ServerNodeListMessageSubParserTest.class, 
		ServerPingMessageSubParserTest.class,
		ServerClientStatusUpdateMessageSubParserTest.class,
		ServerWarningMessageSubParserTest.class})
public class ServerMessageSubParserSuite {}
