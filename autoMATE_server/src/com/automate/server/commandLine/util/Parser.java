package com.automate.server.commandLine.util;

import java.util.ArrayList;
import java.util.List;

import com.automate.server.commandLine.GrammarFile;
import com.automate.server.commandLine.util.SyntaxTreeNode.LeafNode;

public class Parser {

	private Scanner scanner;
	private GrammarFile grammarFile;
	
	public Parser(Scanner scanner, GrammarFile grammarFile) {
		this.scanner = scanner;
		this.grammarFile = grammarFile;
	}

	public SyntaxTreeNode parse(String input) {
		scanner.setInput(input);
		try {
			return grammarFile.getStart().parse(this);
		} catch (ParseException e) {
			scanner.getCallback().error("Syntax error: " + e.getMessage());
			return null;
		}
	}
	
	public static abstract class SyntaxNode <S extends SyntaxTreeNode> {
		
		public abstract S parse(Parser parser) throws ParseException;
		
	}
	
	public static class NonTerminal extends SyntaxNode <SyntaxTreeNode> {
		
		final public int nonTerminalId;
		final public List<List<SyntaxNode<SyntaxTreeNode>>> production;

		public NonTerminal(int productionType,
				List<List<SyntaxNode<SyntaxTreeNode>>> production) {
			this.nonTerminalId = productionType;
			this.production = production;
		}

		@Override
		public SyntaxTreeNode parse(Parser parser) throws ParseException {
			ParseException exception = null;
			ArrayList<SyntaxTreeNode> children = new ArrayList<SyntaxTreeNode>();
			int i = 0;
			for(List<SyntaxNode<SyntaxTreeNode>> rule : production) {
				if(children.size() > 0) {
					--i;
					break;
				}
				for(SyntaxNode<SyntaxTreeNode> element : rule) {
					try {
						SyntaxTreeNode node = element.parse(parser);
						if(node != null) {
							children.add(node);
						}
					} catch(ParseException e) {
						children.clear();
						exception = e;
						break;
					}
				}
				++i;
			}
			if(children.size() > 0) {
				return new SyntaxTreeNode(this, children, parser.grammarFile.getActionForProduction(nonTerminalId, i));
			} else if(exception == null) {
				return null;
			} else {
				throw exception;
			}
		}

	}
	
	public static class Terminal<T> extends SyntaxNode<LeafNode<T>> {
		
		public final int tokenType;

		public Terminal(int tokenType) {
			this.tokenType = tokenType;
		}

		@Override
		public LeafNode<T> parse(Parser parser) throws ParseException {
			Token<?> token = parser.scanner.lex();
			if(token != null && token.tokenType == this.tokenType) {
				return new LeafNode<T>((Token<T>) token);
			}
			throw new ParseException("Unexpected token \"" + token.matchedString + "\".  Expected " 
			+ parser.grammarFile.getTokenName(this.tokenType));
		}
		
	}
	
	public static class EmptyTerminal extends SyntaxNode<LeafNode<Void>> {

		public final int [] followSet;
		
		public EmptyTerminal(int[] followSet) {
			this.followSet = followSet;
		}

		@Override
		public LeafNode<Void> parse(Parser parser) throws ParseException {
			Token<?> token = parser.scanner.peek();
			if(token == null) return null;
			for(int i = 0; i < followSet.length; ++i) {
				if(token.tokenType == followSet[i]) {
					return null;
				}
			}
			throw new ParseException("Unexpected token \"" + token.matchedString + "\".");
		}
		
	}
	
}
