package com.automate.server.connectivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ConnectivityWatchdogThread extends Thread implements IWatchdogThread {

	private HashMap<Long, ArrayList<String>> responseTimeoutToClientId = new HashMap<Long, ArrayList<String>>();
	private HashMap<String, Long> clientIdToTimeout = new HashMap<String, Long>();

	private final Object lock = new Object();

	private long currentSecond;
	private long startTime;

	private boolean cancelled;
	
	private OnClientTimeoutListener listener;
	
	public ConnectivityWatchdogThread(OnClientTimeoutListener listener) {
		this.listener = listener;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	/* (non-Javadoc)
	 * @see com.automate.server.connectivity.IWatchdogThread#run()
	 */
	@Override
	public void run() {
		startTime = System.currentTimeMillis();
		synchronized (lock) {
			while(!cancelled) {
				ArrayList<String> clientList = responseTimeoutToClientId.remove(currentSecond);
				if(clientList != null) {
					for(String client : clientList) {
						listener.onClientTimeout(client);
						clientIdToTimeout.remove(client);
					}
				}
				++currentSecond;
				long nextWakeTime = startTime + currentSecond * 1000;
				long sleepTime = nextWakeTime - System.currentTimeMillis();
				if(sleepTime < 0) {
					System.out.println("ConnectivityWatchdogThread loop took longer than one second.  Resuming immediately.");
				}
				try {
					lock.wait(Math.max(0, sleepTime));
				} catch (InterruptedException e) {}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.automate.server.connectivity.IWatchdogThread#setTimeout(java.lang.String, int)
	 */
	@Override
	public void setTimeout(String client, int timeoutDelaySeconds) {
		long timeout = currentSecond + 1 + timeoutDelaySeconds;
		synchronized(lock) {
			if(clientIdToTimeout.containsKey(client)) {
				return;
			}
			if(!responseTimeoutToClientId.containsKey(timeout)) {
				responseTimeoutToClientId.put(timeout, new ArrayList<String>());
			}
			responseTimeoutToClientId.get(timeout).add(client);
			clientIdToTimeout.put(client, timeout);
		}
	}

	/* (non-Javadoc)
	 * @see com.automate.server.connectivity.IWatchdogThread#cancelTimeout(java.lang.String)
	 */
	@Override
	public boolean cancelTimeout(String sessionKey) {
		synchronized (lock) {
			if(!clientIdToTimeout.containsKey(sessionKey)) return false;
			long timeout = clientIdToTimeout.remove(sessionKey);
			ArrayList<String> clientList = responseTimeoutToClientId.get(timeout); 
			clientList.remove(sessionKey);
			if(clientList.isEmpty()) {
				responseTimeoutToClientId.remove(timeout);
			}
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see com.automate.server.connectivity.IWatchdogThread#cancel()
	 */
	@Override
	public void cancel() {
		synchronized (lock) {
			cancelled = true;
			lock.notify();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.listener = null;
	}

	public interface OnClientTimeoutListener {
		
		public void onClientTimeout(String client);
		
	}

}
