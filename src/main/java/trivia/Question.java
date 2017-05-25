package trivia;

import org.javalite.activejdbc.Model;

public class Question extends Model {
  static{
    validatePresenceOf("question").message("Please, provide your question");
  }
}
