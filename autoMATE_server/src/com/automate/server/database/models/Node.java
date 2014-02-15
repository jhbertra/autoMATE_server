package com.automate.server.database.models;

/**
 * Encapsulates a Node entry from the database.
 */
public class Node {
	/**
	 * The node's unique identifier.
	 */
	public final long uid;
	/**
	 * The node's optional name.
	 */
	public final String name;
	/**
	 * The foreign key of this node's user
	 */
	public final int userId;
	/**
	 * The foreign key of this node's model
	 */
	public final int modelId;
	
	public Node(long uid, String name,int userId,int modelId){
		this.uid = uid;
		this.name = name;
		this.userId = userId;
		this.modelId = modelId;
	}
	
}
