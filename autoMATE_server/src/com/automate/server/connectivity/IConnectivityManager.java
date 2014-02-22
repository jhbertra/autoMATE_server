package com.automate.server.connectivity;

import com.automate.server.IManager;
import com.automate.server.messaging.IMessageManager;

public interface IConnectivityManager extends IManager {

	public boolean handleClientPing(String sessionKey);

	public void setMessageManager(IMessageManager messageManager);

	public void setWatchdogThread(IWatchdogThread watchdogThread);
	
}
