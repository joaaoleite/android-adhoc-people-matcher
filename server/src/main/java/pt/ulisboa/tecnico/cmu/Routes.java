package pt.ulisboa.tecnico.cmu;

import pt.ulisboa.tecnico.cmu.server.*;
import pt.ulisboa.tecnico.cmu.exceptions.ExpiredSessionException;
import java.util.Map;
import java.util.HashMap;
import spark.QueryParamsMap;
import spark.Session;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Date;

public class Routes{
    private HTTP http;

    public Routes(){
        this.http = new HTTP(8080);
    }

    public void launch(){

        http.GET("/test", (Map<String, String> params, Session session) -> {
            try{
                return Response.OK;
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.POST("/signup", (QueryParamsMap params, Session session) -> {
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

        http.POST("/login", (QueryParamsMap params, Session session) -> {
            try{
                String username = params.value("username");
                String password = params.value("password");
                Database.Users().logIn(username, password);
                session.attribute("username", username);
                return Response.OK;
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.POST("/logout", (QueryParamsMap params, Session session) -> {
            try{
                if(session.attribute("username") != null){
                    session.removeAttribute("username");
                    return Response.OK;
                }
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.GET("/locations", (Map<String, String> params, Session session) -> {
            try{
                if(session.attribute("username") != null)
                    return Database.Locations().ModelSetToJSON(Database.Locations().getLocations(), "locations");
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.GET("/locations/:name", (Map<String, String> params, Session session) -> {
            try{
                if(session.attribute("username") != null)
                    return Database.Locations().getLocationByName(params.get(":name")).toJSON();
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.POST("/locations", (QueryParamsMap params, Session session) -> {
            try{
                if(session.attribute("username") != null){
                    String name = params.value("name");
                    if(!params.hasKey("ssid")){
                        double latitude = Double.parseDouble(params.value("latitude"));
                        double longitude = Double.parseDouble(params.value("longitude"));
                        int radius = Integer.parseInt(params.value("radius"));
                        Database.Locations().createLocation(name, latitude, longitude, radius);
                    }
                    else{
                        String ssid = params.value("ssid");
                        Database.Locations().createLocation(name, ssid);
                    }
                }
                else throw new ExpiredSessionException();
                return Response.OK;
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.PUT("/locations/now", (QueryParamsMap params, Session session) -> {
            try{
                if(session.attribute("username") != null){
                    String username = session.attribute("username");
                    Double latitude = Double.parseDouble(params.value("latitude"));
                    Double longitude = Double.parseDouble(params.value("longitude"));
                    HashSet<String> ssids = new HashSet<String>();
                    int i = 1;
                    String n_ssid = "ssid" + i;
                    while(params.value(n_ssid) != null){
                        ssids.add(params.value(n_ssid));
                        n_ssid = "ssid" + ++i;
                    }
                    return Database.Locations().setToJSON(Database.Locations().getLocationsNearBy(latitude, longitude, ssids));
                }
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.DELETE("/locations/:name", (Map<String, String> params, Session session) -> {
            try{
                if(session.attribute("username") != null){
                    Database.Locations().removeLocation(params.get(":name"));
                }
                else throw new ExpiredSessionException();
                return Response.OK;
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.GET("/keys", (Map<String, String> params, Session session) -> {
            try{
                if(session.attribute("username") != null){
                    return Database.Users().setToJSON(Database.Users().getGlobalKeys());
                }
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.GET("/profile", (Map<String, String> params, Session session) -> {
            try{
                if(session.attribute("username") != null){
                    String username = session.attribute("username");
                    return Database.Users().mapToJSON(Database.Users().getUserKeys(username));
                }
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.PUT("/profile", (QueryParamsMap params, Session session) -> {
            try{
                if(session.attribute("username") != null){
                    String username = session.attribute("username");
                    String key = params.value("key");
                    String value = params.value("value");
                    Database.Users().addKeyToUser(username, key, value);
                    return Response.OK;
                }
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.GET("/messages", (Map<String, String> params, Session session) -> {
            try{
                if(session.attribute("username") != null){
                    String username = session.attribute("username");
                    return Database.Messages().ModelSetToJSON(Database.Messages().getMessagesByUser(username), "messages");
                }
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.POST("/messages", (QueryParamsMap params, Session session) -> {
            try{
                if(session.attribute("username") != null){
                    String username = session.attribute("username");
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
                    Database.Messages().addMessage(username, location, policy, keys, start, end, content);
                    return Response.OK;
                }
                else throw new ExpiredSessionException();
            }
            catch(Exception e){
                return Response.ERROR;
            }
        });

        http.DELETE("/messages/:id", (Map<String, String> params, Session session) -> {
            try{
                if(session.attribute("username") != null){
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
