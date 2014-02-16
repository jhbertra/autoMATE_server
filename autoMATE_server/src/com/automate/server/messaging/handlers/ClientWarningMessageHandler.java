package com.automate.server.messaging.handlers;

import java.util.HashMap;

import com.automate.protocol.Message;
import com.automate.protocol.client.messages.ClientWarningMessage;
import com.automate.protocol.server.ServerProtocolParameters;

public class ClientWarningMessageHandler implements IMessageHandler<ClientWarningMessage, Void> {

	private final HashMap<Long, String> pendingWarnings;
	
	public ClientWarningMessageHandler(HashMap<Long, String> pendingWarnings) {
		super();
		this.pendingWarnings = pendingWarnings;
	}

	@Override
	public Message<ServerProtocolParameters> handleMessage(int majorVersion,
			int minorVersion, boolean sessionValid, ClientWarningMessage message,
			Void params) {
		synchronized (pendingWarnings) {
			pendingWarnings.remove(message.warningId);
		}
		return null;
	}

}
