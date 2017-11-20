package trivia;

import org.javalite.activejdbc.Model;

public class Game extends Model {
	static {
		validatePresenceOf("user_id").message("Please, provide user_id");
		validatePresenceOf("lifes").message("Please, provide lifes");
	}

	public Game() {
		super();
	}

	public int lifes() {
		return Integer.parseInt(this.getString("lifes"));
	}

	public Game(String uid, String lifes) {
		set("user_id", uid);
		set("lifes", lifes);
		saveIt();
	}

	// Descuenta una vida
	public void reduceLife() {
		int lifes = Integer.parseInt(this.getString("lifes")) - 1;
		set("lifes", lifes);
		saveIt();
	}

	// Devuelve el juego del usuario dado
	public static Game getFirst(String uid) {
		return findFirst("user_id = ?", uid);
	}
}
