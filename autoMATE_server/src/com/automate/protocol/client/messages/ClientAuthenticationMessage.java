package com.automate.protocol.client.messages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.util.xml.Attribute;

/**
 * Message sent my the client to the server for authentication.
 * @author jamie.bertram
 *
 */
public class ClientAuthenticationMessage extends Message <ClientProtocolParameters> {

	/**
	 * The authentication username (user provided)
	 */
	public final String username;
	/**
	 * The authentication password (user provided)
	 */
	public final String password;
	
	/**
	 * Creates a new {@link ClientAuthenticationMessage}
	 * @param parameters the parameters from the message
	 * @param username the username from the message
	 * @param password the password from the message
	 * @throws NullPointerException if username is null
	 * @throws NullPointerException if password is null
	 * @throws IllegalArgumentException if username is not at least 6 alphanumeric/ underscore/ hyphen characters.
	 * @throws IllegalArgumentException if password is not at least 6 characters with at least one letter and one number.
	 */
	public ClientAuthenticationMessage(ClientProtocolParameters parameters, String username, String password) {
		super(parameters);
		if(username == null) {
			throw new NullPointerException("username was null in ClientAuthenticationMessage");
		}
		if(password == null) {
			throw new NullPointerException("password was null in ClientAuthenticationMessage");
		}
		Matcher userMatcher = Pattern.compile("[\\w-\\.]{6,}").matcher(username);
		Matcher passwordMatcher1 = Pattern.compile(".{6,}").matcher(password);
		Matcher passwordMatcher2 = Pattern.compile("[a-zA-Z]").matcher(password);
		Matcher passwordMatcher3 = Pattern.compile("[0-9]").matcher(password);
		if(!userMatcher.matches()) {
			throw new IllegalArgumentException(username + " is not a valid username.");
		} else if(!passwordMatcher1.matches() || !passwordMatcher2.find() || !passwordMatcher3.find()) {
			throw new IllegalArgumentException(password + " is not a valid password.");
		}
		this.username = username;
		this.password = password;
	}

	@Override
	protected void addContent() {
		addElement("authentication", true, new Attribute("username", username), new Attribute("password", password));
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.AUTHENTICATION;
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.Message#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj)) {
			return 	this.username.equals(((ClientAuthenticationMessage)obj).username)
					&& this.password.equals(((ClientAuthenticationMessage)obj).password);
		} else return false;
	}

	@Override
	public String toString() {
		return super.toString() + "\nClientAuthenticationMessage:\nusername: " + username + "\npassword: " + password;
	}

}
