package com.automate.server.messaging;

import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;

/**
 * Interface for injecting sockets and mocking them.
 * @author jamie.bertram
 *
 */
public interface ISocket {

	OutputStream getOutputStream() throws IOException;
	void close() throws IOException;
	void connect() throws UnknownHostException, IOException;

	public interface Factory {
		
		public ISocket newInstance(String host, int port);
		
	}
}
