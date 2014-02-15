package com.automate.server.database.models;

import com.automate.server.database.IDatabaseManager;

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
	/**
	 * The maximum protocol version this node supports.
	 */
	public final String maxVersion;
	
	public Node(long uid, String name,int userId,int modelId, String maxVersion){
		this.uid = uid;
		this.name = name;
		this.userId = userId;
		this.modelId = modelId;
		this.maxVersion = maxVersion;
	}
	
	public com.automate.protocol.models.Node toProtocolNode(IDatabaseManager dbManager) {
		Model model = dbManager.getModelByUid(modelId);
		Manufacturer man = dbManager.getManufacturerByUid(model.manufacturerId);
		
		return new com.automate.protocol.models.Node(name, uid, man.name, model.name, maxVersion, model.informationUrl, model.commandListUrl);
	}
	
}
