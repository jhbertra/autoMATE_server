package com.automate.server.connectivity;

import com.automate.server.messaging.IMessageManager;

/**
 * Interface implemented by aany classes that observe the ConnectivityEngine.
 * @author jamie.bertram
 *
 */
public interface EngineCallback {

	/**
	 * Tells the callback that connection has been lost with a list of clients.
	 * @param client - the uids of the clients.
	 */
	public void connectionLost(String client);
	
	/**
	 * Tells the callback to ping all the clients
	 * @param listener - a listener that is called every time a client is pinged.
	 */
	public int pingAllClients(ClientPingListener listener, IMessageManager messageManager);
	
	
	/**
	 * Tells the callback that the client has sent a ping.
	 * @param client the session key of the client that pinged.
	 * @return 
	 */
	public boolean clientPingReceived(String client);
	
	public static interface ClientPingListener {

		void clientPinged(String id);
		
	}
	
}
