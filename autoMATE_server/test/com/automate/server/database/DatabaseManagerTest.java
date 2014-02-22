package com.automate.server.database;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.management.RuntimeErrorException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.automate.server.commandLine.GrammarFile.State;
import com.automate.server.database.models.Manufacturer;
import com.automate.server.database.models.Model;
import com.automate.server.database.models.Node;
import com.automate.server.database.models.User;

public class DatabaseManagerTest {

	private DatabaseManager subject;
	private Connection connection;
	private Statement stmt;
	private ResultSet rtSet;
	private Mockery context; 
	@Before
	public void setUp() throws Exception {
		context = new Mockery();
		connection = context.mock(Connection.class);
		stmt = context.mock(Statement.class);
		rtSet = context.mock(ResultSet.class);
		subject = new DatabaseManager(connection);
	}

	@Test(expected=NullPointerException.class)
	public void testNullConnection() {
		subject = new DatabaseManager(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetManufacturerByUid_InvalidUid(){
		subject.getManufacturerByUid(-1);
	}
	
	@Test
	public void testGetManufacturerByUid_ManufacturerNotFound() throws SQLException{
		context.checking(new Expectations() {
			{
				oneOf(connection).createStatement(); will(returnValue(stmt));
				oneOf(stmt).executeQuery("select * from manufacturer where uid = 0"); will(returnValue(rtSet));
				oneOf(rtSet).getLong("uid"); will(throwException(new SQLException()));
			}
		});

		assertNull(subject.getManufacturerByUid(0));
		context.assertIsSatisfied();
	}

	@Test
	public void testGetManufacturerByUid_ManufacturerFound() throws SQLException{
		context.checking(new Expectations() {
			{
				oneOf(connection).createStatement(); will(returnValue(stmt));
				oneOf(stmt).executeQuery("select * from manufacturer where uid = 0"); will(returnValue(rtSet));
				oneOf(rtSet).getLong("uid"); will(returnValue(0L));
				oneOf(rtSet).getString("name"); will(returnValue("Samsung"));
				oneOf(rtSet).getString("information_url"); will(returnValue("someUrl"));
			}
		});

		Manufacturer expected = new Manufacturer(0, "Samsung", "someUrl");
		assertEquals(expected, subject.getManufacturerByUid(0));
		context.assertIsSatisfied();
	}
	
	
	@Test
	public void testGetModelByUid_ModelNotFound() throws SQLException{
		context.checking(new Expectations(){
			{
				oneOf(connection).createStatement(); will(returnValue(stmt));
				oneOf(stmt).executeQuery("select * from model where uid = 0"); will(returnValue(rtSet));
				oneOf(rtSet).getLong("uid"); will(throwException(new SQLException()));
				
			}
		});
		assertNull(subject.getModelByUid(0));
		context.assertIsSatisfied();
		
	}
	//following test keeps failing
	@Test
	public void testGetModelByUid_ModelFound() throws SQLException{
		context.checking(new Expectations(){
			{
				oneOf(connection).createStatement(); will(returnValue(stmt));
				oneOf(stmt).executeQuery("select * from model where uid = 0"); will(returnValue(rtSet));
				oneOf(rtSet).getLong("uid"); will(returnValue(0L));
				oneOf(rtSet).getLong("mnfr_id"); will(returnValue(0L));
				oneOf(rtSet).getString("information_url"); will(returnValue("someUrl"));
				oneOf(rtSet).getString("command_list_url"); will(returnValue("someCmdUrl"));
				oneOf(rtSet).getString("name"); will(returnValue("someName"));
			}
		});
		Model expected = new Model(0, 0, "someUrl", "someCmdUrl", "someName");
		assertEquals(expected, subject.getModelByUid(0));
		context.assertIsSatisfied();
	}
	
	
	@Test
	public void testGetNodeByUid_NodeNotFound() throws SQLException{
		context.checking(new Expectations(){
			{
				oneOf(connection).createStatement(); will(returnValue(stmt));
				oneOf(stmt).executeQuery( "select * from node where uid = 0" ); will(returnValue(rtSet));
				oneOf(rtSet).getLong("uid");  will(throwException(new SQLException()));
				
			}
		});
		assertNull(subject.getNodeByUid(0));
		context.assertIsSatisfied();
	}
	
	@Test
	public void testGetNodeByUid_NodeFound() throws SQLException{
		context.checking(new Expectations(){
			{
				oneOf(connection).createStatement(); will(returnValue(stmt));
				oneOf(stmt).executeQuery( "select * from node where uid = 0" ); will(returnValue(rtSet));
				oneOf(rtSet).getLong("uid"); will(returnValue(0L));
				oneOf(rtSet).getString("name"); will(returnValue("someName"));
				oneOf(rtSet).getLong("user_id"); will(returnValue(0L));
				oneOf(rtSet).getLong("model_id"); will(returnValue(0L));
				oneOf(rtSet).getString("max_version"); will(returnValue("0"));
			}
		});
		Node expected = new Node(0, "someName", 0, 0, "0");
		assertEquals(expected,subject.getNodeByUid(0));
		context.assertIsSatisfied();
	}
	
	
	public void testGetUserByUid_NotFound() throws SQLException{
		context.checking(new Expectations(){
			{
				oneOf(connection).createStatement(); will(returnValue(stmt));
				oneOf(stmt).executeQuery( "select * from users where username = 0" ); will(returnValue(rtSet));
				oneOf(rtSet).getLong("uid"); will(throwException(new SQLException()));
			}
		});
		assertNull(subject.getUserByUid(0));
		context.assertIsSatisfied();
	}
	
	
	public void testGetUserByUid_Found() throws SQLException{
		context.checking(new Expectations(){
			{
				oneOf(connection).createStatement(); will(returnValue(stmt));
				oneOf(stmt).executeQuery( "select * from users where username = 0" ); will(returnValue(rtSet));
				oneOf(rtSet).getLong("uid"); will(returnValue(0L));
				oneOf(rtSet).getString("username"); will(returnValue("someUsername"));
				oneOf(rtSet).getString("first_name"); will(returnValue("someFirstName"));
				oneOf(rtSet).getString("last_name"); will(returnValue("someLastName"));
				oneOf(rtSet).getString("password"); will(returnValue("somePassword"));
				oneOf(rtSet).getString("email"); will(returnValue("some@email.com"));
			}
		});
		User expected = new User(0, "someUsername", "someFirstName", "someLastName", "somePassword", "some@email.com");
		assertEquals(expected, subject.getUserByUid(0));
		context.assertIsSatisfied();
	}
	
	public void testGetUserByUsername_NotFound() throws SQLException{
		context.checking(new Expectations(){
			{
				oneOf(connection).createStatement(); will(returnValue(stmt));
				oneOf(stmt).executeQuery( "select * from users where username = testUser" ); will(returnValue(rtSet));
				oneOf(rtSet).getLong("uid"); will(throwException(new SQLException()));
			}
		});
		assertNull(subject.getUserByUsername("testUser"));
		context.assertIsSatisfied();
	}
	
	public void testGetUserByUsername_Found() throws SQLException{
		context.checking(new Expectations(){
			{
				oneOf(connection).createStatement(); will(returnValue(stmt));
				oneOf(stmt).executeQuery( "select * from users where username = testUser" ); will(returnValue(rtSet));
				oneOf(rtSet).getLong("uid"); will(returnValue(0L));
				oneOf(rtSet).getString("username"); will(returnValue("testUser"));
				oneOf(rtSet).getString("first_name"); will(returnValue("someFirstName"));
				oneOf(rtSet).getString("last_name"); will(returnValue("someLastName"));
				oneOf(rtSet).getString("password"); will(returnValue("somePassword"));
				oneOf(rtSet).getString("email"); will(returnValue("some@email.com"));
			}
		});
		User expected = new User(0, "testUser", "someFirstName", "someLastName", "somePassword", "some@email.com");
		assertEquals(expected, subject.getUserByUid(0));
		context.assertIsSatisfied();
	}
	
}
