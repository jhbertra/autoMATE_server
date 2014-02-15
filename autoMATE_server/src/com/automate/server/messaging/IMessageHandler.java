package com.automate.server.messaging;

import com.automate.protocol.Message;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.server.ServerProtocolParameters;

/**
 * Delegate for handling incoming messages
 * @author jamie.bertram
 *
 * @param <M> The type of message this delegate handles
 * @param <Params> container class for additional parameters.
 */
public interface IMessageHandler<M extends Message<ClientProtocolParameters>, Params> {

	/**
	 * Handle the message upon receipt.
	 * 
	 * @param responseParameters protocol parameters for constructing a response Message
	 * @param message the message received from the client
	 * @return a response message if response is required by protocol spec.
	 */
	public abstract Message<ServerProtocolParameters> handleMessage(ServerProtocolParameters responseParameters, M message, Params params);
	
}
