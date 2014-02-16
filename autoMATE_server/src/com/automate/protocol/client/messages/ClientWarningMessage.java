package com.automate.protocol.client.messages;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlFormatException;

public class ClientWarningMessage extends Message<ClientProtocolParameters> {

	public final long warningId;
	
	public ClientWarningMessage(ClientProtocolParameters parameters, long warningId) {
		super(parameters);
		this.warningId = warningId;
	}

	@Override
	protected void addContent() throws XmlFormatException {
		addElement("warning", true, new Attribute("warning-id", String.valueOf(warningId)));
	}

	@Override
	public com.automate.protocol.Message.MessageType getMessageType() {
		return MessageType.WARNING;
	}

	/* (non-Javadoc)
	 * @see com.automate.protocol.Message#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj)) {
			return 	this.warningId == ((ClientWarningMessage)obj).warningId;
		} else return false;
	}

	@Override
	public String toString() {
		return super.toString() + "\nClientWarningMessage:\nwarningId: " + warningId;
	}

}
