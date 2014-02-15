package com.automate.server.messaging.handlers;

import com.automate.protocol.Message;
import com.automate.protocol.client.messages.ClientPingMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.server.connectivity.IConnectivityManager;

public class PingMessageHandler implements IMessageHandler<ClientPingMessage, Void> {

	private IConnectivityManager connectivityManager;
	
	public PingMessageHandler(IConnectivityManager connectivityManager) {
		this.connectivityManager = connectivityManager;
	}

	@Override
	public Message<ServerProtocolParameters> handleMessage(int minorVersion,
			int majorVersion, boolean sessionValid, ClientPingMessage message,
			Void params) {
		connectivityManager.handleClientPing(message.getParameters().sessionKey);
		return null;
	}

}
