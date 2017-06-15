package trivia;
import static spark.Spark.*;
import trivia.Category;
import trivia.Game;
import trivia.Question;
import trivia.User;
import org.javalite.activejdbc.Base;

import java.util.HashMap;
import java.util.Map;
import java.util.*;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

public class App {


	static private boolean isValid(String s) {
		return s != null && s.length() > 6;
	}

	static private String htmlizar(String s) {
		String ret = "";
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == ' ') {
				ret += "_";
			}else {
				ret += s.charAt(i);				
			}
		}
		return ret;
	}

	static public final int LIFES = 3; // Vidas al iniciar un juego

	public static void main(String[] args) {



		staticFileLocation("/public");
		String driverdb = "com.mysql.jdbc.Driver";
		String basedb = "jdbc:mysql://localhost/trivia";
		String userdb = "root";
		String passworddb = "root";


		before((rq, rs) -> {
			Base.open(driverdb, basedb, userdb, passworddb);
		});

		after((rq, rs) -> {
			Base.close();
		});


		// Principal
		get("/", (rq, rs) -> {
			return new ModelAndView(null, "logueo.html");
		},new MustacheTemplateEngine());

		post("/logueo", (rq, rs) -> {
			Map map = new HashMap();
			String currentUser = rq.session().attribute("currentUser");
			User user;
			if (currentUser != null) {
				// Si hay usuario registrado
				user = User.findFirst("id = ?", currentUser);
				String usuario = user.getString("name");
				String puntos = user.getString("score");
				map.put("usuario", usuario);
				map.put("puntos", puntos);
				if (user.getString("esadmin") == "true") {
					map.put("typeadmin", "submit");
				}else {
					map.put("typeadmin", "hidden");
				}
				return new ModelAndView(map, "principal.html");
			}else {
				String entrar = rq.queryParams("entrar");
				//String registro = rq.queryParams("registrarme");
				if (entrar != null) {
					// Inicio de sesion
					String nombrel = rq.queryParams("nombrel");
					String passwordl = rq.queryParams("clavel");	
					user = User.findFirst("name = ?", nombrel);
					if (user != null && user.getString("password").equals(passwordl)) {
						rq.session(true);
						map.put("usuario", nombrel);
						String puntos = user.getString("score");
						map.put("puntos", puntos);
						rq.session().attribute("currentUser", user.getString("id"));
						return new ModelAndView(map, "principal.html");
					}else {
						map.put("estado", "Nombre de usuario o password incorrecto");
						return new ModelAndView(map, "logueo.html");						
					}
				}else {
					// Registro de nuevo usuario
					String nombrer = rq.queryParams("nombrer");
					String passwordr = rq.queryParams("clave1");	
					String password2r = rq.queryParams("clave2");
					user = User.findFirst("name = ?", nombrer);			
					if (user == null) {
						if (isValid(nombrer) && isValid(password2r)) {
							if (passwordr.equals(password2r)) {
								User usr = new User();
								usr.set("name", nombrer);
								usr.set("password", passwordr);
								usr.set("score", 0);
								usr.set("esadmin", false);
								usr.saveIt();
								map.put("estado", "Se creo la cuenta \"" + nombrer + "\", ahora inicia sesion");
								return new ModelAndView(map, "logueo.html");
							}else {
								map.put("estado", "Las contraseñas ingresadas no coinciden");
								return new ModelAndView(map, "logueo.html");
							}							
						}else {
							map.put("estado", "El nombre de usuario y contraseña deben tener mas de 6 caracteres");
							return new ModelAndView(map, "logueo.html");
						}
					}else {
						map.put("estado", "El nombre de usuario \"" + nombrer + "\" ya existe");
						return new ModelAndView(map, "logueo.html");
					}
				}
			}
		},new MustacheTemplateEngine());


		post("/principal", (rq, rs) -> {
			Map map = new HashMap();
			String currentUser = rq.session().attribute("currentUser");
			String currentGame = rq.session().attribute("currentGame");
			if (currentUser == null) {
				// Si no hay usuario registrado
				return new ModelAndView(null, "logueo.html");
			}else {
				User user = User.findFirst("id = ?", currentUser);
				String usuario = user.getString("name");
				String puntos = user.getString("score");
				String continuar =	rq.queryParams("continuar");
				map.put("usuario", usuario);
				map.put("puntos", puntos);
				if (currentGame != null) {
					map.put("estado", "Continuas jugando");
					return new ModelAndView(map, "generar.html");
				}else {
					String jugar = rq.queryParams("jugar");
					if (jugar != null) {
						map.put("estado", "Jugar un nuevo juego");
						return new ModelAndView(map, "generar.html");
					}else {
						String administrar = rq.queryParams("crearpregunta");
						if (administrar != null) {
							map.put("estado", "Para crear una pregunta completa los siguientes campos");
							return new ModelAndView(map, "crearpregunta.html");
						}else {
							String exit = rq.queryParams("exit");
							if (exit != null) {
								rq.session().removeAttribute("currentUser");
								return new ModelAndView(null, "logueo.html");
							}else {
								// Todos los usuarios ordenados de mayor a menor puntaje
								List<User> usuarios = User.findAll().limit(10).orderBy("score desc");
								for (int i = 0; i < usuarios.size(); i++) {
									String add = "user" + i;
									map.put(add, usuarios.get(i).getString("name"));
									add = "score" + i;
									map.put(add, usuarios.get(i).getString("score"));
								}
								return new ModelAndView(map, "ranking.html");								
							}
						}
					}
				}
			}
		},new MustacheTemplateEngine());

		// Muestra si la pregunta fue correcta o incorreccta y genera una nueva pregunta aleatoria
		post("/generar", (rq, rs) -> {
			Map map = new HashMap();
			String currentUser = rq.session().attribute("currentUser");
			String currentGame = rq.session().attribute("currentGame");	
			if (currentUser == null) {
				// Si no hay usuario registrado
				return new ModelAndView(null, "logueo.html");
			}else {
				User user = User.findFirst("id = ?", currentUser);
				String usuario = user.getString("name");
				String puntos = user.getString("score");
				String continuar =	rq.queryParams("continuar");
				map.put("usuario", usuario);
				map.put("puntos", puntos);
				if (user.getString("esadmin") == "false") {
					map.put("admin1", "<!--");
					map.put("admin2", "-->");
				}
				if (continuar != null) {
					Game game;
					if (currentGame == null) {
						game = new Game();
						game.set("user_id", currentUser);
						game.set("lifes", LIFES);
						game.saveIt();
					}else {
						game = Game.findFirst("id = ?", currentGame);	
					}
					currentGame = game.getString("id");
					rq.session().attribute("currentGame", currentGame);						

					if (Integer.parseInt(game.getString("lifes")) >= 0) {
						// Todas las preguntas
						List<Question> questions = Question.findAll();		
						// Obtiene una pregunta aleatoriamente
						int ran = (int)(Math.random() * questions.size());
						Question ques = questions.get(ran);						
						// carga el map
						String currentQuestion = ques.getString("id");
						String categoria = Category.findFirst("id = ?", ques.getString("category_id")).getString("name");
						String vidas = game.getString("lifes");
						String pregunta = ques.getString("question");
						map.put("categoria", categoria);
						map.put("vidas", vidas);
						map.put("pregunta", pregunta);
						map.put("rpta1", htmlizar(ques.getString("answer1")));				
						map.put("rpta2", htmlizar(ques.getString("answer2")));				
						map.put("rpta3", htmlizar(ques.getString("answer3")));				
						rq.session().attribute("currentQuestion", currentQuestion);
						return new ModelAndView(map, "jugando.html");
					}else {
						if (currentGame != null) {
							game = Game.findFirst("id = ?", currentGame);	
							game.delete();
							rq.session().removeAttribute("currentGame");
							return new ModelAndView(map, "principal.html");
						}
						return new ModelAndView(map, "principal.html");
					}
				}else {
					if (currentGame != null) {				
						Game game = Game.findFirst("id = ?", currentGame);
						game.delete();
						rq.session().removeAttribute("currentGame");
					}
					map.put("estado", "Cancelaste el juego");
					return new ModelAndView(map, "principal.html");
				}
			}
		},new MustacheTemplateEngine());

		post("/jugando", (rq, rs) -> {
			Map map = new HashMap();
			String currentUser = rq.session().attribute("currentUser");
			String currentGame = rq.session().attribute("currentGame");
			String currentQuestion = rq.session().attribute("currentQuestion");		
			if (currentUser == null || currentGame == null || currentQuestion == null) {
				return new ModelAndView(null, "logueo.html");
			}else {
				Question ques = Question.findFirst("id = ?", currentQuestion);
				User userp = User.findFirst("id = ?", currentUser);
				Game game = Game.findFirst("id = ?", currentGame);;
				String userAns = rq.queryParams("ans");
				String correctAns = ques.getString("answer" + (ques.getString("correct")));				
				if (userAns.equals(htmlizar(correctAns))) {
					map.put("estado", "Respuesta correcta");
					map.put("estado1", ques.getString("question"));
					map.put("estado2", correctAns);	
					int score = Integer.parseInt(userp.getString("score")) + 1;
					userp.set("score", score).saveIt();
				}else {
					map.put("estado", "Respuesta incorreccta");
					map.put("estado1", "Tu respuesta: " + userAns);
					map.put("estado2", "Correcta: " + correctAns);					
					int lifes = Integer.parseInt(game.getString("lifes")) - 1;
					game.set("lifes", lifes).saveIt();
				}
				return new ModelAndView(map, "generar.html");
			}
		},new MustacheTemplateEngine());

		post("/ranking",(rq, rs) -> {
			Map map = new HashMap();
			String currentUser = rq.session().attribute("currentUser"); 
			if (currentUser == null) {
				return new ModelAndView(null, "logueo.html");
			}else {
				User user = User.findFirst("id = ?", currentUser);
				String usuario = user.getString("name");
				String puntos = user.getString("score");
				map.put("usuario", usuario);
				map.put("puntos", puntos);
				if (rq.queryParams("atras") != null) {
					if (user.getString("esadmin") == "false") {
						map.put("admin1", "<!--");
						map.put("admin2", "-->");
					}
					return new ModelAndView(map, "principal.html");
				}else {
					return new ModelAndView(map, "ranking.html");
				}
			}
		},new MustacheTemplateEngine());


		post("/crearpregunta",(rq, rs) -> {
			Map map = new HashMap();
			String currentUser = rq.session().attribute("currentUser"); 
			if (currentUser == null) {
				return new ModelAndView(null, "logueo.html");
			}else {
				if (rq.queryParams("newquestion") != null) {
					String question = rq.queryParams("question");
					String categoria = rq.queryParams("categoria");
					String ans1 = rq.queryParams("ans1");
					String ans2 = rq.queryParams("ans2");
					String ans3 = rq.queryParams("ans3");
					String correct = rq.queryParams("correct");
					int correcto = Integer.parseInt(correct);
					if (question != null && categoria != null && ans1 != null && ans2 != null && ans3 != null && correct != null && correcto > 0 && correcto <= 3) {
						// Informacion correcta en los campos			
						Category catName = Category.findFirst("name = ?", categoria);
						Category catId = Category.findFirst("id = ?", categoria);
						if (catName != null || catId != null) {
							// Categoria correcta
							if (Question.findFirst("question = ?", question) == null) {
								// La pregunta no existe
								Category catego = catName != null ? catName : catId;
								Question ques = new Question();
								ques.set("question", question);
								ques.set("category_id", catego.getString("id"));
								ques.set("answer1", ans1);
								ques.set("answer2", ans2);
								ques.set("answer3", ans3);
								ques.set("correct", correct);
								ques.saveIt();
								map.put("estado", "La pregunta " + ques.getString("id") + " se creo correctamente");								
							}else {
								// La pregunta ya existe
								map.put("estado", "Error: La pregunta ya existe");
							}							
						}else {
							// Categoria incorrecta
							map.put("estado", "Error: La categoria no existe");								
						}							
					}else {
						// Informacion invalida en los campos
						map.put("estado", "Error: Informacion invalida en los campos");
					}
					return new ModelAndView(map, "crearpregunta.html");
				}else {
					map.put("usuario", User.findFirst("id = ?", currentUser).getString("name"));
					map.put("puntos", User.findFirst("id = ?", currentUser).getString("score"));					
					return new ModelAndView(map, "principal.html");

				}
			}
		},new MustacheTemplateEngine());
	}
}