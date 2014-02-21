package com.automate.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.automate.server.commandLine.CommandLineInput;
import com.automate.server.commandLine.GrammarFile;
import com.automate.server.setup.gui.SetupGui;
import com.automate.util.xml.XmlFormatException;

public class ServerMain {

	/**
	 * Usage:
	 * 
	 * 		java AutomateServer.jar -p [properties file]
	 * 
	 * 			-	Reads the server properties from a .properties file
	 * 
	 * 		java AutomageServer.jar -g
	 * 
	 * 			-	Starts the server GUI (not supported yet)
	 * 
	 */
	public static void main(String[] args) {
		if(args.length < 1) {
			showUsage();
		} else if(args[0].equalsIgnoreCase("-p")) {
			if(args.length < 2) {
				showUsage();
			} else {
				try {
					startServerFromPropertiesFile(args[1]);
				} catch (FileNotFoundException e) {
					System.out.println("Error: properties file " + args[1] + " could not be found.");
					System.exit(2);
				} catch (IOException e) {
					System.out.println("Error: error reading properties file " + args[1]);
					System.exit(3);
				}
			}
		} else if(args[0].equalsIgnoreCase("-g")) {
			startServerGui();
		} else {
			
		}
	}

	private static void startServerFromPropertiesFile(String propertiesFile) throws FileNotFoundException, IOException {
		AutomateServer server = new AutomateServer();
		Properties serverProperties = new Properties();
		serverProperties.load(new FileInputStream(propertiesFile));
		// start the server
		server.setProperties(serverProperties);
		try {
			server.initSubsystems();
		} catch (InitializationException e1) {
			e1.printStackTrace();
			return;
		}
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

	private static void startServerGui() {
		SetupGui gui = new SetupGui();
		AutomateServer server = new AutomateServer();
		while(true) {
			Properties serverProperties = gui.start();
			// start the server
			server.setProperties(serverProperties);
			try {
				server.initSubsystems();
			} catch (InitializationException e1) {
				e1.printStackTrace();
				continue;
			}
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

	static void showUsage() {
		System.out.println("Usage: (-p [properties file]) | -g");
		System.exit(1);
	}

}
