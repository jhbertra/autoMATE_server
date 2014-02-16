package com.automate.server.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.automate.protocol.client.messages.ClientMessageSuite;
import com.automate.protocol.client.subParsers.ClientMessageSubParserSuite;
import com.automate.protocol.server.messages.ServerMessageSuite;
import com.automate.protocol.server.subParsers.ServerMessageSubParserSuite;
import com.automate.server.messaging.handlers.MessageHandlerSuite;

@RunWith(Suite.class)
@SuiteClasses({
	ClientMessageSuite.class,
	ClientMessageSubParserSuite.class,
	ServerMessageSuite.class,
	ServerMessageSubParserSuite.class,
	MessageHandlerSuite.class})
public class TestSuite {

}
