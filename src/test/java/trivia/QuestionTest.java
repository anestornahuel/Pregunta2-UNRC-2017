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
        System.out.println(cat.get("id"));
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
        System.out.println(cat.get("id"));
        ques.set("category_id", cat.get("id"));
        ques.set("question", "¿que haces?");
        ques.set("answer1", "nada");
        ques.set("answer2", "poco");
        ques.set("answer3", "mucho");
        ques.set("correct", 1);

        assertEquals(ques.isValid(), true);
    }




}
