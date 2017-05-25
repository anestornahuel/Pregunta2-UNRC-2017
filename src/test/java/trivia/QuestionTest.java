package trivia;

import trivia.User;

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

    // @Test
    // public void validateUniquenessOfUsernames(){
    //     User user = new User();
    //     user.set("username", "anakin");
    //     user.saveIt();

    //     User user2 = new User();
    //     user.set("username", "anakin");

    //     assertEquals(user2.isValid(), false);
    // }

    @Test
    public void validateUniquenessOfQuestionQuestion(){
        Question ques = new Question();
        ques.set("question", "");

        assertEquals(ques.isValid(), false);
    }
}
