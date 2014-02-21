package com.automate.server.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import javax.xml.parsers.ParserConfigurationException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.automate.protocol.IIncomingMessageParser;
import com.automate.protocol.Message;
import com.automate.protocol.MessageFormatException;
import com.automate.protocol.Message.MessageType;
import com.automate.protocol.client.ClientProtocolParameters;
import com.automate.protocol.client.messages.ClientPingMessage;
import com.automate.protocol.server.ServerProtocolParameters;
import com.automate.protocol.server.messages.ServerPingMessage;
import com.automate.server.connectivity.IConnectivityManager;
import com.automate.server.messaging.ISocket.Factory;
import com.automate.server.messaging.handlers.IMessageHandler;
import com.automate.server.security.ISecurityManager;
import com.automate.util.xml.XmlFormatException;

public class MessageManagerTest {

	private MessageManager subject;
	private ISecurityManager securityManager;
	private IConnectivityManager connectivityManager;
	private ExecutorService packetSendThreadpool;
	private IPacketReceiveThread receiveThread;
	private IIncomingMessageParser<ClientProtocolParameters> parser;
	private HashMap<MessageType, IMessageHandler<? extends Message<ClientProtocolParameters>, ?>> handlers;
	private ISocket.Factory socketFactory;
	private Mockery context;
	private ISocket socket;
	
	@Before
	public void setUp() throws Exception {
		context = new Mockery();
		securityManager = context.mock(ISecurityManager.class);
		connectivityManager = context.mock(IConnectivityManager.class);
		packetSendThreadpool = context.mock(ExecutorService.class);
		receiveThread = context.mock(IPacketReceiveThread.class);
		parser = context.mock(IIncomingMessageParser.class);
		handlers = new HashMap<Message.MessageType, IMessageHandler<? extends Message<ClientProtocolParameters>,?>>();
		socketFactory = context.mock(Factory.class);
		socket = context.mock(ISocket.class);
		subject = new MessageManager(securityManager, connectivityManager, receiveThread, packetSendThreadpool, parser, handlers, 
				socketFactory, 2, 5);
	}

	@Test(expected=NullPointerException.class)
	public void testHandleInput_NullReader() {
		subject.handleInput(null, "address");
	}

	@Test(expected=NullPointerException.class)
	public void testHandleInput_NullAddress() {
		subject.handleInput(new BufferedReader(new StringReader("")), null);
	}

	@Test
	public void testHandleInput_EmptyInput() {
		subject.handleInput(new BufferedReader(new StringReader("")), "address");
		context.assertIsSatisfied();
	}

	@Test
	public void testHandleInput_NullMessage() throws XmlFormatException, IOException, MessageFormatException, SAXException, ParserConfigurationException {
		context.checking(new Expectations() {
			{
				oneOf(parser).parse("message"); will(returnValue(null));
			}
		});
		subject.handleInput(new BufferedReader(new StringReader("message")), "address");
		context.assertIsSatisfied();
	}

	@Test
	public void testHandleInput_NonNullMessage_NoResponse() throws XmlFormatException, IOException, MessageFormatException, SAXException, ParserConfigurationException {
		final IMessageHandler<Message<ClientProtocolParameters>, Void> handler = context.mock(IMessageHandler.class);
		final ClientProtocolParameters parameters = new ClientProtocolParameters(1, 0, "session");
		final ClientPingMessage message = new ClientPingMessage(parameters);
		context.checking(new Expectations() {
			{
				oneOf(parser).parse("message"); will(returnValue(message));
				oneOf(securityManager).validateParameters(parameters); will(returnValue(true));
				oneOf(handler).handleMessage(2, 5, true, message, null); will(returnValue(null));
			}
		});
		handlers.put(MessageType.PING, handler);
		subject.handleInput(new BufferedReader(new StringReader("message")), "address");
		context.assertIsSatisfied();
	}

