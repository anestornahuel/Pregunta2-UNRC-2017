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
		validatePresenceOf("questionNumber").message("Please, provide questionNumber");
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
		set("questionNumber", 0);
		saveIt();
	}

	public void setScore(int score) {
		set("score", score);
		saveIt();
	}

	public int score() {
		return getInteger("score");
	}

	public int questionNumber() {
		return getInteger("questionNumber");
	}

	// Cambia el turno, debe jugar user2
	public void changeTurn() {
		String aux = new String(getString("user1"));
		set("user1", getString("user2"));
		set("user2", aux);
		int auxcorrect = getInteger("corrects1");
		set("corrects1", getInteger("corrects2"));
		set("corrects2", auxcorrect);
		set("questionNumber", questionNumber() + 1);
		saveIt();
	}

	public static Duel getFirst(int id) {
		return findFirst("id = ?", id);
	}

	// Devuelve el duelo entre los usuarios u1 y u2
	public static Duel getFirst(String u1, String u2) {
		String query = "select * from duels where user1 = \'"+u1+"\' and user2 = \'"+u2+"\'";
		List list = findBySQL(query);
		return (list.size() == 0) ? null : (Duel)list.get(0);
	}

	// Si existe duelo entre los usuarios u1 y u2 
	public static boolean exist(String u1, String u2) {
		return getFirst(u1, u2) != null && getFirst(u2, u1) != null;
	}

	// Retorna los desafiantes de un usuario dado
	public static List desafiants(String u2) {
		String query = "select * from duels where user2 = \'" + u2 + "\'";
		return Duel.findBySQL(query).collect("user1");
	}

	// El usuario u respondio correctamente
	public void correct(String u) {
		if (u == getString("user1")) {
			set("corrects1", getInteger("corrects1") + 1);
		}else if (u == getString("user2")) {
			set("corrects2", getInteger("corrects2") + 1);
		}
		saveIt();
	}
}