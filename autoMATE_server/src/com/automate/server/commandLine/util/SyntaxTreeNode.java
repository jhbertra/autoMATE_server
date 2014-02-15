package com.automate.server.commandLine.util;

import java.util.ArrayList;

import com.automate.server.AutomateServer.Api;
import com.automate.server.commandLine.util.Parser.NonTerminal;

public class SyntaxTreeNode {

	public final NonTerminal nonTerminal;
	public final ArrayList<SyntaxTreeNode> children;
	private Action action;
	
	public SyntaxTreeNode(NonTerminal nonTerminal, ArrayList<SyntaxTreeNode> children, Action action) {
		this.nonTerminal = nonTerminal;
		this.children = children;
		this.action = action;
	}
	
	public Object execute(Api serverApi) {
		ArrayList<Object> args = new ArrayList<Object>();
		for(SyntaxTreeNode node : children) {
			if(node instanceof LeafNode<?>) {				
				args.add(((LeafNode<?>)node).token);
			} else {
				args.add(node.execute(serverApi));
			}
		}
		return this.action.run(args);
	}

	public static class LeafNode<T> extends SyntaxTreeNode {
		
		public final Token<T> token;

		public LeafNode(Token<T> token) {
			super(null, null, null);
			this.token = token;
		}
	}
	
	public static interface Action {
		
		public Object run(ArrayList<Object> args);
		
	}
	
}
