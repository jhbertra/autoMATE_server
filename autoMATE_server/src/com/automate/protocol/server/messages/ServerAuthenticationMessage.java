package com.automate.protocol.server.messages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.events.Characters;

import com.automate.protocol.Message;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.util.xml.Attribute;

/**
 * Represents an authentication message from the server
 * @author jamie.bertram
 *
 */
public class ServerAuthenticationMessage extends Message <ServerProtocolParameters> {

	/**
	 * The username for which authentication was requested.
	 */
	public final String username;
	
	/**
	 * The numeric response code.  One of:
	 * 
	 * 200 (OK)
	 * 400 (DENIED)
	 * 500 (INTERNAL SERVER ERROR)
	 */
	public final int responseCode;
	
	/**
	 * The corresponding response message.  May be null or empty.
	 */
	public final String response;
	
	/**
	 * The newly created session key (only is responseCode is 200).
	 */
	public final String sessionKey;
	
	/**
	 * Creates a new {@link ServerAuthenticationMessage}
	 * @param parameters the protocol parameters from the server
	 * @param username the username
	 * @param responseCode the numeric response code
	 * @param response the (optional) response messsage.
	 * @param sessionKey the key for the new session created (if authentication succeeded).
	 * 
	 * @throws NullPointerException if username is null.
	 * @throws IllegalArgumentException if username is less than six {@link Characters} long, or has 
	 * characters other than alphanumeric character, hyphens, underscores, or periods.
	 * @throws IllegalArgumentException if response code is 200 and sessionKey is empty or null.
	 * @throws IllegalArgumentException if responseCode is 400 or 500 and sessionKey is not null or empty
	 * @throws IllegalArgumentException if responseCode is not 200, 400, or 500
	 */
	public ServerAuthenticationMessage(ServerProtocolParameters parameters,
			String username, int responseCode, String response, String sessionKey) {
		super(parameters);
		if(username == null) {
			throw new NullPointerException("username null in ServerAuthenticationMesssage.");
		}
		Matcher userMatcher = Pattern.compile("[\\w-\\.]{6,}").matcher(username);
		if(!userMatcher.matches()) {
			throw new IllegalArgumentException(username + " is not a valid username.");
		}
		if(responseCode == 200) {
			if(sessionKey == null || sessionKey.isEmpty()) {
				throw new IllegalArgumentException("response was OK, but session key was null or empty.");
			}
		} else if (responseCode == 400 || responseCode == 500) {
			if(sessionKey != null && ! sessionKey.isEmpty()) {
				throw new IllegalArgumentException("response was " + responseCode + " but session key was not blank.");
			}
		} else {
			throw new IllegalArgumentException("Invalid responseCode " + responseCode);
		}
		this.username = username;
		this.responseCode = responseCode;
		this.response = response;
		this.sessionKey = sessionKey;
	}

	@Override
	protected void addContent() {
		if(sessionKey != null) {
			addElement("authentication", true, 	new Attribute("username", username), 
												new Attribute("response", responseCode + (response != null ? (" " + response) : "")),
												new Attribute("session-key", sessionKey));
		} else {
			addElement("authentication", true, 	new Attribute("username", username), 
												new Attribute("response", responseCode + (response != null ? (" " + response) : "")));
		}
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
			return 	this.username.equals(((ServerAuthenticationMessage)obj).username)
					&& this.responseCode == ((ServerAuthenticationMessage)obj).responseCode
					&& (this.response == null ?
						((ServerAuthenticationMessage)obj).response == null
						: this.response.equals(((ServerAuthenticationMessage)obj).response))
					&& this.sessionKey.equals(((ServerAuthenticationMessage)obj).sessionKey);
		} else return false;
	}

	@Override
	public String toString() {
		return super.toString() + "\nServerAuthenticationMessage:\nusername: " + username + "\nresponseCode: " + responseCode
				+ "\nresponse: " + response + "\nsessionKey: " + sessionKey;
	}
	
}
