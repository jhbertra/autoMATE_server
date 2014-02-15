package com.automate.server.database.models;

/**
 * Encapsulates a Model element from the database
 */
public class Model {

	/**
	 * This model's uniqe id.
	 */
	public final long uid;
	/**
	 * The foreign key for this model's manufacturer.
	 */
	public final long manufacturerId;
	/**
	 * The url to the information page for this model.
	 */
	public final String informationUrl;
	/**
	 * The url to the command list spec. of this model.
	 */
	public final String commandListUrl;
	/**
	 * The name of the model
	 */
	public final String name;
	
	public Model(long uid, long manufacturerId, String informationUrl, String commandListUrl, String name){
		this.uid = uid;
		this.manufacturerId = manufacturerId;
		this.informationUrl = informationUrl;
		this.commandListUrl = commandListUrl;
		this.name = name;
	}
}