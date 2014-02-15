package com.automate.protocol.client;

import java.util.HashMap;
import java.util.NoSuchElementException;

import com.automate.protocol.IncomingMessageParser;
import com.automate.protocol.Message;
import com.automate.protocol.MessageSubParser;
import com.automate.protocol.client.subParsers.ClientAuthenticationMessageSubParser;
import com.automate.protocol.client.subParsers.ClientMessageSubParser;
import com.automate.protocol.client.subParsers.ClientNodeListMessageSubParser;
import com.automate.protocol.client.subParsers.ClientPingMessageSubParser;

public class IncomingClientMessageParser extends IncomingMessageParser<ClientProtocolParameters> {

	private HashMap<String, ClientMessageSubParser<?>> subParsers;
	
	public IncomingClientMessageParser() {
		subParsers = new HashMap<String, ClientMessageSubParser<?>>(Message.NUM_MESSAGE_TYPES);
		subParsers.put(Message.AUTHENTICATION, new ClientAuthenticationMessageSubParser());
		subParsers.put(Message.NODE_LIST, new ClientNodeListMessageSubParser());
//		subParsers.put(Message.COMMAND, new ClientCommandMessageSubParser());
		subParsers.put(Message.PING, new ClientPingMessageSubParser());
//		subParsers.put(Message.STATUS_UPDATE, new ClientStatusUpdateMessageSubParser());
//		subParsers.put(Message.WARNING, new ClientWarningMessageSubParser());
	}

	@Override
	protected MessageSubParser<Message<ClientProtocolParameters>, ClientProtocolParameters> getSubParser(
			String contentType) throws NoSuchElementException {
		ClientMessageSubParser<?> parser = subParsers.get(contentType);
		if(parser == null) {
			throw new NoSuchElementException();
		}
		return (MessageSubParser<Message<ClientProtocolParameters>, ClientProtocolParameters>) parser;
	}

}
