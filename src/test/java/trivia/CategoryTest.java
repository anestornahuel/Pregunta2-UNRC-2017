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

    

}
