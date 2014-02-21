package com.automate.server;

import com.automate.server.database.IDatabaseManager;
import com.automate.server.security.ISessionManager;

public class SecurityManagerParameters {

	public final ISessionManager sessionManager;
	public final IDatabaseManager dbManager;
	public final int majorVersion;
	public final int minorVersion;

	public SecurityManagerParameters(ISessionManager sessionManager,
			IDatabaseManager dbManager, int majorVersion, int minorVersion) {
		super();
		this.sessionManager = sessionManager;
		this.dbManager = dbManager;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}

}
