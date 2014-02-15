package com.automate.protocol;

public class MessageFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5985818437779078127L;

	public MessageFormatException() {
		super();
	}

	public MessageFormatException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public MessageFormatException(String detailMessage) {
		super(detailMessage);
	}

	public MessageFormatException(Throwable throwable) {
		super(throwable);
	}

}
