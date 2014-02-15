package com.automate.server.commandLine.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.automate.server.commandLine.util.Scanner.ScannerCallback;
import com.automate.server.commandLine.util.Scanner.ScannerRule;

public class ScannerTest {

	private Scanner subject;

	private int errorCalls;
	private ArrayList<String> errorStrings = new ArrayList<String>();
	
	private boolean rule1;
	private boolean rule2;
	
	@Before
	public void setUp() throws Exception {
		subject = new Scanner(new ScannerCallback() {
			@Override
			public void error(String string) {
				++errorCalls;
				errorStrings.add(string);
			}
		});
	}

	@After
	public void tearDown() throws Exception {
		errorCalls = 0;
		errorStrings.clear();
		rule1 = rule2 = false;
	}

	private void assertMockState(int errorCalls, String ... errorStrings) {
		assertEquals(errorCalls, this.errorCalls);
		if(errorStrings == null) return;
		assertEquals(errorStrings.length, this.errorStrings.size());
		for(int i = 0; i < errorStrings.length; ++i) {
			assertEquals(errorStrings[i], this.errorStrings.get(i));
		}
	}

	@Test
	public void testAddRule_NullRule() {
		subject.addRule(null);
		assertTrue(subject.numRules() == 0);
		assertMockState(0, (String[])null);
	}
	
	@Test
	public void testAddRule_OneRule() {
		subject.addRule(new ScannerRule<Void>("abc") {
			@Override
			public Token<Void> getToken(String match, int position) {
				return null;
			}
		});
		assertTrue(subject.numRules() == 1);
		assertMockState(0, (String[])null);
	}

	@Test
	public void testAddRule_TwoRulesNoReplace() {
		subject.addRule(new ScannerRule<Void>("abc") {
			@Override
			public Token<Void> getToken(String match, int position) {
				return null;
			}
		});
		subject.addRule(new ScannerRule<Void>("abb") {
			@Override
			public Token<Void> getToken(String match, int position) {
				return null;
			}
		});
		assertTrue(subject.numRules() == 2);
		assertMockState(0, (String[])null);
	}

	@Test
	public void testAddRule_TwoRulesReplace() {
		subject.addRule(new ScannerRule<Void>("abc") {
			@Override
			public Token<Void> getToken(String match, int position) {
				return null;
			}
		});
		subject.addRule(new ScannerRule<Void>("abc") {
			@Override
			public Token<Void> getToken(String match, int position) {
				return null;
			}
		});
		assertTrue(subject.numRules() == 1);
		assertMockState(0, (String[])null);
	}

	@Test
	public void testAddRules_TwoRulesNoReplace() {
		List<ScannerRule<?>> rules = new ArrayList<Scanner.ScannerRule<?>>();
		rules.add(new ScannerRule<Void>("abc") {
			@Override
			public Token<Void> getToken(String match, int position) {
				return null;
			}
		});
		rules.add(new ScannerRule<Void>("abb") {
			@Override
			public Token<Void> getToken(String match, int position) {
				return null;
			}
		});
		subject.addRules(rules);
		assertTrue(subject.numRules() == 2);
		assertMockState(0, (String[])null);
	}
	
	@Test
	public void testSetInput_Null() {
		subject.setInput(null);
		assertEquals(0, subject.tokenCount());
		assertEquals(null, subject.getInput());
		assertMockState(0, (String[])null);
	}
	
	@Test
	public void testSetInput_NoRules() {
		subject.setInput("Hello, world!");
		assertEquals(0, subject.tokenCount());
		assertEquals("Hello, world!", subject.getInput());
		assertMockState(1, "Hello, world!");
	}
	
	@Test
	public void testSetInput_MatchesOneRule() {
		subject.addRule(new ScannerRule<Void>("Hello, world!") {
			@Override
			public Token<Void> getToken(String match, int position) {
				return new Token<Void>(match, position, null, position);
			}			
		});
		subject.setInput("Hello, world!");
		assertEquals(1, subject.tokenCount());
		assertMockState(0, (String[])null);
	}
	
