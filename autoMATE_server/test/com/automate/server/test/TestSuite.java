package com.automate.server.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.automate.protocol.client.messages.ClientMessageSuite;
import com.automate.protocol.client.subParsers.ClientMessageSubParserSuite;
import com.automate.protocol.server.messages.ServerMessageSuite;
import com.automate.protocol.server.subParsers.ServerMessageSubParserSuite;
import com.automate.server.database.DatabaseSuite;
import com.automate.server.messaging.MessagingSuite;
import com.automate.server.messaging.handlers.MessageHandlerSuite;
import com.automate.server.security.SecuritySuite;

@RunWith(Suite.class)
@SuiteClasses({
	ClientMessageSuite.class,
	ClientMessageSubParserSuite.class,
	ServerMessageSuite.class,
	ServerMessageSubParserSuite.class,
	MessageHandlerSuite.class,
	SecuritySuite.class,
	MessagingSuite.class,
	DatabaseSuite.class
})
public class TestSuite {

}
