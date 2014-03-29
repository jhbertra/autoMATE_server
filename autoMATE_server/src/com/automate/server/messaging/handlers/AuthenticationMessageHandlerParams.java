package com.automate.server.messaging.handlers;

import java.net.Socket;

public class AuthenticationMessageHandlerParams {

	public Socket clientSocket;

	public AuthenticationMessageHandlerParams(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
}
