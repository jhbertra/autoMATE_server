package com.automate.server;

import java.util.HashMap;

import com.automate.protocol.Message;
import com.automate.protocol.Message.MessageType;
import com.automate.protocol.MessageSubParser;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.server.connectivity.IConnectivityManager;
import com.automate.server.messaging.handlers.IMessageHandler;
import com.automate.server.security.ISecurityManager;

public class MessageManagerParameters {

	public final int numReceiveThreads;
	public final int numSendThreads;
	public final HashMap<String, MessageSubParser<Message<ClientProtocolParameters>, ClientProtocolParameters>> subParsers;
	public final ISecurityManager securityManager;
	public final IConnectivityManager connectivityManager;
	public final HashMap<MessageType, IMessageHandler<? extends Message<ClientProtocolParameters>, ?>> handlers;
	public final int minorVersion;
	public final int majorVersion;
	
	public MessageManagerParameters(
			int numReceiveThreads,
			int numSendThreads,
			HashMap<String, MessageSubParser<Message<ClientProtocolParameters>, ClientProtocolParameters>> subParsers,
			ISecurityManager securityManager,
			IConnectivityManager connectivityManager,
			HashMap<MessageType, IMessageHandler<? extends Message<ClientProtocolParameters>, ?>> handlers,
			int minorVersion, int majorVersion) {
		this.numReceiveThreads = numReceiveThreads;
		this.numSendThreads = numSendThreads;
		this.subParsers = subParsers;
		this.securityManager = securityManager;
		this.connectivityManager = connectivityManager;
		this.handlers = handlers;
		this.minorVersion = minorVersion;
		this.majorVersion = majorVersion;
	}	

}
