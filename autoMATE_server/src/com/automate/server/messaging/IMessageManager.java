package com.automate.server.messaging;

import java.io.BufferedReader;
import java.net.Socket;

import com.automate.protocol.Message;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.server.IManager;

/**
 * Manager that handles the passing of protocol messages between clients, nodes, and the server.
 * @author jamie.bertram
 *
 */
public interface IMessageManager extends IManager {

	/**
	 * Send message to the client id'd by clientd.
	 * @param message
	 */
	public void sendMessage(Message<ServerProtocolParameters> message);

	/**
	 * Send message to the client id'd by clientd.  Calls the listener when the message was sent
	 * @param message  the message to be sent
	 * @param listener the listener to be notified when the message is sent
	 */
	public void sendMessage(Message<ServerProtocolParameters> message, MessageSentListener listener);
	
	/**
	 * Handles input from a socket (called from a worker thread).
	 * @param reader the reader from which to read the input.
	 * @param hostAddress the address of the host that sent the message.
	 */
	public void handleInput(BufferedReader reader, Socket socket);
	
	public interface MessageSentListener {
		
		public void messageSent();

		void messageDeliveryFailed(Message<ServerProtocolParameters> message);
		
	}
	
}
