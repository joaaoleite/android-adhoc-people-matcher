package pt.ulisboa.tecnico.cmu;

import pt.ulisboa.tecnico.cmu.server.*;
import spark.QueryParamsMap;

public class Routes{
    private HTTP http;

    public Routes(){
        this.http = new HTTP(8080);
    }

    public void launch(){
        http.POST("/signup", (QueryParamsMap params, String user) -> {
            try{
                String username = params.value("username");
                String password = params.value("password");
                Database.Users().createUser(username, password);
                return Response.OK;
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        /*http.get("/locations", (HashMap<String, String> params, String user) -> {
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
