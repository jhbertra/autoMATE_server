package com.automate.server.connectivity;

public interface IWatchdogThread {

	public abstract void setTimeout(String client, int timeoutDelaySeconds);

	public abstract boolean cancelTimeout(String sessionKey);

	public abstract void cancel();

}