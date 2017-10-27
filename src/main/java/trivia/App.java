package trivia;
import static spark.Spark.*;
import trivia.Category;
import trivia.Game;
import trivia.Question;
import trivia.User;
import org.javalite.activejdbc.Base;

import java.util.*;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

public class App {

	static private final int CATEGORIES = 6; 		// Cantidad de categorias
	static private final String LIFES = "3"; 		// Vidas al iniciar un juego
	static private final String driverdb = "com.mysql.jdbc.Driver";
	static private final String basedb = "jdbc:mysql://localhost/trivia";
	static private final String userdb = "root";
	static private final String passworddb = "root";
     
	// Obtiene una pregunta aleatoria de la n-esesima categoria
	private static Question generarPreguntaAleatoria(int n) {
		String idcat = (Category.where("id >= ?", "0").get(n)).getString("id");
		List<Question> questions = Question.where("category_id = ?", idcat);
		Random ran = new Random();
		return questions.get(ran.nextInt(questions.size()));
	}

	// Obtiene una pregunta aleatoria
	private static Question generarPreguntaAleatoria() {
		Random ran = new Random();
		return  generarPreguntaAleatoria(ran.nextInt(CATEGORIES));
	}

	private static  boolean isValid(String s) {
		return s != null && s.length() > 6;
	}

	public static void main(String[] args) {
		staticFileLocation("/public");
		
		Base.open(driverdb, basedb, userdb, passworddb);		
		if (Category.count() == 0) {
			Inicializacion.inicializar(driverdb, basedb, userdb, passworddb);	
		}
		Base.close();

		before((rq, rs) -> {
			Base.open(driverdb, basedb, userdb, passworddb);
		});

		after((rq, rs) -> {
			Base.close();
		});

		// Principal
		get("/", (rq, rs) -> {
			Map map = new HashMap();
			map.put("estado", "Bienvenido a Pregunta2");
			return new ModelAndView(map, "logueo.html");
		},new MustacheTemplateEngine());

		// Inicio de secion y registro de nuevo usuario
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
				String vidas = user.getString("globalLives");
				map.put("vidas", vidas);
				return new ModelAndView(map, "principal.html");
			}else {
				String entrar = rq.queryParams("entrar");
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
						String vidas = user.getString("globalLives");
						map.put("vidas", vidas);
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
						// Si no hay un usuario registrado con el mismo nombre
						if (isValid(nombrer) && isValid(password2r)) {
							if (passwordr.equals(password2r)) {
								User usr = new User(nombrer, passwordr);
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
	
		// Menu principal
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
				String continuar = rq.queryParams("continuar");
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
				String continuar = rq.queryParams("continuar");
				map.put("usuario", usuario);
				map.put("puntos", puntos);
				String vidas = user.getString("globalLives");
				map.put("vidas", vidas);
				if (continuar != null) {
					Game game;
					if (currentGame == null) {
						game = new Game(currentUser, LIFES);
						if (user.lives() <= 0) {
							map.put("estado", "No tienes vidas suficientes para iniciar el juego");
							return new ModelAndView(map, "principal.html");
						}else {
							user.reduceLive();
						}
					}else {
						game = Game.findFirst("id = ?", currentGame);	
					}
					currentGame = game.getString("id");
					rq.session().attribute("currentGame", currentGame);
					if (Integer.parseInt(game.getString("lifes")) >= 0) {
						Question ques = generarPreguntaAleatoria();
						String currentQuestion = ques.getString("id");
						String categoria = Category.findFirst("id = ?", ques.getString("category_id")).getString("name");
						String gamelife = game.getString("lifes");
						String pregunta = ques.getString("question");
						String incorrect = ("1" == ques.getString("correct")) ? "2" :  "1";
						map.put("categoria", categoria);
						map.put("vidas", gamelife);
						map.put("pregunta", pregunta);
						map.put("rpta1", ques.getString("answer1").replace(' ', '_'));				
						map.put("rpta2", ques.getString("answer2").replace(' ', '_'));
						map.put("rpta3", ques.getString("answer3").replace(' ', '_'));
						map.put("incorrect", incorrect);
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

		// Muestra la pregunta
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
				if (rq.queryParams("time") != null) {
					map.put("estado", "Se acabo el tiempo de respuesta");
					map.put("estado2", "Correcta: " + correctAns);					
					game.reduceLife();
				}else {
					if (userAns.equals(correctAns.replace(' ', '_'))) {
						map.put("estado", "Respuesta correcta");
						map.put("estado1", ques.getString("question"));
						map.put("estado2", correctAns);	
						userp.updateScore(1);
					}else {
						map.put("estado", "Respuesta incorreccta");
						map.put("estado1", "Tu respuesta: " + userAns);
						map.put("estado2", "Correcta: " + correctAns);					
						game.reduceLife();
					}					
				}
				return new ModelAndView(map, "generar.html");
			}
		},new MustacheTemplateEngine());

		// Muestra los 10 jugadores con mayor puntaje
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
				String vidas = user.getString("globalLives");
				map.put("vidas", vidas);
				if (rq.queryParams("atras") != null) {					
					return new ModelAndView(map, "principal.html");
				}else {
					return new ModelAndView(map, "ranking.html");
				}
			}
		},new MustacheTemplateEngine());

		// Permite al usuario crear una nueva pregunta
		post("/crearpregunta",(rq, rs) -> {
			Map map = new HashMap();
			String currentUser = rq.session().attribute("currentUser"); 
			if (currentUser == null) {
				return new ModelAndView(null, "logueo.html");
			}else {
				User user = User.findFirst("id = ?", currentUser);
				String vidas = user.getString("globalLives");
				map.put("usuario", user.getString("name"));
				map.put("puntos", user.getString("score"));
				map.put("vidas", vidas);
				if (rq.queryParams("newquestion") != null) {
					String question = rq.queryParams("question");
					String categoria = rq.queryParams("categoria");
					String ans1 = rq.queryParams("ans1");
					String ans2 = rq.queryParams("ans2");
					String ans3 = rq.queryParams("ans3");
					String correct = rq.queryParams("correct");					
					int correcto = Integer.parseInt(correct);
					if (correct != null && correct.compareTo("1") >= 0 && correct.compareTo("3") <= 0 &&question != null && categoria != null && ans1 != null && ans2 != null && ans3 != null) {
						// Informacion correcta en los campos			
						Category catName = Category.findFirst("name = ?", categoria);
						Category catId = Category.findFirst("id = ?", categoria);
						if (catName != null || catId != null) {
							// Categoria correcta
							if (Question.findFirst("question = ?", question) == null) {
								// La pregunta no existe
								Category catego = catName != null ? catName : catId;
								Question ques = new Question(catego.getString("id"), question, ans1, ans2, ans3, correct);
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
					return new ModelAndView(map, "principal.html");
				}
			}
		},new MustacheTemplateEngine());
	}
}