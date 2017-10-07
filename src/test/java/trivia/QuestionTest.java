package trivia;

import trivia.Question;
import trivia.Category;

import org.javalite.activejdbc.Base;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QuestionTest{
	@Before
	public void before(){
		Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia_test", "root", "root");
		System.out.println("QuestionTest setup");
		Base.openTransaction();
	}

	@After
	public void after(){
		System.out.println("QuestionTest tearDown");
		Base.rollbackTransaction();
		Base.close();
	}

	@Test
	public void validatePresenceOf2(){
		Question ques = new Question();
		ques.set("question", "");
		assertEquals(ques.isValid(), false);
	}

	@Test
	public void validatePresenceOf3(){
		Question ques = new Question();
		Category cat = new Category();
		cat.set("name", "Deporte");
		ques.set("category_id", cat.get("id"));
		ques.set("question", "¿que haces?");
		ques.set("answer2", "poco");
		ques.set("answer3", "mucho");
		assertEquals(ques.isValid(), false);
	}



	@Test
	public void validatePresenceOf4(){
		Question ques = new Question();
		Category cat = new Category();
		cat.set("name", "Deporte");
		cat.saveIt();
		ques.set("category_id", cat.get("id"));
		ques.set("question", "¿que haces?");
		ques.set("answer1", "nada");
		ques.set("answer2", "poco");
		ques.set("answer3", "mucho");
		ques.set("correct", 4);
		assertEquals(ques.isValid(), false);
	}

	@Test
	public void validatePresenceOf5(){
		Question ques = new Question();
		Category cat = new Category();
		cat.set("name", "Deporte");
		cat.saveIt();
		ques.set("category_id", cat.get("id"));
		ques.set("question", "¿que haces?");
		ques.set("answer1", "nada");
		ques.set("answer2", "poco");
		ques.set("answer3", "mucho");
		ques.set("correct", 1);
		assertEquals(ques.isValid(), true);
	}


	@Test
	public void uniqueness1(){
		Category cat = new Category();
		cat.set("name", "Categoria");
		cat.saveIt();
		Question ques1 = new Question();
		ques1.set("category_id", cat.get("id"));
		ques1.set("question", "¿que haces?");
		ques1.set("answer1", "nada");
		ques1.set("answer2", "poco");
		ques1.set("answer3", "mucho");
		ques1.set("correct", 1);
		ques1.saveIt();
		Question ques2 = new Question();
		ques2.set("category_id", cat.get("id"));
		ques2.set("question", "¿que haces?");
		ques2.set("answer1", "nada");
		ques2.set("answer2", "poco");
		ques2.set("answer3", "mucho");
		ques2.set("correct", 1);
		ques2.saveIt();
		assertEquals(ques2.isValid(), false);
	}

	 @Test
	public void uniqueness2(){
		Category cat = new Category();
		cat.set("name", "Categoria");
		cat.saveIt();
		Question ques1 = new Question();
		ques1.set("category_id", cat.get("id"));
		ques1.set("question", "¿que haces?");
		ques1.set("answer1", "nada");
		ques1.set("answer2", "poco");
		ques1.set("answer3", "mucho");
		ques1.set("correct", 1);
		ques1.saveIt();
		Question ques2 = new Question();
		ques2.set("category_id", cat.get("id"));
		ques2.set("question", "¿que haces loco?");
		ques2.set("answer1", "nada");
		ques2.set("answer2", "poco");
		ques2.set("answer3", "mucho");
		ques2.set("correct", 1);
		ques2.saveIt();
		assertEquals(ques2.isValid(), true);
	}
}
