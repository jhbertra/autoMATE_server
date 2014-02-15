package com.automate.protocol.models;

import com.automate.util.xml.Attribute;
import com.automate.util.xml.XmlConvertible;
import com.automate.util.xml.XmlFormatException;

public class Node extends XmlConvertible {

	public final String name;
	public final long id;
	public final String manufacturerCode;
	public final String model;
	public final String maxVersion;
	public final String infoUrl;
	public final String commandListUrl;
	
	public Node(String name, long id, String manufacturerCode, String model,String maxVersion, String infoUrl, String commandListUrl) {
		this.name = name;
		this.id = id;
		this.manufacturerCode = manufacturerCode;
		this.model = model;
		this.maxVersion = maxVersion;
		this.infoUrl = infoUrl;
		this.commandListUrl = commandListUrl;
	}

	@Override
	protected void constructXml(StringBuilder builder, int indentationLevel) throws XmlFormatException {
		addElement("node", true
				, new Attribute("name", name)
				, new Attribute("id", String.valueOf(id))
				, new Attribute("manufacturer", manufacturerCode)
				, new Attribute("model", model)
				, new Attribute("max-version", maxVersion)
				, new Attribute("info-url", infoUrl)
				, new Attribute("command-list-url", commandListUrl));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Node) {
			return 	name.equals(((Node) obj).name)
					&& id == ((Node) obj).id
					&& manufacturerCode.equals(((Node) obj).manufacturerCode)
					&& model.equals(((Node) obj).model)
					&& maxVersion.equals(((Node) obj).maxVersion)
					&& infoUrl.equals(((Node) obj).infoUrl)
					&& commandListUrl.equals(((Node) obj).commandListUrl);
		} else return false;
	}
	
}
