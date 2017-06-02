package trivia;

import org.javalite.activejdbc.Model;

public class Question extends Model {
  static{
    validatePresenceOf("question").message("Please, provide your question");
    validatePresenceOf("category_id").message("Please, provide your question");
    validatePresenceOf("question").message("Please, provide your question");
  	validatePresenceOf("answer1").message("Please, provide your question");
  	validatePresenceOf("answer2").message("Please, provide your question");
  	validatePresenceOf("answer3").message("Please, provide your question");
  	validatePresenceOf("correct").message("Please, provide your question");
  }
}
