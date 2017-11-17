package trivia;

import org.javalite.activejdbc.Model;
import java.util.*;

public class Duel extends Model {
	static {
		validatePresenceOf("user1").message("Please, provide user1");
		validatePresenceOf("user2").message("Please, provide user2");
		validatePresenceOf("score").message("Please, provide score");
		validatePresenceOf("corrects1").message("Please, provide corrects1");
		validatePresenceOf("corrects2").message("Please, provide corrects2");
		validatePresenceOf("category").message("Please, provide category");
	}

	public Duel() {
		super();
	}

	public Duel(String  u1, String u2, int s) {
		set("user1", u1);	// Desafiant
		set("user2", u2);	// Challenged
		set("score", s);
		set("corrects1", 0);
		set("corrects2", 0);
		set("category", 0);
	}

	public void updateCat() {
		set("category", getInteger("category") + 1);
	}

	public void setScore(int score) {
		set("score", score);
		saveIt();
	}

	public void changeTurn() {
		String aux = new String(getString("user1"));
		set("user1", getString("user2"));
		set("user2", aux);
	}

	public static Duel getFirst(int id) {
		return findFirst("id = ?", id);
	}

	public static Duel getFirst(String u1, String u2) {
		String query = "select * from duels where user1 = \'"+u1+"\' and user2 = \'"+u2+"\'";
		List list = findBySQL(query);
		return (list.size() == 0) ? null : (Duel)list.get(0);
	}

	public static boolean exist(String u1, String u2) {
		return getFirst(u1, u2) != null;
	}

	public static List desafiants(String u2) {
		String query = "select * from duels where user2 = \'" + u2 + "\'";
		return Duel.findBySQL(query).collect("user1");
	}

	public void correct(String u) {
		if (u == getString("user1")) {
			set("corrects1", getInteger("corrects1") + 1);
		}else if (u == getString("user2")) {
			set("corrects2", getInteger("corrects2") + 1);
		}
	}
}