package com.automate.server;

import java.util.concurrent.ExecutorService;

import com.automate.server.connectivity.EngineCallback;

public class ConnectivityEngineParameters {

	public final ExecutorService executorService;
	public final EngineCallback callback;
	public final int timeout;
	public final int pingInterval;
	
	public ConnectivityEngineParameters(ExecutorService executorService, EngineCallback callback, int timeout, int pingInterval) {
		this.executorService = executorService;
		this.callback = callback;
		this.timeout = timeout;
		this.pingInterval = pingInterval;
	}
	
}
