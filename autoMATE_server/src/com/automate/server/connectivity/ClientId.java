package com.automate.server.connectivity;

public class ClientId {

	public ClientType type;
	public String uid;
	
	public ClientId(ClientType type, String uid) {
		this.type = type;
		this.uid = uid;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return (type == ClientType.APP ? "app:" : "node:") + uid;
	}
	
}