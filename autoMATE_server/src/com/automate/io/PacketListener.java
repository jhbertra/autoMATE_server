package com.automate.io;

import java.io.OutputStream;

public interface PacketListener {

	public void packetRecieved(String xml, OutputStream responseStream);
	
}
