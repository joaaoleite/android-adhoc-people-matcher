package pt.ulisboa.tecnico.cmu;

import pt.ulisboa.tecnico.cmu.server.*;
import java.util.HashMap;

public class Routes{
    private HTTP http;

    public Routes(){
        this.http = new HTTP(8080);
    }

    public void launch(){
        /*http.post("/signup", (HashMap<String, String> params, String user) -> {
            try{
                String username = params.get("username");
                String password = params.get("password");
                Database.Users().create(username, password);
                return Response.OK;
            }
            catch(Exception e){
                return Reponse.ERROR;
            }
        });

        http.get("/locations", (HashMap<String, String> params, String user) -> {
            try{
                return Database.Locations().list(params.get("filter"));
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.get("/profile", (HashMap<String, String> params, String user) -> {
            try{
                return Users.getKeys(user);
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });*/
    }

}
