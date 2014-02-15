package com.automate.protocol.client.subParsers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = {
		ClientAuthenticationMessageSubParserTest.class, 
		ClientCommandMessageSubParserTest.class, 
		ClientNodeListMessageSubParserTest.class, 
		ClientPingMessageSubParserTest.class,
		ClientStatusUpdateMessageSubParserTest.class,
		ClientWarningMessageSubParserTest.class})
public class ClientMessageSubParserSuite {}
