package trivia;
import trivia.User;
import org.javalite.activejdbc.Base;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
      Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");

      User u = new User();
      u.set("username", "Maradona");
      u.set("password", "messi");
      u.saveIt();

      Base.close();
    }
}
