package trivia;

import org.javalite.activejdbc.Model;

public class Game extends Model {
	static{
		validatePresenceOf("user_id").message("Please, provide user_id");
		validatePresenceOf("lifes").message("Please, provide lifes");
	}
}
