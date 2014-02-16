package com.automate.protocol.models;

import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlConvertible;
import com.automate.util.xml.XmlFormatException;

public class CommandArgument<T> extends XmlConvertible {

	public final String name;
	public final Type type;
	public final T value;
	
	private CommandArgument(String name, Type type, T value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@Override
	protected void constructXml(StringBuilder builder, int indentationLevel)
			throws XmlFormatException {
		addElement("argument", true
				, new Attribute("name", name)
				, new Attribute("type", type.toString())
				, new Attribute("value", value.toString()));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CommandArgument<?>) {
			return 	((CommandArgument) obj).name.equals(name)
					&& ((CommandArgument) obj).type.equals(type)
					&& ((CommandArgument) obj).value.equals(value);
		} else return false;
	}

	public static CommandArgument<?> newCommandArgument(String name, Type type, String value) {
			switch (type) {
			case STRING:
				return new CommandArgument<String>(name, type, value);
			case INTEGER:
				return new CommandArgument<Integer>(name, type, Integer.parseInt(value));
			case REAL:
				return new CommandArgument<Double>(name, type, Double.parseDouble(value));
			case BOOLEAN:
				return new CommandArgument<Boolean>(name, type, Boolean.parseBoolean(value));
			case PERCENT:
				double percentValue = Double.parseDouble(((String) value).substring(0, ((String) value).indexOf("%")));
				return new CommandArgument<String>(name, type, String.valueOf(percentValue) + "%");
			default:
				return null;
			}
	}
	
	public static CommandArgument<?> newCommandArgument(String name, Type type, Object value) {
		try {
			switch (type) {
			case STRING:
				return new CommandArgument<String>(name, type, (String) value);
			case INTEGER:
				return new CommandArgument<Integer>(name, type, (Integer) value);
			case REAL:
				return new CommandArgument<Double>(name, type, (Double) value);
			case BOOLEAN:
				return new CommandArgument<Boolean>(name, type, (Boolean) value);
			case PERCENT:
				double percentValue;
				if(value instanceof String) {
					percentValue = Double.parseDouble(((String) value).substring(0, ((String) value).indexOf("%")));
				} else if (value instanceof Double) {
					percentValue = ((Double)value);
				} else if (value instanceof Integer) {
					percentValue = ((Integer)value);
				} else {
					throw new ClassCastException("Cannot convert " + value.getClass().getName() + " to percentage.");
				}
				return new CommandArgument<String>(name, type, String.valueOf(percentValue) + "%");
			default:
				return null;
			}
		} catch(ClassCastException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return getClass().getName() + ":\nname: " + name + "\ntype: " + type + "\nvalue: " + value;
	}
	
}
