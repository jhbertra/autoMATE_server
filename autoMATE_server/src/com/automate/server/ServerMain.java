package com.automate.server;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.automate.server.commandLine.CommandLineInput;
import com.automate.server.commandLine.GrammarFile;
import com.automate.util.xml.XmlFormatException;

public class ServerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AutomateServer server = new AutomateServer();
		// start the server
		server.initSubsystems();
		server.start();
		
		// start the command line input
		try {
			CommandLineInput input = new CommandLineInput(server.getApi());
			input.loadGrammar(GrammarFile.Factory.loadFile("res/command_line_grammar.xml"));
			input.start();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlFormatException e) {
			e.printStackTrace();
		}
		
		server.shutdown();
	}

}
