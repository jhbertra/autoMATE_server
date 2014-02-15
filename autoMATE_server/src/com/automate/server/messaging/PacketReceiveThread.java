package com.automate.server.messaging;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

public class PacketReceiveThread extends Thread {

	private ExecutorService threadpool;
	private boolean cancelled;
	private IMessageManager manager;
	private ServerSocket serverSocket;
	
	public PacketReceiveThread(ExecutorService threadpool, IMessageManager manager) {
		this.threadpool = threadpool;
		this.manager = manager;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(6300);
			while(!cancelled) {
				HandleInputTask task = new HandleInputTask(manager, serverSocket.accept());
				threadpool.submit(task);
			}
			serverSocket.close();
		} catch (SocketException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void cancel() {
		this.cancelled = true;
		if(serverSocket != null && !serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(threadpool != null) {
			threadpool.shutdown();
		}
	}
	
}
