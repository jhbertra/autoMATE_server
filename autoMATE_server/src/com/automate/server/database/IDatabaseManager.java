package com.automate.server.database;

import java.util.List;

import com.automate.protocol.models.Node;
import com.automate.server.IManager;
import com.automate.server.database.models.User;

/**
 * The manager that handles all database interaction.
 * @author jamie.bertram
 *
 */
public interface IDatabaseManager extends IManager {

	List<Node> getClientNodeList(String username);

	User getUser(String username);
	
}
