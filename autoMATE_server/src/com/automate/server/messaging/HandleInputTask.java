package com.automate.server.messaging;

import java.net.Socket;

public class HandleInputTask implements Runnable {
	
	private IMessageManager manager;
	private Socket socket;
	
	public HandleInputTask(IMessageManager manager, Socket socket) {
		this.manager = manager;
		this.socket = socket;
	}

	@Override
	public void run() {
		manager.handleInput(socket);
	}

}
