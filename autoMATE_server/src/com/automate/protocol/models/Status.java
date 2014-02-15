package com.automate.protocol.models;

import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlConvertible;
import com.automate.util.xml.XmlFormatException;

public class Status<T> extends XmlConvertible {

	public final String name;
	public final Type type;
	public final T value;

	private Status(String name, Type type, T value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@Override
	protected void constructXml(StringBuilder builder, int indentationLevel) throws XmlFormatException {
		addElement("status", true
				, new Attribute("component-name", name)
		, new Attribute("type", type.toString())
		, new Attribute("value", value.toString()));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Status<?>) {
			return 	this.name.equals(((Status) obj).name)
					&& this.type.equals(((Status) obj).type)
					&& this.value.equals(((Status) obj).value);
		} else return false;
	}

	public static Status<?> newStatus(String componentName, Type type, Object value) {
		try {
			switch (type) {
			case STRING:
				return new Status<String>(componentName, type, (String) value);
			case INTEGER:
				return new Status<Integer>(componentName, type, (Integer) value);
			case REAL:
				return new Status<Double>(componentName, type, (Double) value);
			case BOOLEAN:
				return new Status<Boolean>(componentName, type, (Boolean) value);
			case PERCENT:
				double percentValue;
				if(value instanceof String) {
					percentValue = Double.parseDouble(((String) value).substring(0, ((String) value).indexOf("%"))) / 100.0;
				} else if (value instanceof Double) {
					percentValue = ((Double)value) / 100.0;
				} else if (value instanceof Integer) {
					percentValue = ((Integer)value) / 100.0;
				} else {
					throw new ClassCastException("Cannot convert " + value.getClass().getName() + " to percentage.");
				}
				return new Status<Double>(componentName, type, percentValue);
			default:
				return null;
			}
		} catch(ClassCastException e) {
			return null;
		}
	}	

	public static Status<?> newStatus(String componentName, Type type, String value) {
		switch (type) {
		case STRING:
			return new Status<String>(componentName, type, value);
		case INTEGER:
			return new Status<Integer>(componentName, type, Integer.parseInt(value));
		case REAL:
			return new Status<Double>(componentName, type, Double.parseDouble(value));
		case BOOLEAN:
			return new Status<Boolean>(componentName, type, Boolean.parseBoolean(value));
		case PERCENT:
			double percentValue = Double.parseDouble(((String) value).substring(0, ((String) value).indexOf("%"))) / 100.0;
			return new Status<Double>(componentName, type, percentValue);
		default:
			return null;
		}
	}

}
