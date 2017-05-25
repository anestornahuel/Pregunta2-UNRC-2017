package trivia;

import org.javalite.activejdbc.Model;

public class Category extends Model {
  static{
    validatePresenceOf("name").message("Please, provide your categoryname");
  }
}
