package com.automate.server.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			manager.handleInput(reader, socket.getInetAddress().getHostAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
