package com.automate.server.security;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SessionManagerTest {

	private SessionManager subject;
	
	@Before
	public void setUp() throws Exception {
		subject = new SessionManager(1, 0);
	}

	@After
	public void tearDown() throws Exception {
	}

	/*
	 * createNewAppSession unit tests
	 */

	@Test(expected=NullPointerException.class)
	public void testCreateNewAppSession_NullArgs() {
		subject.createNewAppSession(null, null);
	}

	@Test(expected=NullPointerException.class)
	public void testCreateNewAppSession_NullUsername() {
		subject.createNewAppSession(null, "address1");
	}

	@Test(expected=NullPointerException.class)
	public void testCreateNewAppSession_NullIpAddress() {
		subject.createNewAppSession("user1", null);
	}

	@Test
	public void testCreateNewAppSession_NewSession() {
		String expected = "2c2e755a6a7c0221acda26403155867f";
		assertEquals(expected, subject.createNewAppSession("user1", "address1"));
	}

	@Test
	public void testCreateNewAppSession_SameUserTriesAgain() {
		subject.createNewAppSession("user1", "address1");
		assertNull(subject.createNewAppSession("user1", "address2"));
	}

	@Test
	public void testCreateNewAppSession_TwoUsersTry() {
		String expected1 = "2c2e755a6a7c0221acda26403155867f";
		String expected2 = "c93a9e58a71d3c3bf37200a4bcccc366";
		assertEquals(expected1, subject.createNewAppSession("user1", "address1"));
		assertEquals(expected2, subject.createNewAppSession("user2", "address2"));
	}
	
	/*
	 * createNewNodeSession unit tests
	 */

	@Test(expected=IllegalArgumentException.class)
	public void testCreateNewNodeSession_NullUsername() {
		subject.createNewNodeSession(-1, "address1");
	}

	@Test(expected=NullPointerException.class)
	public void testCreateNewNodeSession_NullIpAddress() {
		subject.createNewNodeSession(0, null);
	}

	@Test
	public void testCreateNewNodeSession_NewSession() {
		String expected = "a1839b83e892687dd535edc22d15dd36";
		assertEquals(expected, subject.createNewNodeSession(0, "address1"));
	}

	@Test
	public void testCreateNewNodeSession_SameUserTriesAgain() {
		subject.createNewNodeSession(0, "address1");
		assertNull(subject.createNewNodeSession(0, "address2"));
	}

	@Test
	public void testCreateNewNodeSession_TwoUsersTry() {
		String expected1 = "a1839b83e892687dd535edc22d15dd36";
		String expected2 = "74ccc85b9a305713da923f7ecf2e1adb";
		assertEquals(expected1, subject.createNewNodeSession(0, "address1"));
		assertEquals(expected2, subject.createNewNodeSession(1, "address2"));
	}
	
	@Test 
	public void testCreateNewSession_NodeTriesThenAppTries() {
		String expected1 = "310d7fe77650b4b2b33845f88eaa3ebc";
		String expected2 = "1df81579e50db3612dbdf59a1a819b72";
		assertEquals(expected1, subject.createNewNodeSession(123456, "address1"));
		assertEquals(expected2, subject.createNewAppSession("123456", "address2"));		
	}
	
	@Test 
	public void testCreateNewSession_AppTriesThenNodeTries() {
		String expected1 = "1df81579e50db3612dbdf59a1a819b72";
		String expected2 = "310d7fe77650b4b2b33845f88eaa3ebc";
		assertEquals(expected1, subject.createNewAppSession("123456", "address2"));		
		assertEquals(expected2, subject.createNewNodeSession(123456, "address1"));
	}
	
	/*
	 * getIpAddressForSessionKey unit tests
	 */

	@Test(expected=NullPointerException.class)
	public void testGetIpAddressForSessionKey_NullSessionKey() {
		subject.getIpAddressForSessionKey(null);
	}

	@Test
	public void testGetIpAddressForSessionKey_SessionKeyInvalid() {
		assertNull(subject.getIpAddressForSessionKey("2c2e755a6a7c0221acda26403155867f"));
	}

	@Test
	public void testGetIpAddressForSessionKey_SessionKeyValid_OneExistingAppSession() {
		subject.createNewAppSession("user1", "address1");
		assertEquals("address1", subject.getIpAddressForSessionKey("2c2e755a6a7c0221acda26403155867f"));
	}

	@Test
	public void testGetIpAddressForSessionKey_SessionKeyValid_TwoExistingAppSessions() {
		subject.createNewAppSession("user1", "address1");
		subject.createNewAppSession("user2", "address2");
		assertEquals("address1", subject.getIpAddressForSessionKey("2c2e755a6a7c0221acda26403155867f"));
		assertEquals("address2", subject.getIpAddressForSessionKey("c93a9e58a71d3c3bf37200a4bcccc366"));
	}

	@Test
	public void testGetIpAddressForSessionKey_SessionKeyValid_OneExistingNodeSession() {
		subject.createNewNodeSession(123456, "address1");
		assertEquals("address1", subject.getIpAddressForSessionKey("310d7fe77650b4b2b33845f88eaa3ebc"));
	}

	@Test
	public void testGetIpAddressForSessionKey_SessionKeyValid_TwoExistingNodeSessions() {
		subject.createNewNodeSession(123456, "address1");
		subject.createNewNodeSession(1, "address2");
		assertEquals("address1", subject.getIpAddressForSessionKey("310d7fe77650b4b2b33845f88eaa3ebc"));
		assertEquals("address2", subject.getIpAddressForSessionKey("74ccc85b9a305713da923f7ecf2e1adb"));
	}

	@Test
	public void testGetIpAddressForSessionKey_SessionKeyInvalid_OneExistingAppSession() {
		subject.createNewAppSession("user1", "address1");
		assertNull(subject.getIpAddressForSessionKey("sdf2e755a6a7c0221acda26403155867f"));
	}

	@Test
	public void testGetIpAddressForSessionKey_SessionKeyInvalid_TwoExistingAppSessions() {
		subject.createNewAppSession("user1", "address1");
		subject.createNewAppSession("user2", "address2");
		assertNull(subject.getIpAddressForSessionKey("2caf755a6a7c0221acda26403155867f"));
		assertNull(subject.getIpAddressForSessionKey("c9dfs9e58a71d3c3bf37200a4bcccc366"));
	}

	@Test
	public void testGetIpAddressForSessionKey_SessionKeyInvalid_OneExistingNodeSession() {
		subject.createNewNodeSession(123456, "address1");
		assertNull(subject.getIpAddressForSessionKey("310dfwfe77650b4b2b33845f88eaa3ebc"));
	}

	@Test
	public void testGetIpAddressForSessionKey_SessionKeyInvalid_TwoExistingNodeSessions() {
		subject.createNewNodeSession(123456, "address1");
		subject.createNewNodeSession(1, "address2");
		assertNull("address1", subject.getIpAddressForSessionKey("310d7fawdfdf7650b4b2b33845f88eaa3ebc"));
		assertNull(subject.getIpAddressForSessionKey("74ccc85b9awf5713da923f7ecf2e1adb"));
	}
	
	/*
	 * getNodeIdForSessionKey unit tests
	 */

	@Test(expected=NullPointerException.class)
	public void testGetNodeIdForSessionKey_NullSessionKey() {
		subject.getNodeIdForSessionKey(null);
	}

	@Test
	public void testGetNodeIdForSessionKey_InvalidSessionKey() {
		assertEquals(-1, subject.getNodeIdForSessionKey("310d7fe77650b4b2b33845f88eaa3ebc"));
	}

	@Test
	public void testGetNodeIdForSessionKey_ValidSessionKey_OneNodeSession() {
		assertEquals(123456, subject.getNodeIdForSessionKey(subject.createNewNodeSession(123456, "address1")));
	}

	@Test
	public void testGetNodeIdForSessionKey_ValidSessionKey_TwoNodeSessions() {
		String key1 = subject.createNewNodeSession(123456, "address1");
		String key2 = subject.createNewNodeSession(223456, "address2");
		assertEquals(123456, subject.getNodeIdForSessionKey(key1));
		assertEquals(223456, subject.getNodeIdForSessionKey(key2));
	}

	@Test
	public void testGetNodeIdForSessionKey_ValidSessionKey_TryToGetFromAppSession() {
		String key1 = subject.createNewAppSession("123456", "address1");
		assertEquals(-1, subject.getNodeIdForSessionKey(key1));
	}
	
	/*
	 * getSessionKeyForNodeId unit tests
	 */

	@Test(expected=NullPointerException.class)
	public void testGetUsernameForSessionKey_NullSessionKey() {
		subject.getUsernameForSessionKey(null);
	}

	@Test
	public void testGetUsernameForSessionKey_InvalidSessionKey() {
		assertNull(subject.getUsernameForSessionKey("310d7fe77650b4b2b33845f88eaa3ebc"));
	}

	@Test
	public void testGetUsernameForSessionKey_ValidSessionKey_OneNodeSession() {
		assertEquals("user1", subject.getUsernameForSessionKey(subject.createNewAppSession("user1", "address1")));
	}

	@Test
	public void testGetUsernameForSessionKey_ValidSessionKey_TwoNodeSessions() {
		String key1 = subject.createNewAppSession("user1", "address1");
		String key2 = subject.createNewAppSession("user2", "address2");
		assertEquals("user1", subject.getUsernameForSessionKey(key1));
		assertEquals("user2", subject.getUsernameForSessionKey(key2));
	}

	@Test
	public void testGetUsernameForSessionKey_ValidSessionKey_TryToGetFromNodeSession() {
		String key1 = subject.createNewNodeSession(123456, "address1");
		assertNull(subject.getUsernameForSessionKey(key1));
	}
	
	/*
	 * getSessionKeyForUsername unit tests
	 */

	@Test(expected=NullPointerException.class)
	public void testGetSessionKeyForUsername_NullUsername() {
		subject.getSessionKeyForUsername(null);
	}

	@Test
	public void testGetSessionKeyForUsername_InvalidUsername() {
		assertNull(subject.getSessionKeyForUsername("user1"));
	}

	@Test
	public void testGetSessionKeyForUsername_ValidUsername() {
		String key = subject.createNewAppSession("user1", "address1");
		assertEquals(key, subject.getSessionKeyForUsername("user1"));
	}
	
//	/*
//	 * getUsernameForSessionKey unit tests
//	 */
//
//	@Test
//	public void testGetUsernameForSessionKey_() {
//		fail("Not yet implemented");
//	}
//	
//	/*
//	 * sessionValid unit tests
//	 */
//
//	@Test
//	public void testSessionValid_() {
//		fail("Not yet implemented");
//	}
		
}
