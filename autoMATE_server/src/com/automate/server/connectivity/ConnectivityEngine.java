package com.automate.server.connectivity;

import com.automate.server.connectivity.ConnectivityWatchdogThread.OnClientTimeoutListener;
import com.automate.server.connectivity.EngineCallback.ClientPingListener;
import com.automate.server.messaging.IMessageManager;

public class ConnectivityEngine implements OnClientTimeoutListener, IConnectivityManager {

	private boolean terminated;
	private boolean running;
	private long interval;
	private int timeout;
	private ConnectivityWatchdogThread watchdogThread;
	private EngineCallback callback;
	private Thread executionThread;
	private final Object loopLock = new Object();
	private IMessageManager messageManager;

	/**
	 * Creates a new Connectivity engine.
	 * @param pingInterval
	 * @param callback
	 */
	public ConnectivityEngine(int pingIntervalSeconds, int timeout, EngineCallback callback) {
		if(timeout < 1) {
			throw new IllegalArgumentException("Minimum timeout is 1 second.");
		}
		if(pingIntervalSeconds < 5) {
			throw new IllegalArgumentException("Minimum Ping interval is 5 seconds.");
		}
		if(timeout >= pingIntervalSeconds) {
			throw new IllegalArgumentException("Timeout must be smaller than ping interval");
		}
		if(callback == null) {
			throw new NullPointerException("ConnectivityEngine callback was null.");
		}
		this.interval = pingIntervalSeconds;
		this.callback = callback;
		this.timeout = timeout;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.automate.server.IManager#start()
	 */
	public void start() throws IllegalStateException {
		if(terminated) {
			throw new IllegalStateException("Connectivity engine cannot be restarted after it has been shut down.");
		}
		if(running) {
			throw new IllegalStateException("Connectivity engine already running.");
		}
		synchronized (loopLock) {
			running = true;
		}
		executionThread.start();
	}

	void engineMainLoop() {
		synchronized (loopLock) { 					// lock the engine
			while(!terminated) {
				long startTime = System.currentTimeMillis();
				loopDelegate();
				long endTime = System.currentTimeMillis();
				long pingTime = endTime - startTime;
				try {
					long waitTime = Math.max(1, (interval * 1000) - pingTime);
					loopLock.wait(waitTime);
				} catch (InterruptedException e) {} // release the engine lock and wait for the next ping interval.	
			}
		}
	}

	void loopDelegate() {
		callback.pingAllClients(new ClientPingListener() { // tell the callback to ping all the clients
			@Override
			public void clientPinged(String id) {
				watchdogThread.setTimeout(id, timeout);
			}
		}, messageManager);
	}

	/**
	 * Tell the engine that an ack ping has been received.
	 * @param uid - the uid of the client that has acknowledged the ping
	 * @return 
	 * @return true if the engine is waiting for an ack ping from this client.
	 */
	@Override
	public boolean handleClientPing(String sessionKey) {
		watchdogThread.cancelTimeout(sessionKey);
		return callback.clientPingReceived(sessionKey);
	}

	/*
	 * (non-Javadoc)
	 * @see com.automate.server.IManager#terminate()
	 */
	@Override
	public void terminate() {
		synchronized (loopLock) {
			if(!terminated) {
				terminated = true;
				running = false;
				loopLock.notify();
				this.callback = null;
			}
		}
	}

	/**
	 * @return the callback
	 */
	public EngineCallback getCallback() {
		return callback;
	}

	@Override
	public void onClientTimeout(String client) {
		callback.connectionLost(client);
	}

	@Override
	public void initialize() {
		this.watchdogThread = new ConnectivityWatchdogThread(this);
		executionThread = new Thread(new Runnable() {
			@Override
			public void run() {
				engineMainLoop();
			}
		}, "Connectivity Engine");
	}
	
	@Override
	public void setMessageManager(IMessageManager messageManager) {
		this.messageManager = messageManager;
	}
}
