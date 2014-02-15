package com.automate.protocol;

public class MessageHandlingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2589993565827512949L;

	public MessageHandlingException() {
		super();
	}

	public MessageHandlingException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public MessageHandlingException(String detailMessage) {
		super(detailMessage);
	}

	public MessageHandlingException(Throwable throwable) {
		super(throwable);
	}
	
}
