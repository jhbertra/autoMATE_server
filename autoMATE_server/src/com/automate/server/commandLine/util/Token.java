package com.automate.server.commandLine.util;

/**
 * Record of a token produced by a Scanner
 * @author jamie.bertram
 *
 * @param <T> the type of data that the Token encapsulates.
 */
public class Token<T> {

	public final int tokenType;
	public final String matchedString;
	public final int position;
	public final T data;
	
	public Token(String matchedString, int position, T data, int tokenType) {
		this.matchedString = matchedString;
		this.position = position;
		this.data = data;
		this.tokenType = tokenType;
	}
	
}
