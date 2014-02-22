package com.automate.server.messaging;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

public class PacketReceiveThread extends Thread implements IPacketReceiveThread {

	private ExecutorService threadpool;
	private boolean cancelled;
	private IMessageManager manager;
	
	/* (non-Javadoc)
	 * @see com.automate.server.messaging.IPacketReceiveThread#setManager(com.automate.server.messaging.IMessageManager)
	 */
	@Override
	public void setManager(IMessageManager manager) {
		this.manager = manager;
	}

	private ServerSocket serverSocket;
	
	public PacketReceiveThread(ExecutorService threadpool) {
		this.threadpool = threadpool;
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
	
	/* (non-Javadoc)
	 * @see com.automate.server.messaging.IPacketReceiveThread#cancel()
	 */
	@Override
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
