package com.automate.io;

public class PacketReadThread extends Thread {

	protected PacketListener callback;
	protected boolean cancelled;
	
	public PacketReadThread(PacketListener callback) {
		super("Packet read thread");
		this.callback = callback;
	}

	public void cancel() {
		cancelled = true;
	}
	
}
