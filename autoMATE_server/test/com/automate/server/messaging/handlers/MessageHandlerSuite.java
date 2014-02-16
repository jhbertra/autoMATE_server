package com.automate.server.messaging.handlers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = {
		AuthenticationMessageHandlerTest.class,
		ClientCommandMessageHandlerTest.class})
public class MessageHandlerSuite {

}
