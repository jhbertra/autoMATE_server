package com.automate.util.xml;

public class Attribute {

	private String name;
	private String value;
	
	public Attribute(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Attribute) {
			return name.equals(((Attribute) o).name) && value.equals(((Attribute) o).value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return name + "=\"" + value +"\"";
	}
	
	public void appendAsXml(StringBuilder builder) {
		builder.append(name);
		builder.append('=');
		builder.append('"');
		builder.append(value);
		builder.append('"');
	}
	
}
