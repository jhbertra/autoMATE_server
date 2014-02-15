package com.automate.server.database.models;

public class Model {

	public final long uid;
	public final long mnfr_id;
	public final String informationUrl;
	public final String commandListUrl;
	
	public Model(long uid, long mnfr_id, String informationUrl, String commandListUrl){
	
		this.uid = uid;
		this.mnfr_id = mnfr_id;
		this.informationUrl = informationUrl;
		this.commandListUrl = commandListUrl;
	}
}