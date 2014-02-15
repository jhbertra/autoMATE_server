package com.automate.protocol;

public abstract class MessageHandler<M extends Message<? extends ProtocolParameters>> {

	protected ProtocolManager manager;
	
	public MessageHandler(ProtocolManager manager) {
		this.manager = manager;
	}
	
	public abstract void handleMessage(M message) throws MessageHandlingException;
	
}
