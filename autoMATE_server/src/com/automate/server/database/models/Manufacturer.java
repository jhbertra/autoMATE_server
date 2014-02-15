package com.automate.server.database.models;

public class Manufacturer {
	
	public final long uid;
	public final String name;
	public final String information_url;
	
	public Manufacturer(long uid, String name, String information_url){
		this.uid = uid;
		this.name = name;
		this.information_url = information_url;
		
	}
}
