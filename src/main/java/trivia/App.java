package trivia;
import static spark.Spark.*;
import trivia.Category;
import trivia.Game;
import trivia.Question;
import trivia.User;
import org.javalite.activejdbc.Base;

import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;


/**
 * Hello world!
 *
 */
public class App
{

    public static void main( String[] args )
    {

      Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");


      // hello.mustache file is in resources/templates directory
      get("/hello", (rq, rs) -> { 
        Map map = new HashMap();
        map.put("name", "Sam");
        return new ModelAndView(map, "hello.mustache");},
        new MustacheTemplateEngine()
      );

      post("/bye", (rq, rs) -> { 
        String name1 = rq.queryParams("namesub1");
        String name2 = rq.queryParams("namesub2");
        Map map = new HashMap();

        map.put("name1", name1);
        map.put("name2", name2);
        System.out.println(name1);
        System.out.println(name2);

        return new ModelAndView(map, "hello.mustache");},
        new MustacheTemplateEngine()
      );

      get("/bola", (rq, rs) -> { 
        return new ModelAndView(null, "bye.mustache");},
        new MustacheTemplateEngine()
      );

      User us1 = new User();
      us1.set("name", "Nahuel");
      us1.set("password", "1234");
      us1.set("score", 0);
      us1.saveIt();

      Category cat1 = new Category();
      if (Category.findFirst("name = ?", "Ciencia") == null) {            
        cat1.set("name", "Ciencia");
        cat1.saveIt();        
      }else {
        cat1 = Category.findFirst("name = ?", "Ciencia");
      }

      Question ques1 = new Question();
      ques1.set("category_id", cat1.get("id"));
      ques1.set("question", "¿Cual es la distancia de la tierra al sol?");
      ques1.set("answer1", "149600000km");
      ques1.set("answer2", "249600000km");
      ques1.set("answer3", "349600000km");
      ques1.set("correct", 1);
      ques1.saveIt();

      Question ques2 = new Question();
      ques2.set("category_id", cat1.get("id"));
      ques2.set("question", "¿Cuando una persona es politeista?");
      ques2.set("answer1", "Vive en el polo sur");
      ques2.set("answer2", "Cree en varios dioses");
      ques2.set("answer3", "Es un policia ladron");
      ques2.set("correct", 2);
      ques2.saveIt();

      Game game1 = new Game();
      game1.set("user_id", us1.get("id"));
      game1.set("lifes", 3);
      game1.saveIt();

      System.out.println("INCORRECTA");

      int respuesta = 2;

      System.out.println("Vidas: " + game1.get("lifes"));

      if (respuesta != (Integer)ques1.get("correct")) {
        game1.set("lifes", ((Integer)game1.get("lifes") - 1));
      }

      System.out.println("Vidas: " + game1.get("lifes"));


      System.out.println("CORRECTA");

      respuesta = 1;

      System.out.println("Vidas: " + game1.get("lifes"));

      if (respuesta != (Integer)ques1.get("correct")) {
        game1.set("lifes", ((Integer)game1.get("lifes") - 1));
      }


      System.out.println("Vidas: " + game1.get("lifes"));

      Base.close();
    }
}
