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
}
