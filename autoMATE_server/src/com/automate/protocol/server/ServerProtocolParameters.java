package com.automate.protocol.server;

import com.automate.protocol.ProtocolParameters;

/**
 * Parameters for messages sent by the server.
 */
public class ServerProtocolParameters extends ProtocolParameters {

	public final boolean sessionValid;
	public final String sessionKey;

	public ServerProtocolParameters(int majorVersion, int minorVersion, boolean sessionValid, String sessionKey) {
		super(majorVersion, minorVersion);
		this.sessionValid = sessionValid;
		this.sessionKey = (sessionKey == null ? "" : sessionKey);
	}

	private void addSessionValidParameter() {
		addParameter("session-valid", String.valueOf(sessionValid));
	}

	@Override
	protected void addSpecializedParameters() {
		addSessionValidParameter();
		addSessionKeyParameter();
	}

	private void addSessionKeyParameter() {
		addParameter("session-key", String.valueOf(sessionKey));
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.ProtocolParameters#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj)) {
			return 	this.sessionValid == ((ServerProtocolParameters)obj).sessionValid
					&& this.sessionKey.equals(((ServerProtocolParameters)obj).sessionKey);
		} else return false;
	}

	@Override
	public String toString() {
		return "serverProtocolParameters:\n" + "session-key: " + sessionKey + "\n" + "session-valid: " + sessionValid + "\n" + super.toString();
	}
	
}
