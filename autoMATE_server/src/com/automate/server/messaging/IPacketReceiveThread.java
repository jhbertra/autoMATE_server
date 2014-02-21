package com.automate.server.messaging;

public interface IPacketReceiveThread {

	public abstract void setManager(IMessageManager manager);

	public abstract void cancel();

	public abstract void start();

}