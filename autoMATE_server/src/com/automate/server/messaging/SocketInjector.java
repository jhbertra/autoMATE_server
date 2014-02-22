package com.automate.server.messaging;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketInjector implements ISocket {

	private Socket socket;
	private String host;
	private int port;
	
	public SocketInjector(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}

	@Override
	public void connect() throws UnknownHostException, IOException {
		socket = new Socket(host, port);
	}

}
