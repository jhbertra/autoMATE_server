package com.automate.server.database.models;

public class User {

	public final long uid;
	public final String sername;
	public final String firstName;
	public final String lastName;
	public final String password;
	public final String email;
	
	public User(long uid, String sername, String firstName, String lastName,
			String password, String email) {
		this.uid = uid;
		this.sername = sername;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
	}
	
	
	
}
