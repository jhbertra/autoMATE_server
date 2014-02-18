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
	public void testCreateNewSession_NodeTriesThenUserTries() {
		String expected1 = "310d7fe77650b4b2b33845f88eaa3ebc";
		String expected2 = "1df81579e50db3612dbdf59a1a819b72";
		assertEquals(expected1, subject.createNewNodeSession(123456, "address1"));
		assertEquals(expected2, subject.createNewAppSession("123456", "address2"));		
	}
	
	@Test 
	public void testCreateNewSession_UserTriesThenNodeTries() {
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

//	/*
//	 * getNodeIdForSessionKey unit tests
//	 */
//
//	@Test
//	public void testGetNodeIdForSessionKey_() {
//		fail("Not yet implemented");
//	}
//	
//	/*
//	 * getSessionKeyForNodeId unit tests
//	 */
//
//	@Test
//	public void testGetSessionKeyForNodeId_() {
//		fail("Not yet implemented");
//	}
//	
//	/*
//	 * getSessionKeyForUsername unit tests
//	 */
//
//	@Test
//	public void testGetSessionKeyForUsername_() {
//		fail("Not yet implemented");
//	}
//	
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