	@Test
	public void testSetInput_PartiallyMatchesOneRule() {
		subject.addRule(new ScannerRule<Void>("Hello") {
			@Override
			public Token<Void> getToken(String match, int position) {
				return new Token<Void>(match, position, null, position);
			}			
		});
		subject.setInput("Hello, world!");
		assertEquals(1, subject.tokenCount());
		assertMockState(1, ", world!");
	}
	
	@Test
	public void testSetInput_FullyMatchesTwoRules() {
		subject.addRule(new ScannerRule<Void>("Hello") {
			@Override
			public Token<Void> getToken(String match, int position) {
				return new Token<Void>(match, position, null, position);
			}			
		});
		subject.addRule(new ScannerRule<Void>(", [a-z]+!") {
			@Override
			public Token<Void> getToken(String match, int position) {
				return new Token<Void>(match, position, null, position);
			}			
		});
		subject.setInput("Hello, world!");
		assertEquals(2, subject.tokenCount());
		assertMockState(0, (String[])null);
	}
	
	@Test
	public void testSetInput_PartiallyMatchesTwoRules() {
		subject.addRule(new ScannerRule<Void>("Hello") {
			@Override
			public Token<Void> getToken(String match, int position) {
				return new Token<Void>(match, position, null, position);
			}			
		});
		subject.addRule(new ScannerRule<Void>("[a-z]+!") {
			@Override
			public Token<Void> getToken(String match, int position) {
				return new Token<Void>(match, position, null, position);
			}			
		});
		subject.setInput("Hello, world!");
		assertEquals(2, subject.tokenCount());
		assertMockState(1, ", ");
	}

	
	@Test
	public void testSetInput_PartiallyMatchesTwoRulesWithTwoErrors() {
		subject.addRule(new ScannerRule<Void>("Hello") {
			@Override
			public Token<Void> getToken(String match, int position) {
				return new Token<Void>(match, position, null, position);
			}			
		});
		subject.addRule(new ScannerRule<Void>(" [a-z]+") {
			@Override
			public Token<Void> getToken(String match, int position) {
				return new Token<Void>(match, position, null, position);
			}			
		});
		subject.setInput("Hello, world!");
		assertEquals(2, subject.tokenCount());
		assertMockState(2, ",", "!");
	}
	
	@Test
	public void testSetInput_TwoConflictingRulesLongerRuleWins() {
		subject.addRule(new ScannerRule<Void>("Hell") {
			@Override
			public Token<Void> getToken(String match, int position) {
				rule1 = true;
				return new Token<Void>(match, position, null, position);
			}			
		});
		subject.addRule(new ScannerRule<Void>("Hello") {
			@Override
			public Token<Void> getToken(String match, int position) {
				rule2 = true;
				return new Token<Void>(match, position, null, position);
			}			
		});
		subject.setInput("Hello");
		assertEquals(1, subject.tokenCount());
		assertFalse(rule1);
		assertTrue(rule2);
		assertMockState(0, (String[])null);
	}
	
	@Test
	public void testSetInput_TwoConflictingRulesEqualLengthFirstRuleWins() {
		subject.addRule(new ScannerRule<Void>("Hell") {
			@Override
			public Token<Void> getToken(String match, int position) {
				rule1 = true;
				return new Token<Void>(match, position, null, position);
			}			
		});
		subject.addRule(new ScannerRule<Void>("He(ll)?") {
			@Override
			public Token<Void> getToken(String match, int position) {
				rule2 = true;
				return new Token<Void>(match, position, null, position);
			}			
		});
		subject.setInput("Hello");
		assertEquals(1, subject.tokenCount());
		assertTrue(rule1);
		assertFalse(rule2);
		assertMockState(1, "o");
	}
	
}
