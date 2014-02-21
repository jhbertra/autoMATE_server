package com.automate.server;

public interface IManager {

	public void initialize() throws InitializationException;
	public void start() throws Exception;
	public void terminate() throws Exception;
	
}
