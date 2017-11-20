package trivia;
import static spark.Spark.*;
import trivia.Category;
import trivia.Game;
import trivia.Question;
import trivia.User;
import org.javalite.activejdbc.Base;

import java.util.*;

import  java.text.DateFormat;
import  java.text.SimpleDateFormat;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

public class App {

	static private final int CATEGORIES = 6; 		// Cantidad de categorias
	static private final String LIFES = "3"; 		// Vidas al iniciar un juego
	static public final int LIFECOST = 50;		// Precio de una vida
	static private final String driverdb = "com.mysql.jdbc.Driver";
	static private final String basedb = "jdbc:mysql://localhost/trivia";
	static private final String userdb = "root";
	static private final String passworddb = "root";

    // Para webSocket <SessionWS, Nombre>
    static private Map<Session, String> usernames = new ConcurrentHashMap<>();
    // Usuarios esperando la respuesta del oponente
    static private Queue<Session> suspenseSet = new ConcurrentLinkedQueue<>();

    // Elimina un usuario de la lista de usuarios en modo duelo
    public static void removeUser(Session user) {
    	usernames.remove(user);
    	suspenseSet.remove(user);
    }   

    // Envia a los usuarios la instruccion de actualizarse
    public static void update() {
		Base.open(driverdb, basedb, userdb, passworddb);		
    	usernames.keySet().stream().filter(Session::isOpen).forEach(session -> {
    	    try {
    	        session.getRemote().sendString(String.valueOf(new JSONObject()
    	            .put("type", "Actualizar")
    	            .put("userlist", usernames.values())
    	            .put("duelist", Duel.desafiants(usernames.get(session)))
    	        ));
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	    }
    	});
		Base.close();
    }

    // Envia a los usuarios esperando la respuesta la instruccion de actualizacion
    public static void updateSuspense() {
    	suspenseSet.stream().filter(Session::isOpen).forEach(session -> {
    	    try {
    	        session.getRemote().sendString(String.valueOf(new JSONObject()
    	            .put("type", "Jugar")
    	        ));
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	    }
    	});
    }

