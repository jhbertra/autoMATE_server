package com.automate.server.commandLine.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generic Stateless text scanner.  Tokenizes input strings as defined by a list of rules.
 * @author jamie.bertram
 *
 */
public class Scanner {

	private List<ScannerRule<?>> rules;
	private List<Token<?>> tokens;
	private String input;
	private int tokenCursor;
	
	private ScannerCallback callback;
	
	public Scanner(ScannerCallback callback) {
		rules = new ArrayList<Scanner.ScannerRule<?>>();
		tokens = new ArrayList<Token<?>>();
		this.callback = callback;
	}

	/**
	 * Adds a rule to the Scanner's list of rules.  If a rule already matches the pattern
	 * that <code>rule</code> matches, the previous rule is overwritten.
	 * @param rule - the rule to add.
	 */
	public void addRule(ScannerRule<?> rule) {
		if(rule != null) {
			rules.remove(rule);
			rules.add(rule);
		}
	}
	
	/**
	 * Adds all the rules in <code>rules</code> to this scanner's rules.  Replaces any
	 * existing rules that match any of the patterns matched by the rules in <code>rules</code>.
	 * @param rules the rules to add.
	 */
	public void addRules(List<ScannerRule<?>> rules) {
		if(rules != null) {
			for(ScannerRule<?> rule : rules) {
				addRule(rule);
			}
		}
	}
	
	/**
	 * Removes all rules.
	 */
	public void clearRules() {
		this.rules.clear();
	}
	
	/**
	 * Clears the input string and the token queue.
	 */
	public void clearInput() {
		this.input = null;
		this.tokens.clear();
		this.tokenCursor = -1;
	}

	public int numRules() {
		return rules.size();
	}

	public int tokenCount() {
		return tokens.size();
	}

	public String getInput() {
		return input;
	}
	
	/**
	 * Scans the input and genreates a token list.
	 * @param input the input to scan.
	 */
	public void setInput(String input) {
		clearInput();
		if(input != null) {
			this.input = input;
			scan();
			tokenCursor = 0;
		}
	}
	
	/*
	 * Scans the input and constructs the tokens list.
	 */
	private void scan() {
		int inputCursor = 0;
		StringBuilder errorBuffer = new StringBuilder();
		while(inputCursor < input.length()) {
			String matchedString = null;
			ScannerRule<?> matchingRule = null;
			for(ScannerRule<?> rule : rules) {
				String match;
				if((match = rule.match(input.substring(inputCursor))) != null) {
					if(matchedString == null || match.length() > matchedString.length()) {
						matchedString = match;
						matchingRule = rule;
					}
				}
			}
			if(matchedString == null) {
				errorBuffer.append(input.charAt(inputCursor));
				++inputCursor;
			} else {
				tokens.add(matchingRule.getToken(matchedString, inputCursor));
				inputCursor += matchedString.length();
				reportError(errorBuffer);
				errorBuffer = new StringBuilder();
			}
		}
		reportError(errorBuffer);
	}

	/*
	 * Reports that the substring couldn't be matched by any rules.
	 */
	private void reportError(StringBuilder errorBuffer) {
		if(callback != null && errorBuffer.length() > 0) {
			callback.error(errorBuffer.toString());
		}
	}

	/**
	 * Pops the next token off the top of the list.  Retrieves the current 
	 * token and advances the cursor to the next token.
	 * @return the next token in the list.
	 */
	public Token<?> lex() {
		if(tokenCursor == -1 || tokenCursor >= tokens.size()) {
			return null;
		}
		Token<?> token = tokens.get(tokenCursor);
		++tokenCursor;
		return token;
	}
	
	/**
	 * Pushes the last popped token back on to the top of the list.
	 * Moves the cursor back one position.
	 */
	public void unlex() {
		if(tokenCursor < 1) return;
		--tokenCursor;
	}
	
	/**
	 * Retrieves the next token off the top of the list without advancing
	 * the cursor.
	 * @return the next token in the list.
	 */
	public Token<?> peek() {
		if(tokenCursor == -1 || tokenCursor >= tokens.size()) {
			return null;
		}
		return tokens.get(tokenCursor);
	}
	
	/**
	 * Class that defines a rule for the scanner.
	 * @author jamie.bertram
	 *
	 * @param <T> the type of data encapsulated by the token matched.
	 */
	public abstract static class ScannerRule<T> {
		
		/**
		 * The regular expression that this rule matches.
		 */
		public final String regex;
		private Pattern pattern;
		
		/**
		 * Constructs a new Scanner rule that matches a regular expression.
		 * @param regex the regular expression to match.
		 */
		public ScannerRule(String regex) {
			this.regex = regex;
			this.pattern = Pattern.compile(regex);
		}
		
		/**
		 * If input begins with a substring matched by this rule,
		 * returns the substring matched.  Otherwise it returns null.
		 * @param input the string to match.
		 * @return the substring at the start of input matched by this rule.  
		 * null if no match was found.
		 */
		public String match(String input) {
			Matcher matcher = pattern.matcher(input);
			if(matcher.lookingAt()) {
				return matcher.group();
			} else {
				return null;
			}
		}
		
		/**
		 * Constructs a token encapsulating the matched string.
		 * @param match - the matched string.
		 * @return a token that encapsulates the matched string.
		 */
		public abstract Token<T> getToken(String match, int position);

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof ScannerRule) {
				return this.regex.equals(((ScannerRule<?>) obj).regex);
			} else {
				return false;
			}
		}
		
	}
	
	/**
	 * Callback for the scanner.
	 * @author jamie.bertram
	 *
	 */
	public interface ScannerCallback {
		
		/**
		 * Called when a sequence of characters could not be matched by any rule.
		 * @param string the string that the rules could not match.
		 */
		public void error(String string);
		
	}

	public ScannerCallback getCallback() {
		return this.callback;
	}
	
}
