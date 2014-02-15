package com.automate.server.messaging.handlers;

import com.automate.protocol.Message;
import com.automate.protocol.client.messages.ClientPingMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.server.connectivity.IConnectivityManager;
import com.automate.server.messaging.IMessageHandler;

public class PingMessageHandler implements IMessageHandler<ClientPingMessage, Void> {

	private IConnectivityManager connectivityManager;
	
	public PingMessageHandler(IConnectivityManager connectivityManager) {
		this.connectivityManager = connectivityManager;
	}

	@Override
	public Message<ServerProtocolParameters> handleMessage(ServerProtocolParameters responseParameters, 
			ClientPingMessage message, Void parameters) {
		connectivityManager.handleClientPing(message.getParameters().sessionKey);
		return null;
	}

}
