package pt.ulisboa.tecnico.cmu.server;

import static spark.Spark.*;
import java.util.function.BiFunction;
import java.util.Map;
import java.util.HashMap;
import spark.QueryParamsMap;
import spark.Session;
import org.json.JSONObject;

public class HTTP{

    public HTTP(int port){
        port(port);
    }
    
    public void GET(String endpoint, BiFunction<Map<String, String>, String, JSONObject> done){
        get(endpoint, (request, response) -> {
			System.out.println("HTTP GET: " + endpoint);
			response.type("application/json");
            String token;
            if(request.headers("Authorization") != null) token = request.headers("Authorization").split(" ")[1];
            else token = null;
            return done.apply(request.params(), token);
        });
    }

    public void POST(String endpoint, BiFunction<QueryParamsMap, String, JSONObject> done){
        post(endpoint, (request, response) -> {
			System.out.println("HTTP POST: " + endpoint);
			response.type("application/json");
            String token;
            if(request.headers("Authorization") != null) token = request.headers("Authorization").split(" ")[1];
            else token = null;
            return done.apply(request.queryMap(), token);
        });
    }

    public void PUT(String endpoint, BiFunction<QueryParamsMap, String, JSONObject> done){
        put(endpoint, (request, response) -> {
			System.out.println("HTTP PUT: " + endpoint);
			response.type("application/json");
            String token;
            if(request.headers("Authorization") != null) token = request.headers("Authorization").split(" ")[1];
            else token = null;
            return done.apply(request.queryMap(), token);
        });
    }

    public void DELETE(String endpoint, BiFunction<Map<String, String>, String, JSONObject> done){
        delete(endpoint, (request, response) -> {
			System.out.println("HTTP DELETE: " + endpoint);
			response.type("application/json");
            String token;
            if(request.headers("Authorization") != null) token = request.headers("Authorization").split(" ")[1];
            else token = null;
            return done.apply(request.params(), token);
        });
    }
}