    private static void sendError(Session user, String message) {
    	try {
    	    user.getRemote().sendString(String.valueOf(new JSONObject()
    	        .put("type", "Error")
    	        .put("message", message)
    	    ));
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
    }    

    // Interpreta el mensaje recibido y realiza la accion correspondiente
    public static void manageMessage(Session sender, String message) {
    	JSONObject obj = new JSONObject(message);
    	String type = new String(obj.getString("type"));
		if (type.equals("Entrar")) {
			String sendername = new String(obj.getString("sendername"));
	    	usernames.put(sender, sendername);
	    	update();	
	    }else if (type.equals("Desafiar")) {
			Base.open(driverdb, basedb, userdb, passworddb);		
    		String sendername = usernames.get(sender);
			User user = User.findFirst("name = ?", sendername);
    		int score = obj.getInt("score");
			if (user.lives() <= 0 || user.score() < score) {
				String aux = (user.lives() <= 0)? "vidas" : "puntos";
				sendError(sender, "No tiene " + aux + " suficientes");
			} else {	
	    		String op = new String(obj.getString("opponentname"));
		    	if (!(op.equals(sendername) || Duel.exist(sendername, op))) {
		    		// Si no se esta "autodesafiando" y el desafio no existe
					sendError(sender, "Desafiaste a " + op + " el juego aparecera en la lista cuando sea tu turno");
					user.reduceLive();
					user.updateScore(-score);
		    		Duel duel = new Duel(sendername, op, score);
		    		duel.saveIt();
					Base.close();
		    		update();
					Base.open(driverdb, basedb, userdb, passworddb);		
		    	}else {
		    		String e = (op.equals(sendername)) ? "No puedes desafiarte" : "Ya existe el duelo";
	    			sendError(sender, "Error: " + e);
		    	}
			}
			Base.close();
	    }else if (type.equals("Esperar")) {
	    	suspenseSet.add(sender);
	    }
    }

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
		webSocket("/duelo", Pregunta2WebSocketHandler.class);
		init();
		
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
			String currentUser = rq.session().attribute("currentUser");
			if (currentUser != null) {
				// Si hay usuario registrado
				User user = User.findFirst("id = ?", currentUser);
				String usuario = user.getString("name");
				String puntos = user.getString("score");
				map.put("usuario", usuario);
				map.put("puntos", puntos);
				String vidas = user.getString("globalLives");
				map.put("vidas", vidas);
				return new ModelAndView(map, "principal.html");
			}else {
				// Si no hay usuario registrado
				map.put("estado", "Bienvenido a Pregunta2");
				return new ModelAndView(map, "logueo.html");
			}
		},new MustacheTemplateEngine());

		get("/espera", (rq, rs) -> {
			Map map = new HashMap();
			String currentUser = rq.session().attribute("currentUser");
			if (currentUser != null) {
				// Si hay usuario registrado
				User user = User.findFirst("id = ?", currentUser);
				String usuario = user.getString("name");
				map.put("usuario", usuario);
				return new ModelAndView(map, "espera.html");
			}else {
				// Si no hay usuario registrado
				return new ModelAndView(null, "logueo.html");
			}
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
			if (currentUser == null) {
				// Si no hay usuario registrado
				return new ModelAndView(null, "logueo.html");
			}else {
				User user = User.findFirst("id = ?", currentUser);
				String usuario = user.getString("name");
				String puntos = user.getString("score");
				map.put("usuario", usuario);
				map.put("puntos", puntos);
				DateFormat dateFormat = new SimpleDateFormat("dd");
				Date lastupdateDate = user.getDate("lastupdate");
				Date currentDate = new Date();
				if (!(dateFormat.format(lastupdateDate)).equals(dateFormat.format(currentDate))) {
					user.updateLives(-user.lives());
					user.updateLives(5);
					user.set("lastupdate", user.getDate("updated_at"));
					user.saveIt();
				}
				if (rq.queryParams("comprarvida") != null) {
					if (user.score() >= LIFECOST) {
						user.updateScore(-LIFECOST);
						user.updateLives(1);
						map.put("estado", "Compraste una vida por " + LIFECOST + " puntos");
					}else {
						map.put("estado", "No tienes puntos suficientes, necesitas " + LIFECOST);
					}
					user = User.findFirst("id = ?", currentUser);
					String vidas = user.getString("globalLives");
					puntos = user.getString("score");
					map.put("puntos", puntos);
					map.put("vidas", vidas);
					return new ModelAndView(map, "principal.html");							
				}else if (rq.queryParams("crearpregunta") != null) {
					map.put("estado", "Para crear una pregunta completa los siguientes campos");
					return new ModelAndView(map, "crearpregunta.html");
				}else {
					// String exit = rq.queryParams("exit");
					if (rq.queryParams("exit") != null) {
						rq.session().removeAttribute("currentUser");
						return new ModelAndView(null, "logueo.html");
					}else {
						return new ModelAndView(map, "principal.html");
					}
				}
			}
		},new MustacheTemplateEngine());

		// Muestra la pregunta
		post("/jugando", (rq, rs) -> {
			Map map = new HashMap();
			String currentUser = rq.session().attribute("currentUser");
			if (currentUser != null) {
				// Si hay usuario registrado
				User user = User.findById(Integer.parseInt(currentUser));
				String username = user.getString("name");
				map.put("usuario", username);
				map.put("puntos", user.score());
				map.put("vidas", user.lives());
				String comun = rq.queryParams("comun");
				String duelo = rq.queryParams("duelo");
				String rptaComun = rq.queryParams("anscomun");
				String rptaDuelo = rq.queryParams("ansduelo");
				String timeComun = rq.queryParams("timecomun");
				String timeDuelo = rq.queryParams("timeduelo");
				if (comun != null) {
					// Si es modo de juego Comun
					Game game = Game.getFirst(currentUser);
					map.put("tipo", "comun");
					if (comun.equals("Jugar")) {
						// Click en jugar
						if (game != null) {
							// Si ya estaba en un juego Comun
							map.put("estado","Continuas jugando");								
						}else {
							// Si no estaba en un juego Comun
							map.put("estado","Jugar un nuevo juego");
						}
						return new ModelAndView(map,"generar.html");
					}else if (comun.equals("Continuar")) {
						// click en continuar
						if (game == null) {
							if (user.lives() > 0) {
								// Si tiene vidas suficientes (globales)
								user.reduceLive();
								game = new Game(currentUser, LIFES);								
							}else {
								map.put("estado", "No tienes vidas suficientes");
								return new ModelAndView(map, "principal.html");								
							}
						}
						if (game.lifes() >= 0) {
							Question ques = generarPreguntaAleatoria();
							Integer category = ques.getInteger("category_id");
							Category cat = Category.findById(category);
							rq.session().attribute("currentQuestion", ques.getString("id"));
							map.put("pregunta" ,ques.getString("question"));
							map.put("rpta1", ques.getString("answer1").replace(' ', '_'));
							map.put("rpta2", ques.getString("answer2").replace(' ', '_'));
							map.put("rpta3", ques.getString("answer3").replace(' ', '_'));
							map.put("vidas", game.lifes());
							map.put("categoria", cat.getString("name"));
							return new ModelAndView(map,"jugando.html");							
						}else {
							game.delete();
							map.put("estado", "Termino el juego");
							return new ModelAndView(map, "principal.html");	
						}
					}else if (comun.equals("Abandonar")) {
						// click en abandonar
						if (game != null) {
							game.delete();
						}
						map.put("estado", "Cancelaste el juego");
						map.put("vidas", user.lives());
						return new ModelAndView(map,"principal.html");	
					}
				}else if (duelo != null) {
					// Si es modo de juego Duelo
					map.put("tipo", "duelo");
					String jugar = duelo.substring(0,5);
					if (jugar.equals("Jugar")) {
						// Click en jugar
						String opponentname = duelo.substring(9);
						map.put("estado","Duelo " + username + " vs " + opponentname);
						Duel duelTurno = Duel.getFirst(opponentname, username);
						rq.session().attribute("currentDuel", duelTurno.getString("id"));
						String score1 = duelTurno.getString("corrects2");
						String score2 = duelTurno.getString("corrects1");
						map.put("estado1", "Resultado parcial: " + score1 + " - " + score2);
						return new ModelAndView(map,"generar.html");
					}else if (duelo.equals("Continuar")) {
						// click en continuar
						String currentDuel = rq.session().attribute("currentDuel");
						Duel duel = Duel.getFirst(Integer.parseInt(currentDuel));
						if (duel == null) {
							// El juego no existe
							map.put("estado","El duelo ya termino");
							return new ModelAndView(map,"principal.html");
						}else {
							if (duel.questionNumber() >= 12) {
								Integer score1 = duel.getInteger("corrects1");
								Integer score2 = duel.getInteger("corrects2");
								Integer scoreDuel = duel.getInteger("score");
								String user1 = duel.getString("user1");
								String user2 = duel.getString("user2");
								String result;
								if (score1 == score2) {
									result = "Empate";
								}else {
									String winnerName = (score1 > score2) ? user1 : user2;
									result = "Ganador " + winnerName;
								}
								map.put("estado1", user1 +  " VS " + user2);
								map.put("estado","Duelo terminado");
								map.put("estado",result);
								map.put("estado2", "Resultado final: " + score1 + " - " + score2);
								return new ModelAndView(map,"generar.html");
							}else {
							 	String opponentname = duel.getString("user2");
								if (opponentname.equals(username)) {
									Integer qn = duel.getInteger("questionNumber");
									qn = (qn % 2 == 0) ? (qn / 2) : ((qn - 1) / 2);
									Question ques = generarPreguntaAleatoria(qn);
									Integer category = ques.getInteger("category_id");
									Category cat = Category.findById(category);
									rq.session().attribute("currentQuestion", ques.getString("id"));
									map.put("pregunta" ,ques.getString("question"));
									map.put("rpta1", ques.getString("answer1").replace(' ', '_'));
									map.put("rpta2", ques.getString("answer2").replace(' ', '_'));
									map.put("rpta3", ques.getString("answer3").replace(' ', '_'));
									map.put("categoria", cat.getString("name"));
									return new ModelAndView(map,"jugando.html");							
							 	}else {					 		
									map.put("estado","Duelo " + username + " vs " + opponentname);
									String scr1 = duel.getString("corrects1");
									String scr2 = duel.getString("corrects2");
									map.put("estado1", "Resultado parcial: " + scr1 + " - " + scr2);
									map.put("estado2","Es el turno de " + opponentname);
									return new ModelAndView(map,"generar.html");
							 	}
							}
						}
					}else if (duelo.equals("Abandonar")) {
						String currentDuel = rq.session().attribute("currentDuel");
						Duel duel = Duel.getFirst(Integer.parseInt(currentDuel));	
						if (duel == null) {
							// El juego no existe
							map.put("estado","El duelo ya termino");
							return new ModelAndView(map,"principal.html");
						}else {
							if (duel.questionNumber() < 12) {
								String user1 = duel.getString("user1");
								String user2 = duel.getString("user2");
								String userScoreName = (username.equals(user1)) ? user2 : user1;
								User userScore = User.findFirst("name = ?", userScoreName);								
							}
							duel.delete();
							map.put("estado","Duelo eliminado");
							return new ModelAndView(map,"principal.html");
						}			
					}
				}else{
					String currentQuestion = rq.session().attribute("currentQuestion");
					Question ques = Question.findFirst("id = ?", currentQuestion);
					String correctAns = ques.getString("answer" + (ques.getString("correct")));
					String currentDuel = rq.session().attribute("currentDuel");
					Game game = Game.getFirst(currentUser);
					if (rptaComun != null || rptaDuelo != null) {
						// Si respondio
						String rpta = (rptaComun != null) ? rptaComun : rptaDuelo;
						if (rpta.equals(correctAns.replace(' ', '_'))) {
							// Correcto
							map.put("estado", "Respuesta correcta");
							map.put("estado1", ques.getString("question"));
							map.put("estado2", correctAns);	
							if (rptaComun != null) {
								// Comun
								user.updateScore(1);
								map.put("tipo", "comun");
							}else {
								// Duelo
								Duel duel = Duel.getFirst(Integer.parseInt(currentDuel));
								duel.correct(username);
								if (duel.questionNumber() == 0) {
									Integer scoreDuel = duel.getInteger("score");
									if (user.lives() <= 0 || user.score() < scoreDuel) {
										map.put("estado", "No tienes puntos o vidas suficientes");
										return new ModelAndView(map,"principal.html");										
									}else {
										user.reduceLive();
										user.updateScore(-scoreDuel);
									}
								}
								duel.changeTurn();
								map.put("tipo", "duelo");
								Base.close();
								update();
								Base.open(driverdb, basedb, userdb, passworddb);	
								updateSuspense();
								String opponentname = duel.getString("user2");
								if (duel.questionNumber() >= 12) {
									Integer score1 = duel.getInteger("corrects1");
									Integer score2 = duel.getInteger("corrects2");
									Integer scoreDuel = duel.getInteger("score");
									String user1 = duel.getString("user1");
									String result;
									if (score1 == score2) {
										result = "Empate";
									}else {
										String winnerName = (score1 > score2) ? user1 : opponentname;
										result = "Ganador " + winnerName;
										User winner = User.findFirst("name = ?", winnerName);
										winner.updateScore(scoreDuel);
									}
									map.put("estado1", user1 +  " VS " + opponentname);
									map.put("estado","Duelo terminado");
									map.put("estado",result);
									map.put("estado2", "Resultado final: " + score1 + " - " + score2);
								}else {
									map.put("estado3", "Es el turno de " + opponentname);
								}
							}
						}else {
							// Incorrecto
							map.put("estado", "Respuesta incorreccta");
							map.put("estado1", "Tu respuesta: " + rpta);
							map.put("estado2", "Correcta: " + correctAns);	
							if (rptaComun != null) {
								// Comun
								game.reduceLife();
								map.put("tipo", "comun");
							}else {
								// Duelo
								Duel duel = Duel.getFirst(Integer.parseInt(currentDuel));
								if (duel.questionNumber() == 0) {
									Integer scoreDuel = duel.getInteger("score");
									if (user.lives() <= 0 || user.score() < scoreDuel) {
										map.put("estado", "No tienes puntos o vidas suficientes");
										return new ModelAndView(map,"principal.html");										
									}else {
										user.reduceLive();
										user.updateScore(-scoreDuel);
									}
								}
								duel.changeTurn();
								map.put("tipo", "duelo");
								Base.close();
								update();
								Base.open(driverdb, basedb, userdb, passworddb);	
								updateSuspense();
								String opponentname = duel.getString("user2");								
								if (duel.questionNumber() >= 12) {
									Integer score1 = duel.getInteger("corrects1");
									Integer score2 = duel.getInteger("corrects2");
									Integer scoreDuel = duel.getInteger("score");
									String user1 = duel.getString("user1");
									String result;
									if (score1 == score2) {
										result = "Empate";
									}else {
										String winnerName = (score1 > score2) ? user1 : opponentname;
										result = "Ganador " + winnerName;
										User winner = User.findFirst("name = ?", winnerName);
										winner.updateScore(scoreDuel);
									}
									map.put("estado1", user1 +  " VS " + opponentname);
									map.put("estado","Duelo terminado");
									map.put("estado",result);
									map.put("estado2", "Resultado final: " + score1 + " - " + score2);									
								}else {
									map.put("estado3", "Es el turno de " + opponentname);	
								}
							}				
						}
						return new ModelAndView(map, "generar.html");
					}else if (timeComun != null || timeDuelo != null) {
						// Si agoto el tiempo de respuesta
						map.put("estado", "Se acabo el tiempo de respuesta");
						map.put("estado2", "Correcta: " + correctAns);
						if (timeComun != null) {
							// Comun
							game.reduceLife();
							map.put("tipo", "comun");
						}else {
							// Duelo
							Duel duel = Duel.getFirst(Integer.parseInt(currentDuel));
							if (duel.questionNumber() == 0) {
								Integer scoreDuel = duel.getInteger("score");
								if (user.lives() <= 0 || user.score() < scoreDuel) {
									map.put("estado", "No tienes puntos o vidas suficientes");
									return new ModelAndView(map,"principal.html");										
								}else {
									user.reduceLive();
									user.updateScore(-scoreDuel);
								}
							}
							duel.changeTurn();
							map.put("tipo", "duelo");
							Base.close();
							update();
							Base.open(driverdb, basedb, userdb, passworddb);
							updateSuspense();
							String opponentname = duel.getString("user2");
							if (duel.questionNumber() >= 12) {
								Integer score1 = duel.getInteger("corrects1");
								Integer score2 = duel.getInteger("corrects2");
								Integer scoreDuel = duel.getInteger("score");
								String user1 = duel.getString("user1");
								String result;
								if (score1 == score2) {
									result = "Empate";
								}else {
									String winnerName = (score1 > score2) ? user1 : opponentname;
									result = "Ganador " + winnerName;
									User winner = User.findFirst("name = ?", winnerName);
									winner.updateScore(scoreDuel);
								}
								map.put("estado1", user1 +  " VS " + opponentname);
								map.put("estado","Duelo terminado");
								map.put("estado",result);
								map.put("estado2", "Resultado final: " + score1 + " - " + score2);									
							}else {
								map.put("estado3", "Es el turno de " + opponentname);	
							}
						}
						return new ModelAndView(map, "generar.html");
					}
				}
			}
			return new ModelAndView(null, "logueo.html");
		},new MustacheTemplateEngine());

		// Muestra los 10 jugadores con mayor puntaje
		get("/ranking",(rq, rs) -> {
			Map map = new HashMap();
			String currentUser = rq.session().attribute("currentUser");
			if (currentUser != null) {
				// Si hay usuario registrado
				List<User> usuarios = User.findAll().limit(10).orderBy("score desc");
				for (int i = 0; i < usuarios.size(); i++) {
					String add = "user" + i;
					map.put(add, usuarios.get(i).getString("name"));
					add = "score" + i;
					map.put(add, usuarios.get(i).getString("score"));
				}
				return new ModelAndView(map, "ranking.html");
			}else {
				// Si no hay usuario registrado
				map.put("estado", "Bienvenido a Pregunta2");
				return new ModelAndView(map, "logueo.html");
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