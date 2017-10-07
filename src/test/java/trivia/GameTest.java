package trivia;

import trivia.User;
import trivia.Game;

import org.javalite.activejdbc.Base;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GameTest{
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
		Game game = new Game();
		game.set("user_id", "");
		assertEquals(game.isValid(), false);
	}

	@Test
	public void validatePresenceOf2(){
		Game game = new Game();
		game.set("lifes", "");
		assertEquals(game.isValid(), false);
	}


	@Test
	public void validatePresenceOf3(){
		Game game = new Game();
		User user = new User();
		user.set("name", "TestUser");
		user.set("password", "TestPassword");
		user.saveIt();
		game.set("user_id", user.get("id"));
		game.set("lifes", 3);
		assertEquals(game.isValid(), true);
	}

}
