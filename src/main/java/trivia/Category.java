package trivia;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.validation.UniquenessValidator;


public class Category extends Model {
	static {
    		validatePresenceOf("name").message("Please, provide your categoryname");
    		validateWith(new UniquenessValidator("name")).message("This name is already taken.");
	}
	public Category() {
		super();
	}

	public Category(String n) {
		set("name", n);
		saveIt();
	}

	public static Category getFirst(String name) {
		return Category.findFirst("name = ?", name);
	}
}
