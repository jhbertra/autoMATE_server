package com.automate.io;

import java.io.OutputStream; 
import java.util.ArrayList;

public abstract class PacketReceiver<T extends PacketReadThread> extends Thread implements PacketListener {
	
	private boolean terminated;
	private final Object cancelNotification = new Object();
	private ArrayList<PacketListener>listeners;
	private T packetReadThread;
	
	public PacketReceiver() {
		super("Packet Receiver");
		listeners = new ArrayList<PacketListener>();
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		synchronized(cancelNotification) {
			startReadThread();
			while(!terminated) {
				try {
					cancelNotification.wait();
				} catch (InterruptedException e) {}
			}
		}
	}

	private void startReadThread() {
		packetReadThread = createReaderThread();
		packetReadThread.start();
	}
	
	protected abstract T createReaderThread();

	public boolean terminate() {
		if(terminated) {
			return false;
		}
		synchronized (cancelNotification) {			
			terminated = true;
			cancelNotification.notify();
			packetReadThread.cancel();
			return true;
		}
	}

	@Override
	public void packetRecieved(String xml, OutputStream responseStream) {
		for(PacketListener listener: listeners) {
			listener.packetRecieved(xml, responseStream);
		}
	}
	
	public void addListener(PacketListener listener) {
		if(listener == null) throw new NullPointerException("Cannot add null PacketListener.");
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
}
