package com.automate.server.messaging;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketReceiveThread extends Thread implements IPacketReceiveThread {

	private ExecutorService threadpool;
	private boolean cancelled;
	private IMessageManager manager;
	
	private static final Logger logger = LogManager.getLogger();
	
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
			logger.error("Error in PacketReceiveThread.", e);
		} catch (IOException e) {
			logger.error("Error in PacketReceiveThread.", e);
		} catch (RejectedExecutionException e) {
			logger.warn("Received package not processed.  PacketReceiveThread previously terminated.");
		}
	}
	
	/* (non-Javadoc)
	 * @see com.automate.server.messaging.IPacketReceiveThread#cancel()
	 */
	@Override
	public void cancel() {
		logger.info("Shutting down PacketReceiveThread.");
		this.cancelled = true;
		logger.info("Cancelling trhead.");
		if(serverSocket != null && !serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				logger.error("Error closing socket.", e);
			}
		}
		if(threadpool != null) {
			threadpool.shutdown();
		}
	}
	
}
