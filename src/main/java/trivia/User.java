package trivia;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.validation.UniquenessValidator;

public class User extends Model {
	static{
    		validatePresenceOf("name").message("Please, provide your username");
		validatePresenceOf("password").message("Please, provide your password");
    		validateWith(new UniquenessValidator("name")).message("This name is already taken.");
		
	}

	// Constructor
	public User() {
		super();
	}

	// Constructor
	public User(String name, String pass) {
		set("password", pass, "name", name, "score", "0", "globalLives", "5");
		saveIt();
	}

	// Aumenta o disminuye x puntos
	public void updateScore(int x) {
		int score = Integer.parseInt(this.getString("score")) + x;
		set("score", score);
		saveIt();
	}

	// Retorna la cantidad de vidas globales
	public int lives() {
		return Integer.parseInt(this.getString("globalLives"));
	}

	// Reduce una vida global
	public void reduceLive() {
		// int lives = Integer.parseInt(this.getString("globalLives")) - 1;
		set("globalLives", lives() - 1);
		saveIt();
	}


}
