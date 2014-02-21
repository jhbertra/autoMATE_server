package com.automate.server.database.models;

/**
 * Encapsulates a manufacturer element from the database.
 */
public class Manufacturer {
	
	/**
	 * This Manufacturer's unique id.
	 */
	public final long uid;
	/**
	 * The name of the manufacturer
	 */
	public final String name;
	/**
	 * The url for this manufacturer's info page.
	 */
	public final String informationUrl;
	
	public Manufacturer(long uid, String name, String informationurl){
		this.uid = uid;
		this.name = name;
		this.informationUrl = informationurl;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Manufacturer) {
			return 	this.uid == ((Manufacturer)obj).uid &&
					this.name.equals(((Manufacturer)obj).name) &&
					this.informationUrl.equals(((Manufacturer)obj).informationUrl);
		} else return false;
	}
	
	
}
