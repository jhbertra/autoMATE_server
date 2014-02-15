package com.automate.server.connectivity;

import com.automate.server.IManager;
import com.automate.server.messaging.IMessageManager;

public interface IConnectivityManager extends IManager {

	boolean handleClientPing(String sessionKey);

	void setMessageManager(IMessageManager messageManager);
	
}
