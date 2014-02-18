package com.automate.protocol.client.messages;

import static org.junit.Assert.*;

import org.junit.Test;

import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.util.xml.XmlFormatException;

public class ClientAuthenticationMessageTest {

	private ClientAuthenticationMessage subject;

	private ClientProtocolParameters parameters = new ClientProtocolParameters(0, 0, "");
	
	@Test(expected=NullPointerException.class)
	public void testNullArgs() {
		subject = new ClientAuthenticationMessage(parameters, null, null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullUsername() {
		subject = new ClientAuthenticationMessage(parameters, null, "password");
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullPassword() {
		subject = new ClientAuthenticationMessage(parameters, "username", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidUsername_TooShort() {
		subject = new ClientAuthenticationMessage(parameters, "user", "pas5word");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidUsername_IllegalCharacters() {
		subject = new ClientAuthenticationMessage(parameters, "username,5", "pas5word");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidPassword_TooShort() {
		subject = new ClientAuthenticationMessage(parameters, "username", "pas5");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidPassword_NoNumber() {
		subject = new ClientAuthenticationMessage(parameters, "username", "password");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidPassword_NoLetter() {
		subject = new ClientAuthenticationMessage(parameters, "username", "142798.234");
	}
	
	@Test
	public void testToXml() {
		subject = new ClientAuthenticationMessage(parameters, "username", "pas5word");
		StringBuilder builder = new StringBuilder();
		try {
			subject.toXml(builder, 0);
		} catch (XmlFormatException e) {
			fail(e.getMessage());
		}
		String expected = 	"content-type:authentication\n" +
							"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
							"<message >\n" +
							"\t<parameters >\n" +
							"\t\t<parameter name=\"version\" value=\"0.0\" />\n" +
							"\t\t<parameter name=\"session-key\" value=\"\" />\n" +
							"\t</parameters>\n" +
							"\t<content >\n" +
							"\t\t<authentication username=\"username\" password=\"pas5word\" />\n" +
							"\t</content>\n" +
							"</message>\n";
		String actual = builder.toString();
		assertEquals(expected, actual);
	}

}
