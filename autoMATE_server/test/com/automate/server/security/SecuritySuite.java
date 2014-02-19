package com.automate.server.security;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = {
		SecurityManagerImplTest.class,
		SessionManagerTest.class,
		SecurityIntegrationTest.class
})
public class SecuritySuite {

}
