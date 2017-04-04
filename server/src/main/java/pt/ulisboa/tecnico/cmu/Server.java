package pt.ulisboa.tecnico.cmu;

public class Server{
    public static void main(String[] args){
        System.out.println("Server running on port 8080");
        new Routes().launch();
  }
}
