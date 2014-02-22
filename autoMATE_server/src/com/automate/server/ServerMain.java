package com.automate.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
		System.setProperty("log4j.configurationFile", "config/log4j2.xml");
		Logger logger = LogManager.getLogger();
		logger.info("==> AUTOMATE SERVER START\n");
		if(args.length < 1) {
			showUsage();
		} else if(args[0].equalsIgnoreCase("-p")) {
			if(args.length < 2) {
				showUsage();
			} else {
				try {
					logger.info("Starting server with properties file {}", args[1]);
					startServerFromPropertiesFile(args[1], logger);
				} catch (FileNotFoundException e) {
					logger.error("Failed to start server - file not found.", e);
					System.exit(2);
				} catch (IOException e) {
					logger.error("Failed to start server.", e);
					System.exit(3);
				}
			}
		} else if(args[0].equalsIgnoreCase("-g")) {
			startServerGui();
		} else {
			
		}
		logger.info("<== AUTOMATE SERVER END\n\n\n");
	}

	private static void startServerFromPropertiesFile(String propertiesFile, Logger logger) throws FileNotFoundException, IOException {
		AutomateServer server = new AutomateServer();
		Properties serverProperties = new Properties();
		logger.trace("Loading properties file.");
		serverProperties.load(new FileInputStream(propertiesFile));
		// start the server
		server.setProperties(serverProperties);
		try {
			server.initSubsystems();
		} catch (Exception e) {
			logger.error("Failed to initialize server.", e);
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
