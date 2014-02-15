package com.automate.protocol;

import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlConvertible;
import com.automate.util.xml.XmlFormatException;

/**
 * Common parameters for messages sent by both the client and the server.
 */
public abstract class ProtocolParameters extends XmlConvertible {

	public final int majorVersion;
	public final int minorVersion;

	public ProtocolParameters(int majorVersion, int minorVersion) {
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}
	
	@Override
	protected void constructXml(StringBuilder builder, int indentationLevel) throws XmlFormatException {
		addElement("parameters", false);
		
		addVersionParameter();
		addSpecializedParameters();
		
		closeElement();
	}

	protected abstract void addSpecializedParameters();

	private void addVersionParameter() {
		addParameter("version", majorVersion + "." + minorVersion);
	}
	
	protected void addParameter(String name, String value) {
		addElement("parameter", true, new Attribute("name", name), new Attribute("value", value));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj.getClass().equals(this.getClass())) {
			return 	((ProtocolParameters) obj).majorVersion == this.majorVersion
					&& ((ProtocolParameters) obj).minorVersion == this.minorVersion;
		} else return false;
	}
	
}
