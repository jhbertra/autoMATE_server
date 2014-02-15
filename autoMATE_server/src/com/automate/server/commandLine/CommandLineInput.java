package com.automate.server.commandLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.automate.server.AutomateServer;
import com.automate.server.commandLine.util.Parser;
import com.automate.server.commandLine.util.Scanner;
import com.automate.server.commandLine.util.Scanner.ScannerCallback;
import com.automate.server.commandLine.util.SyntaxTreeNode;

public class CommandLineInput implements ScannerCallback {

	private AutomateServer.Api serverApi;
	private Scanner scanner;
	private Parser parser;
	private GrammarFile grammarFile;
	
	public CommandLineInput(AutomateServer.Api serverApi) {
		this.serverApi = serverApi;
		this.scanner = new Scanner(this);
	}

	public void loadGrammar(GrammarFile grammarFile) {
		this.parser = new Parser(scanner, grammarFile);
		this.scanner.addRules(grammarFile.getScannerRules());
		this.grammarFile = grammarFile;
	}

	public void start() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		SyntaxTreeNode node = null;
		boolean continueParsing = true;
		do {
			System.out.print(grammarFile.getCurrentStateBreatcrumb() + "> ");
			try {
				node = parser.parse(reader.readLine());
				continueParsing = (Boolean) node.execute(serverApi);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println();
		} while (continueParsing);
	}

	@Override
	public void error(String string) {
		System.out.println("Error handling input: " + string);
	}
	
}