	@Test
	public void testHandleInput_NonNullMessage_ProducesResponse_ClientOffline() throws XmlFormatException, IOException, MessageFormatException, SAXException, ParserConfigurationException {
		final IMessageHandler<Message<ClientProtocolParameters>, Void> handler = context.mock(IMessageHandler.class);
		final ClientProtocolParameters parameters = new ClientProtocolParameters(1, 0, "session");
		final ClientPingMessage message = new ClientPingMessage(parameters);
		final ServerProtocolParameters serverParamters = new ServerProtocolParameters(1, 0, true, "session");
		final ServerPingMessage responseMessage = new ServerPingMessage(serverParamters);
		context.checking(new Expectations() {
			{
				oneOf(parser).parse("message"); will(returnValue(message));
				oneOf(securityManager).validateParameters(parameters); will(returnValue(true));
				oneOf(handler).handleMessage(2, 5, true, message, null); will(returnValue(responseMessage));
				oneOf(securityManager).getIpAddress("session"); will(returnValue(null));
			}
		});
		handlers.put(MessageType.PING, handler);
		subject.handleInput(new BufferedReader(new StringReader("message")), "address");
		context.assertIsSatisfied();
	}

	@Test
	public void testHandleInput_NonNullMessage_ProducesResponse_ClientOnline() throws XmlFormatException, IOException, MessageFormatException, SAXException, ParserConfigurationException {
		final IMessageHandler<Message<ClientProtocolParameters>, Void> handler = context.mock(IMessageHandler.class);
		final ClientProtocolParameters parameters = new ClientProtocolParameters(1, 0, "session");
		final ClientPingMessage message = new ClientPingMessage(parameters);
		final ServerProtocolParameters serverParamters = new ServerProtocolParameters(1, 0, true, "session");
		final ServerPingMessage responseMessage = new ServerPingMessage(serverParamters);
		context.checking(new Expectations() {
			{
				oneOf(parser).parse("message"); will(returnValue(message));
				oneOf(securityManager).validateParameters(parameters); will(returnValue(true));
				oneOf(handler).handleMessage(2, 5, true, message, null); will(returnValue(responseMessage));
				oneOf(securityManager).getIpAddress("session"); will(returnValue("address"));
				oneOf(socketFactory).newInstance("address", 6300); will(returnValue(socket));
				oneOf(packetSendThreadpool).submit(with(aNonNull(Runnable.class)));
			}
		});
		handlers.put(MessageType.PING, handler);
		subject.handleInput(new BufferedReader(new StringReader("message")), "address");
		context.assertIsSatisfied();
	}
	
	@Test(expected=NullPointerException.class)
	public void testSendMessage_NullMessage() {
		subject.sendMessage(null);
	}
	
	@Test
	public void testSendMessage_NonNullMessage_ClientOffline() {
		final ServerProtocolParameters serverParamters = new ServerProtocolParameters(1, 0, true, "session");
		final ServerPingMessage responseMessage = new ServerPingMessage(serverParamters);
		context.checking(new Expectations() {
			{
				oneOf(securityManager).getIpAddress("session"); will(returnValue(null));
			}
		});
		subject.sendMessage(responseMessage);
		context.assertIsSatisfied();
	}
	
	@Test
	public void testSendMessage_NonNullMessage_ClientOnline() {
		final ServerProtocolParameters serverParamters = new ServerProtocolParameters(1, 0, true, "session");
		final ServerPingMessage responseMessage = new ServerPingMessage(serverParamters);
		context.checking(new Expectations() {
			{
				oneOf(securityManager).getIpAddress("session"); will(returnValue("address"));
				oneOf(socketFactory).newInstance("address", 6300); will(returnValue(socket));
				oneOf(packetSendThreadpool).submit(with(aNonNull(Runnable.class)));
			}
		});
		subject.sendMessage(responseMessage);
		context.assertIsSatisfied();
	}
	
}