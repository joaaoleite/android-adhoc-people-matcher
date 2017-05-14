package pt.ulisboa.tecnico.cmu;

import pt.ulisboa.tecnico.cmu.server.*;
import pt.ulisboa.tecnico.cmu.exceptions.ExpiredSessionException;
import java.util.Map;
import java.util.HashMap;
import spark.QueryParamsMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Date;
import org.json.JSONObject;

public class Routes{
    private HTTP http;

    public Routes(){
        this.http = new HTTP(8080);
    }

    public void launch(){

        http.POST("/signup", (QueryParamsMap params, String token) -> {
            try{
                String username = params.value("username");
                String password = params.value("password");
                if(username == null || password == null) return Response.ERROR;
                Database.Users().createUser(username, password);
                return Response.OK;
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.POST("/login", (QueryParamsMap params, String token) -> {
            try{
                String username = params.value("username");
                String password = params.value("password");
                if(username == null || password == null) return Response.ERROR;
                JSONObject res = Response.createJSON("token", Database.Users().logIn(username, password));
                res.put("username",username);
                return res;
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.POST("/logout", (QueryParamsMap params, String token) -> {
            try{
                if(Database.Users().verifyToken(token) != null) return Response.OK;
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.GET("/locations", (Map<String, String> params, String token) -> {
            try{
                if(Database.Users().verifyToken(token) != null)
                    return Database.Locations().toJSON(Database.Locations().getLocations(), "locations");
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.GET("/locations/:name", (Map<String, String> params, String token) -> {
            try{
                if(Database.Users().verifyToken(token) != null)
                    return Response.setOK(Database.Locations().getLocationByName(params.get(":name")).toJSON());
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.POST("/locations", (QueryParamsMap params, String token) -> {
            try{
                if(Database.Users().verifyToken(token) != null){
                    String name = params.value("name");
                    if(!params.hasKey("ssid")){
                        double latitude = Double.parseDouble(params.value("latitude"));
                        double longitude = Double.parseDouble(params.value("longitude"));
                        int radius = Integer.parseInt(params.value("radius"));
                        Database.Locations().createLocation(name, latitude, longitude, radius);
                    }
                    else{
                        String ssid = params.value("ssid");
                        String mac = params.value("mac");
                        Database.Locations().createLocation(name, ssid, mac);
                    }
                }
                else throw new ExpiredSessionException();
                return Response.OK;
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.PUT("/locations/now", (QueryParamsMap params, String token) -> {
            try{
                String username = Database.Users().verifyToken(token);
                if(username != null){
                    Double latitude = Double.parseDouble(params.value("latitude"));
                    Double longitude = Double.parseDouble(params.value("longitude"));
                    HashSet<String> ssids = new HashSet<String>();
                    int i = 1;
                    String n_ssid = "ssid" + i;
                    while(params.value(n_ssid) != null){
                        ssids.add(params.value(n_ssid));
                        n_ssid = "ssid" + ++i;
                    }
                    return Database.Locations().toJSON(Database.Messages().getMatches(username, latitude, longitude, ssids), "messages");
                }
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                e.printStackTrace();
                return Response.ERROR;
            }
        });

        http.DELETE("/locations/:name", (Map<String, String> params, String token) -> {
            try{
                if(Database.Users().verifyToken(token) != null){
                    Database.Locations().removeLocation(params.get(":name"));
                }
                else throw new ExpiredSessionException();
                return Response.OK;
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.GET("/keys", (Map<String, String> params, String token) -> {
            try{
                if(Database.Users().verifyToken(token) != null)
                    return Database.Users().toJSONComplex(Database.Users().getGlobalKeys(), "keys");
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.GET("/profile", (Map<String, String> params, String token) -> {
            try{
                String username = Database.Users().verifyToken(token);
                if(username != null) return Database.Users().toJSON(Database.Users().getUserKeys(username), "keys");
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.PUT("/profile", (QueryParamsMap params, String token) -> {
            try{
                String username = Database.Users().verifyToken(token);
                if(username != null){
                    String key = params.value("key");
                    String value = params.value("value");
                    if(key == null || value == null) return Response.ERROR;
                    Database.Users().addKeyToUser(username, key, value);
                    return Response.OK;
                }
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.DELETE("/profile/:name", (Map<String, String> params, String token) -> {
            try{
                String username = Database.Users().verifyToken(token);
                if(username != null){
                    Database.Users().removeKeyFromUser(username, params.get(":name"));
                    return Response.OK;
                }
                else throw new ExpiredSessionException();

            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.GET("/messages", (Map<String, String> params, String token) -> {
            try{
                String username = Database.Users().verifyToken(token);
                if(username != null)
                    return Database.Messages().toJSON(Database.Messages().getMessagesByUser(username), "messages");
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.POST("/messages", (QueryParamsMap params, String token) -> {
            try{
                String username = Database.Users().verifyToken(token);
                if(username != null){
                    String location = params.value("location");
                    String policy = params.value("policy");
                    HashMap<String, String> keys = new HashMap<String, String>();
                    int i = 1;
                    String n_key = "key" + i;
                    while(params.value(n_key) != null){
                        keys.put(params.value(n_key), params.value("value" + i));
                        n_key = "key" + ++i;
                    }
                    Date start = new Date(Long.parseLong(params.value("start")));
                    Date end = new Date(Long.parseLong(params.value("end")));
                    String content = params.value("content");
                    if(username == null || location == null || policy == null ||
                       keys == null || start == null || end == null || content == null) return Response.ERROR;
                    Database.Messages().addMessage(username, location, policy, keys, start, end, content);
                    return Response.OK;
                }
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.DELETE("/messages/:id", (Map<String, String> params, String token) -> {
            try{
                if(Database.Users().verifyToken(token) != null){
                    Database.Messages().removeMessage(params.get(":id"));
                }
                else throw new ExpiredSessionException();
                return Response.OK;
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });



    }

}
