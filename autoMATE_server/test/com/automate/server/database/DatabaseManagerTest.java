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
//	
//	@Test(expected=NullPointerException.class)
//	public void testGetClientNodeList_NullUsername(){
//		subject.getClientNodeList(null);
//	}
	
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
	
}
