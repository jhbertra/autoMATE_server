package com.automate.protocol;

import com.automate.util.xml.XmlConvertible;
import com.automate.util.xml.XmlFormatException;

public abstract class Message <P extends ProtocolParameters> extends XmlConvertible {

	private P parameters;
	
	public static enum MessageType {
		AUTHENTICATION,
		NODE_LIST,
		COMMAND,
		PING,
		STATUS_UPDATE,
		WARNING;

		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			switch(this.ordinal()) {
			case 0:
				return Message.AUTHENTICATION;
			case 1:
				return Message.NODE_LIST;
			case 2:
				return Message.COMMAND;
			case 3:
				return Message.PING;
			case 4:
				return Message.STATUS_UPDATE;
			case 5:
				return Message.WARNING;
				default:
					return null;
			}
		}
		
	}
	
	public static final String AUTHENTICATION = "authentication";
	public static final String NODE_LIST = "node-list";
	public static final String COMMAND = "command";
	public static final String PING = "ping";
	public static final String STATUS_UPDATE = "status-update";
	public static final String WARNING = "warning";

	public static final int NUM_MESSAGE_TYPES = 6;
	
	public Message(P parameters) {
		this.parameters = parameters;
	}

	/* (non-Javadoc)
	 * @see com.automate.util.xml.XmlConvertible#constructXml(java.lang.StringBuilder, int)
	 */
	@Override
	protected void constructXml(StringBuilder builder, int indentationLevel)
			throws XmlFormatException {
		builder.append("content-type:");
		builder.append(getMessageType().toString());
		builder.append('\n');
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		addElement("message", false);
		
		parameters.toXml(builder, this.indentationLevel);
		
		addElement("content", false);
		
		addContent();
		
		closeElement(); // content
		
		closeElement(); // message
	}
	
	protected abstract void addContent() throws XmlFormatException;

	/**
	 * @return the parameters
	 */
	public P getParameters() {
		return parameters;
	}
	
	public abstract MessageType getMessageType();

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj.getClass().equals(this.getClass())) {
			return ((Message<?>)obj).parameters.equals(this.parameters);
		} else return false;
	}
	
}
