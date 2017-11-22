package trivia;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.validation.UniquenessValidator;

public class User extends Model {

	static public final int LIVECOST = 50;		// Precio de una vida

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
		set("lastupdate", getDate("created_at"));
		saveIt();
	}

	public boolean buyLive() {
		if (score() >= LIVECOST) {
			updateScore(-LIVECOST);
			updateLives(1);
			return true;
		}
		return false;
	}

	// Aumenta o disminuye x puntos
	public void updateScore(int x) {
		set("score", score() + x);
		saveIt();
	}

	// Retorna la cantidad de vidas globales
	public int lives() {
		return Integer.parseInt(this.getString("globalLives"));
	}

	// Retorna la cantidad de puntos
	public int score() {
		return Integer.parseInt(this.getString("score"));
	}

	// Reduce una vida global
	public void reduceLive() {
		set("globalLives", lives() - 1);
		saveIt();
	}

	// Aumenta o disminuye x vidas globales
	public void updateLives(int x) {
		set("globalLives", lives() + x);
		saveIt();
	}
}