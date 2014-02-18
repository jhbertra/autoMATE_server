package com.automate.server.database.models;

/**
 * Encapsulates data for a User element from the database.
 */
public class User {

	/**
	 * The numeric uid of this user
	 */
	public final long uid;
	/**
	 * This user's unique username
	 */
	public final String username;
	/**
	 * This user's first name
	 */
	public final String firstName;
	/**
	 * This user's last name.
	 */
	public final String lastName;
	/**
	 * This user's password.
	 */
	public final String password;
	/**
	 * This user's email.
	 */
	public final String email;
	
	public User(long uid, String username, String firstName, String lastName,
			String password, String email) {
		this.uid = uid;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
	}
	
	
	
}
