package com.automate.server.messaging.handlers;

import com.automate.protocol.Message;
import com.automate.protocol.client.messages.ClientPingMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.server.connectivity.IConnectivityManager;

public class PingMessageHandler implements IMessageHandler<ClientPingMessage, Void> {

	private IConnectivityManager connectivityManager;
	
	public PingMessageHandler(IConnectivityManager connectivityManager) {
		if(connectivityManager == null) {
			throw new NullPointerException("Ping Message Manager requires a non-null IConnectivityManager.");
		}
		this.connectivityManager = connectivityManager;
	}

	@Override
	public Message<ServerProtocolParameters> handleMessage(int majorVersion,
			int minorVersion, boolean sessionValid, ClientPingMessage message,
			Void params) {
		if(message == null) {
			throw new NullPointerException("message was null.");
		}
		connectivityManager.handleClientPing(message.getParameters().sessionKey);
		return null;
	}

}
