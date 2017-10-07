package trivia;

import trivia.Category;

import org.javalite.activejdbc.Base;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CategoryTest{
	@Before
	public void before(){
		Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia_test", "root", "root");
		System.out.println("CategoryTest setup");
		Base.openTransaction();
	}

	@After
	public void after(){
		System.out.println("CategoryTest tearDown");
		Base.rollbackTransaction();
		Base.close();
	}

	@Test
	public void validatePresenceOf1(){
		Category cat = new Category();
		cat.set("name", "");
		assertEquals(cat.isValid(), false);
	}

	@Test
	public void validatePresenceOf2(){
		Category cat = new Category();
		cat.set("name", "Deporte");
		assertEquals(cat.isValid(), true);
	}

	@Test
	public void uniqueness1(){
		Category cat1 = new Category();
		cat1.set("name", "Deporte");
		cat1.saveIt();
		Category cat2 = new Category();
		cat2.set("name", "Deporte");
		cat2.saveIt();
		assertEquals(cat2.isValid(), false);
	}

	@Test
	public void uniqueness2(){
		Category cat1 = new Category();
		cat1.set("name", "Deporte");
		cat1.saveIt();
		Category cat2 = new Category();
		cat2.set("name", "Historia");
		cat2.saveIt();
		assertEquals(cat2.isValid(), true);
	}
}
