package com.automate.server.database.models;



public class Node {
	public final long uid;
	public final String name;
	public final int user_id;
	public final int model_id;
	
	public Node(long uid, String name,int user_id,int model_id){
		this.uid = uid;
		this.name = name;
		this.user_id = user_id;
		this.model_id = model_id;
	}
	
}
