package trivia;
import static spark.Spark.*;
import trivia.Category;
import trivia.Game;
import trivia.Question;
import trivia.User;
import org.javalite.activejdbc.Base;

class Inicializacion {

	static void inicializar(String driverdb , String basedb , String userdb , String passworddb) {
		
		//Categoria Geografia

		Category cat1 = new Category();
		if (Category.findFirst("name = ?", "Geografia") == null) {            
			cat1.set("name", "Geografia");
			cat1.saveIt();        
		}else {
			cat1 = Category.findFirst("name = ?", "Geografia");
		}

		Question ques1 = new Question();
		ques1.set("category_id", cat1.get("id"));
		ques1.set("question", "¿En cual de los siguientes paises NO hay desierto?");
		ques1.set("answer1", "España");
		ques1.set("answer2", "Alemania");
		ques1.set("answer3", "Mongolia");
		ques1.set("correct", 2);
		ques1.saveIt();			

		Question ques2 = new Question();
		ques2.set("category_id", cat1.get("id"));
		ques2.set("question", " ¿Cuál de estas características NO pertenece al clima Mediterráneo?");
		ques2.set("answer1", "Veranos secos y calurosos");
		ques2.set("answer2", "Variables temperaturas en Primavera");
		ques2.set("answer3", "Lluvias muy abundantes");
		ques2.set("correct", 3);
		ques2.saveIt();

		Question ques3 = new Question();

		ques3.set("category_id", cat1.get("id"));
		ques3.set("question", " ¿Con cuantos paises limita Argentina?");
		ques3.set("answer1", "4");
		ques3.set("answer2", "5");
		ques3.set("answer3", "6");
		ques3.set("correct", 2);
		ques3.saveIt();

		Question ques4 = new Question();
		ques4.set("category_id", cat1.get("id"));
		ques4.set("question", " ¿Que es la UA?");
		ques4.set("answer1", "Union Americana");
		ques4.set("answer2", "Union Africana");
		ques4.set("answer3", "Union Afroamericana");
		ques4.set("correct", 2);
		ques4.saveIt();

		Question ques5 = new Question();
		ques5.set("category_id", cat1.get("id"));
		ques5.set("question", " ¿Que es la UA?");
		ques5.set("answer1", "Union Americana");
		ques5.set("answer2", "Union Africana");
		ques5.set("answer3", "Union Afroamericana");
		ques5.set("correct", 2);
		ques5.saveIt();			
		//------------------------------------------------------------------------------------------------

		//Categoria Entretenimiento 

		Category cat2 = new Category();
		if (Category.findFirst("name = ?", "Entretenimiento") == null) {            
			cat2.set("name", "Entretenimiento");
			cat2.saveIt();        
		}else {
			cat2 = Category.findFirst("name = ?", "Entretenimiento");
		}

		Question ques6 = new Question();
		ques6.set("category_id", cat2.get("id"));
		ques6.set("question", "¿En que año se estreno la pelicula de Disney \'Pinocho\'?");
		ques6.set("answer1", "1940");
		ques6.set("answer2", "1950");
		ques6.set("answer3", "1960");
		ques6.set("correct", 1);
		ques6.saveIt();			

		Question ques7 = new Question();
		ques7.set("category_id", cat2.get("id"));
		ques7.set("question", " ¿A quién se considera el Rey del Pop?");
		ques7.set("answer1", "Justin Bieber");
		ques7.set("answer2", "Michael Jackson");
		ques7.set("answer3", "El Pepo");
		ques7.set("correct", 2);
		ques7.saveIt();

		Question ques8 = new Question();
		ques8.set("category_id", cat2.get("id"));
		ques8.set("question", "¿Quién canta ” Vivir mi vida”?");
		ques8.set("answer1", "Enrrique Iglesias");
		ques8.set("answer2", "Cristian Castro");
		ques8.set("answer3", "Marc Anthony");
		ques8.set("correct", 3);
		ques8.saveIt();

		Question ques9 = new Question();
		ques9.set("category_id", cat2.get("id"));
		ques9.set("question", "¿Quién es Paulo Dybala?");
		ques9.set("answer1", "Un futbolista");
		ques9.set("answer2", "Un musico");
		ques9.set("answer3", "Un actor ");
		ques9.set("correct", 1);
		ques9.saveIt();

		Question ques10 = new Question();
		ques10.set("category_id", cat2.get("id"));
		ques10.set("question", "¿Quién se hizo famoso,gracias a su frase \'Que hay de nuevo viejo\'?");
		ques10.set("answer1", "Bugs bunny");
		ques10.set("answer2", "Pato Lucas");
		ques10.set("answer3", "Piolin");
		ques10.set("correct", 1);
		ques10.saveIt();			
		//------------------------------------------------------------------------------------------------
				
		//Creamos Categoria Deporte 

		Category cat3 = new Category();
		if (Category.findFirst("name = ?", "Deporte") == null) {            
			cat3.set("name", "Deporte");
			cat3.saveIt();        
		}else {
			cat3 = Category.findFirst("name = ?", "Deporte");
		}

		Question ques12 = new Question();
		ques12.set("category_id", cat3.get("id"));
		ques12.set("question", "¿Quien es LeBron James?");
		ques12.set("answer1", "Un jugador de futbol");
		ques12.set("answer2", "Un jugador de baloncesto");
		ques12.set("answer3", "Un jugador de rugby");
		ques12.set("correct", 2);
		ques12.saveIt();

		Question ques13 = new Question();
		ques13.set("category_id", cat3.get("id"));
		ques13.set("question", "¿Cual de estos deportes no forma parte de los juegos olimpicos?");
		ques13.set("answer1", "Natacion");
		ques13.set("answer2", "Baloncesto");
		ques13.set("answer3", "Rugby");
		ques13.set("correct", 3);
		ques13.saveIt();

		
		//------------------------------------------------------------------------------------------------
		
		// Categoria Arte

		Category cat4 = new Category();
		if (Category.findFirst("name = ?", "Arte") == null) {            
			cat4.set("name", "Arte");
			cat4.saveIt();        
		}else {
			cat4 = Category.findFirst("name = ?", "Arte");
		}

		Question ques16 = new Question();
		ques16.set("category_id", cat4.get("id"));
		ques16.set("question", " ¿Quién es el autor de \'El retrato de Dorian Gray\'?");
		ques16.set("answer1", "Oscar Wilde");
		ques16.set("answer2", "Ernesto Sabato");
		ques16.set("answer3", "Jorge Luis Borges");
		ques16.set("correct", 1);
		ques16.saveIt();

		Question ques17 = new Question();
		ques17.set("category_id", cat4.get("id"));
		ques17.set("question","¿Qué describe una prosopografía?");
		ques17.set("answer1", "El carácter de una persona");
		ques17.set("answer2", "Caricaturiza a una persona");
		ques17.set("answer3", "El físico de una persona");
		ques17.set("correct", 3);
		ques17.saveIt();

		Question ques18 = new Question();
		ques18.set("category_id", cat4.get("id"));
		ques18.set("question", "Gato con guantes …");
		ques18.set("answer1", "No araña");
		ques18.set("answer2", "No caza ratones");
		ques18.set("answer3", "Y con botas");
		ques18.set("correct", 2);
		ques18.saveIt();

		Question ques19 = new Question();
		ques19.set("category_id", cat4.get("id"));
		ques19.set("question", "¿Qué odia Mafalda?");
		ques19.set("answer1", "Los panqueques");
		ques19.set("answer2", "La sopa");
		ques19.set("answer3", "A Manolito");
		ques19.set("correct", 2);
		ques19.saveIt();

		Question ques20 = new Question();
		ques20.set("category_id", cat4.get("id"));
		ques20.set("question", "¿Quién fue Antonio Lucio Vivaldi?");
		ques20.set("answer1", "Tenor de Opera");
		ques20.set("answer2", "Guitarrista Clásico");
		ques20.set("answer3", "Violinista y Compositor del Barroco");
		ques20.set("correct", 3);
		ques20.saveIt();
		//---------------------------------------------------------------------------------------------
		
		// Categoria Historia

		Category cat5 = new Category();
		if (Category.findFirst("name = ?", "Historia") == null) {            
			cat5.set("name", "Historia");
			cat5.saveIt();        
		}else {
			cat5 = Category.findFirst("name = ?", "Historia");
		}

		Question ques21 = new Question();
		ques21.set("category_id", cat5.get("id"));
		ques21.set("question", "¿En qué año tuvo lugar el ataque a Pearl Harbor?");
		ques21.set("answer1", "1951");
		ques21.set("answer2", "1941");
		ques21.set("answer3", "1939");
		ques21.set("correct", 2);
		ques21.saveIt();

		Question ques22 = new Question();
		ques22.set("category_id", cat5.get("id"));
		ques22.set("question","¿Cuál es la ciudad mas antigua de América Latina?");
		ques22.set("answer1", "Valparaíso");
		ques22.set("answer2", "Caral");
		ques22.set("answer3", "La Paz");
		ques22.set("correct", 2);
		ques22.saveIt();

		Question ques23 = new Question();
		ques23.set("category_id", cat5.get("id"));
		ques23.set("question", "El Renacimiento marcó el principio de la Edad…");
		ques23.set("answer1", "Contemporánea");
		ques23.set("answer2", "Media");
		ques23.set("answer3", "Moderna");
		ques23.set("correct", 3);
		ques23.saveIt();

		Question ques24 = new Question();
		ques24.set("category_id", cat5.get("id"));
		ques24.set("question", "¿Qué país fue orientado por Stalin?");
		ques24.set("answer1", "Union Sovietica");
		ques24.set("answer2", "Alemania");
		ques24.set("answer3", "Polonia");
		ques24.set("correct", 1);
		ques24.saveIt();

		Question ques25 = new Question();
		ques25.set("category_id", cat5.get("id"));
		ques25.set("question", "¿Quién liberó a Argentina, Chile y Perú?");
		ques25.set("answer1", "Ernesto \'Che\' Guevara");
		ques25.set("answer2", "Manual Belgrano");
		ques25.set("answer3", "San Martín");
		ques25.set("correct", 3);
		ques25.saveIt();


		// Categoria Ciencia

		Category cat6 = new Category();
		if (Category.findFirst("name = ?", "Ciencia") == null) {            
			cat6.set("name", "Ciencia");
			cat6.saveIt();        
		}else {
			cat6 = Category.findFirst("name = ?", "Ciencia");
		}

		Question ques26 = new Question();
		ques26.set("category_id", cat6.get("id"));
		ques26.set("question", "¿Cuál de las sisguientes enfermedades ataca al higado?");
		ques26.set("answer1", "Diabetes");
		ques26.set("answer2", "Cifoescoliosis");
		ques26.set("answer3", "Hepatitis");
		ques26.set("correct", 3);
		ques26.saveIt();

		Question ques27 = new Question();
		ques27.set("category_id", cat6.get("id"));
		ques27.set("question","¿Qué cambio de estado ocurre en la sublimación?");
		ques27.set("answer1", "De sólido a líquido");
		ques27.set("answer2", "De líquido a solido");
		ques27.set("answer3", "De sólido a gaseoso");
		ques27.set("correct", 3);
		ques27.saveIt();

		Question ques28 = new Question();
		ques28.set("category_id", cat6.get("id"));
		ques28.set("question", "¿Cuántas caras tiene un icosaedro?");
		ques28.set("answer1", "20");
		ques28.set("answer2", "16");
		ques28.set("answer3", "8");
		ques28.set("correct", 1);
		ques28.saveIt();

		Question ques29 = new Question();
		ques29.set("category_id", cat6.get("id"));
		ques29.set("question", "¿Donde están los meniscos?");
		ques29.set("answer1", "Las rodillas");
		ques29.set("answer2", "En las manos");
		ques29.set("answer3", "En el peroné");
		ques29.set("correct", 1);
		ques29.saveIt();

		Question ques30 = new Question();
		ques30.set("category_id", cat6.get("id"));
		ques30.set("question", "¿Cómo se llama a los electrones que se encuentran en la última capa del átomo?");
		ques30.set("answer1", "Isotopos");
		ques30.set("answer2", "Electrones de valencia");
		ques30.set("answer3", "Iones");
		ques30.set("correct", 2);
		ques30.saveIt();

	}
}