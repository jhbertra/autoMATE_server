
package com.automate.server.database;


import java.util.List;

import com.automate.server.IManager;
import com.automate.server.database.models.Manufacturer;
import com.automate.server.database.models.Model;
import com.automate.server.database.models.Node;
import com.automate.server.database.models.User;

/**
 * The manager that handles all database interaction.
 * @author jamie.bertram
 *
 */
public interface IDatabaseManager extends IManager {


	/*
	 * User queries.
	 */
	User getUserByUsername(String username);
	User getUserByUid(long userId);

	/*
	 * Node queries.
	 */
	Node getNodeByUid(long nodeId);
	List<Node> getClientNodeList(String username);

	/*.
	 * Model queries
	 */
	Model getModelByUid(long modelId);
	
	/*
	 * Manufacturer queries.
	 */
	Manufacturer getManufacturerByUid(long manufacturerId);
	
}
=======
package com.automate.server.database;


import java.util.List;

import com.automate.server.IManager;
import com.automate.server.database.models.Manufacturer;
import com.automate.server.database.models.Model;
import com.automate.server.database.models.Node;
import com.automate.server.database.models.User;

/**
 * The manager that handles all database interaction.
 * @author jamie.bertram
 *
 */
public interface IDatabaseManager extends IManager {


	/*
	 * User queries.
	 */
	User getUserByUsername(String username);
	User getUserByUid(int userId);

	/*
	 * Node queries.
	 */
	Node getNodeByUid(long nodeId);
	List<Node> getClientNodeList(String username);

	/*.
	 * Model queries
	 */
	Model getModelByUid(long modelId);
	
	/*
	 * Manufacturer queries.
	 */
	Manufacturer getManufacturerByUid(long manufacturerId);
	
}
>>>>>>> branch 'master' of https://github.com/jhbertra/autoMATE_server.git
