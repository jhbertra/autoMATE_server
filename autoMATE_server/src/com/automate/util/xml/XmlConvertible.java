package com.automate.util.xml;

import java.util.Stack;


/**
 * Base class for objects representable by Xml.
 */
public abstract class XmlConvertible {

	/**
	 * Current indentation level/ depth.
	 */
	protected int indentationLevel;
	/**
	 * Current {@link StringBuilder} for appending xml.
	 */
	protected StringBuilder builder;
	
	private Stack<ElementTag>openTags = new Stack<ElementTag>();
	
	public void toXml(StringBuilder builder, int indentationLevel) throws XmlFormatException {
		this.indentationLevel = indentationLevel;
		this.builder = new StringBuilder();
		constructXml(this.builder, this.indentationLevel);
		if(this.indentationLevel != indentationLevel) {
			throw new XmlFormatException("Indentation level mismatch.  Before: " + indentationLevel + ", after: " + this.indentationLevel);
		}
		if(!this.openTags.isEmpty()) {
			throw new XmlFormatException("Open tag " + openTags.peek().getName() + " Not closed.");
		}
		builder.append(this.builder.toString());
	}
	
	private void indent() {
		for(int i = 0; i < indentationLevel; ++i) {
			builder.append('\t');
		}
	}
	
	protected void addElement(String element, boolean selfClosing, Attribute ... attributes) {
		indent();
		ElementTag elementTag = new ElementTag(element, selfClosing, attributes); 
		elementTag.appendAsXml(builder);
		if(!selfClosing) {
			openTags.push(elementTag);
			++indentationLevel;
		}
		builder.append('\n');
	}
	
	protected void closeElement() throws XmlFormatException {
		if(openTags.isEmpty()) {
			throw new XmlFormatException("Attempted to close element tag when no elements are unclosed.");
		}
		ElementTag tag = openTags.pop();
		--indentationLevel;
		indent();
		tag.appendClosingTagAsXml(builder);
		builder.append("\n");
	}
	
	public String getInnerXml() {
		if(this.builder != null) {
			return this.builder.toString();
		} else {
			return null;
		}
	}
	
	protected abstract void constructXml(StringBuilder builder, int indentationLevel) throws XmlFormatException;
	
}
