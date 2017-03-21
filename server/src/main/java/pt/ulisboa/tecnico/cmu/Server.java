package pt.ulisboa.tecnico.cmu;
import static spark.Spark.*;

public class Server{
    public static void main(String[] args){
        port(8080);
        System.out.println("Server running on port 8080");
        new Routes().launch();
  }
}
