package com.automate.util.xml;

public class ElementTag {
	
	private String name;
	private boolean selfClosing;
	private Attribute[] attributes;
	
	public ElementTag(String name, boolean selfClosing, Attribute ... attributes) {
		this.name = name;
		this.selfClosing = selfClosing;
		this.attributes = attributes;
	}

	public void appendAsXml(StringBuilder builder) {
		builder.append('<');
		builder.append(name);
		builder.append(' ');
		if(attributes != null) {
			for(int i = 0; i < attributes.length; ++i) {
				attributes[i].appendAsXml(builder);
				builder.append(' ');
			}
		}
		if(selfClosing) {
			builder.append('/');
		}
		builder.append('>');
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the selfClosing
	 */
	public boolean isSelfClosing() {
		return selfClosing;
	}

	/**
	 * @param selfClosing the selfClosing to set
	 */
	public void setSelfClosing(boolean selfClosing) {
		this.selfClosing = selfClosing;
	}

	/**
	 * @return the attributes
	 */
	public Attribute[] getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Attribute[] attributes) {
		this.attributes = attributes;
	}

	public void appendClosingTagAsXml(StringBuilder builder) {
		builder.append("</");
		builder.append(name);
		builder.append('>');
	}
	
}
