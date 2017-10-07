package trivia;

import trivia.User;

import org.javalite.activejdbc.Base;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest{
	@Before
	public void before(){
		Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia_test", "root", "root");
		System.out.println("UserTest setup");
		Base.openTransaction();
	}

	@After
	public void after(){
		System.out.println("UserTest tearDown");
		Base.rollbackTransaction();
		Base.close();
	}

	@Test
	public void validatePresenceOf1(){
		User user = new User();
		user.set("name", "");
		user.set("password", "1234");
		assertEquals(user.isValid(), false);
	}

	@Test
	public void validatePresenceOf2(){
		User user = new User();
		user.set("name", "UserName");
		user.set("password", "");
		assertEquals(user.isValid(), false);
	}

	@Test
	public void validatePresenceOf3(){
		User user = new User();
		user.set("name", "UserName");
		user.set("password", "1234");
		assertEquals(user.isValid(), true);
	}

	@Test
	public void uniqueness1(){
		User user1 = new User();
		user1.set("name", "UserName");
		user1.set("password", "1234");
		user1.saveIt();
		User user2 = new User();
		user2.set("name", "UserName");
		user2.set("password", "1234");
		user2.saveIt();
		assertEquals(user2.isValid(), false);
	}

	@Test
	public void uniqueness2(){
		User user1 = new User();
		user1.set("name", "UserName1");
		user1.set("password", "1234");
		user1.saveIt();
		User user2 = new User();
		user2.set("name", "UserName2");
		user2.set("password", "1234");
		user2.saveIt();
		assertEquals(user2.isValid(), true);
	}
}
