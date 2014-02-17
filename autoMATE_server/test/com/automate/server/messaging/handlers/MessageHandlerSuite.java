package com.automate.server.messaging.handlers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = {
		AuthenticationMessageHandlerTest.class,
		ClientCommandMessageHandlerTest.class,
		ClientStatusUpdateMessageHandlerTest.class,
		ClientToNodeMessageHandlerTest.class,
		ClientWarningMessageHandlerTest.class,
		NodeCommandMessageHandlerTest.class,
		NodeListMessageHandlerTest.class,
		NodeStatusUpdateMessageHandlerTest.class,
		NodeToClientMessageHandlerTest.class,
		NodeWarningMessageHandlerTest.class,
		PingMessageHandlerTest.class})
public class MessageHandlerSuite {

}
