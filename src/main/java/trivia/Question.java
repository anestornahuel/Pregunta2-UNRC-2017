package trivia;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.validation.UniquenessValidator;

public class Question extends Model {
	static {
	  	validatePresenceOf("category_id").message("Please, provide your question");
	    	validatePresenceOf("question").message("Please, provide your question");
	  	validatePresenceOf("answer1").message("Please, provide your question");
	  	validatePresenceOf("answer2").message("Please, provide your question");
	  	validatePresenceOf("answer3").message("Please, provide your question");
	  	validatePresenceOf("correct").message("Please, provide your question");
	  	validateRange("correct", 1, 3).message("correct cannot be less than 1 or more than 3");
	  	validateWith(new UniquenessValidator("question")).message("This question is already taken.");
  	}

  	public Question() {
  		super();
  	}

  	public Question(String cid, String ques, String ans1, String ans2, String ans3, String co) {
		set("category_id", cid);
		set("question", ques);
		set("answer1", ans1);
		set("answer2", ans2);
		set("answer3", ans3);
  		set("correct", Integer.parseInt(co));
  		saveIt();
  	}  

  	public static Question getFirst(String question) {
  		return Question.findFirst("question = ?", question);
  	}
}
