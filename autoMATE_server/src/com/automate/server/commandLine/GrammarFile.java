package com.automate.server.commandLine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.automate.server.commandLine.util.Parser.NonTerminal;
import com.automate.server.commandLine.util.Scanner;
import com.automate.server.commandLine.util.Token;
import com.automate.server.commandLine.util.Scanner.ScannerRule;
import com.automate.server.commandLine.util.SyntaxTreeNode.Action;
import com.automate.util.xml.XmlFormatException;

/**
 * Data structure that abstracts an xml grammar file for the server console
 * input.
 * @author jamie.bertram
 *
 */
public class GrammarFile {

	private String filename;
	private String [] tokenNames;
	
	private State root;
	private State current;
	private List<ScannerRule<?>> scannerRules;
	private List<List<Action>> actions;
	
	private GrammarFile(String filename, String[] tokenNames,
			State root, List<ScannerRule<?>> scannerRules, List<List<Action>> actions) {
		this.filename = filename;
		this.tokenNames = tokenNames;
		this.root = root;
		this.current = root;
		this.scannerRules = scannerRules;
		this.actions = actions;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * Gets the name of a token from its numeric id.
	 * @param tokenType - the id of the token
	 * @return the name of the token, as defined in the grammar file.
	 */
	public String getTokenName(int tokenType) {
		if(tokenType >= 0 || tokenType < tokenNames.length) {
			return tokenNames[tokenType];
		} else {
			return "undefined token type";
		}
	}

	/**
	 * Returns the parse starting point for the current state.
	 * @return the starting point of the parser for the current state.
	 */
	public NonTerminal getStart() {
		return current.start;
	}

	/**
	 * Returns the list of scanner rules defined in the grammar file.
	 * @return the scanner rules.
	 */
	public List<ScannerRule<?>> getScannerRules() {
		return scannerRules;
	}

	public String getCurrentStateBreatcrumb() {
		return current.getBreadcrumb();
	}
	
	public void setState(State state) {
		if(this.current.children.contains(state)) {
			this.current = state;
		} else if(this.current.parent.equals(state)) {
			this.current = state;
		} else {
			throw new IllegalStateException("Cannot navigate to state " + state.name + " from current state " + current.name);
		}
	}

	public Action getActionForProduction(int nonTerminalId, int i) {
		return actions.get(nonTerminalId).get(i);
	}

	public static class Factory {
		
		public static GrammarFile loadFile(String inputFile) throws ParserConfigurationException, SAXException, IOException, XmlFormatException {
			String [] tokenNames;
			HashMap<String, Integer> tokenIndecies = new HashMap<String, Integer>();
			State root;
			List<ScannerRule<?>> scannerRules = new ArrayList<Scanner.ScannerRule<?>>();
			List<List<Action>> actions = new ArrayList<List<Action>>();
			
			File file = new File(inputFile);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			document.getDocumentElement().normalize();
			
			tokenNames = parseTokens(scannerRules, document, tokenIndecies);
			root = buildStateTree(actions, document, tokenIndecies);
			
			return new GrammarFile(inputFile, tokenNames, root, scannerRules, actions);
		}

		private static State buildStateTree(List<List<Action>> actions, Document document, HashMap<String, Integer> tokenIndecies) {
			return null;
		}

		private static String[] parseTokens(List<ScannerRule<?>> scannerRules, Document document, HashMap<String,Integer> tokenIndecies) throws XmlFormatException {
			String[] tokenNames;
			NodeList nodes = document.getElementsByTagName("Tokens");
			Node tokensNode = nodes.item(0);
			NodeList tokenNodes = tokensNode.getChildNodes();
			tokenNames = new String[tokenNodes.getLength()];
			
			for(int i = 0; i < tokenNodes.getLength(); ++i) {
				Element tokenNode = (Element) tokenNodes.item(i);
				String datatype = tokenNode.getAttribute("data-type");
				String pattern = tokenNode.getAttribute("pattern");
				String tokenName = tokenNode.getAttribute("name");
				ScannerRule<?> rule = null;
				
				final int tokenType = i;
				if(datatype.equals("integer")) {
					rule = new ScannerRule<Integer>(pattern) {
						@Override
						public Token<Integer> getToken(String match, int position) {
							return new Token<Integer>(match, position, Integer.parseInt(match), tokenType);
						}
					};
				} else if(datatype.equals("boolean")) {
					rule = new ScannerRule<Boolean>(pattern) {
						@Override
						public Token<Boolean> getToken(String match, int position) {
							return new Token<Boolean>(match, position, Boolean.parseBoolean(match), tokenType);
						}
					};
				} else if(datatype.equals("decimal")) {
					rule = new ScannerRule<Double>(pattern) {
						@Override
						public Token<Double> getToken(String match, int position) {
							return new Token<Double>(match, position, Double.parseDouble(match), tokenType);
						}
					};
				} else if(datatype.equals("string")) {
					rule = new ScannerRule<String>(pattern) {
						@Override
						public Token<String> getToken(String match, int position) {
							return new Token<String>(match, position, match, tokenType);
						}
					};
				} else if(datatype.equals("void")) {
					rule = new ScannerRule<Void>(pattern) {
						@Override
						public Token<Void> getToken(String match, int position) {
							return new Token<Void>(match, position, (Void) null, tokenType);
						}
					};
				}
				if(rule == null) {
					throw new XmlFormatException("unrecognized token type " + datatype);
				}
				scannerRules.add(rule);
				tokenNames[i] = tokenName;
				tokenIndecies.put(tokenName, i);
			}
			return tokenNames;
		}
		
	}
	
	public static class State {
		
		public final NonTerminal start;
		private State parent;
		private List<State> children;
		private String name;
		
		public State(NonTerminal start, State parent, List<State> children, String name) {
			this.start = start;
			this.parent = parent;
			this.children = children;
			this.name = name;
		}
		
		public String getBreadcrumb() {
			StringBuilder builder = new StringBuilder();
			State state = this;
			do {
				builder.append(state.name);
				builder.append('/');
			} while((state = state.parent) != null);
			return builder.toString();
		}
		
	}
	
}
