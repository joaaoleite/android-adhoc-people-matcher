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
    //QUERY CODE
    /*HashMap<String, String> map = new HashMap<String, String>();
    if(request.queryString() != null){
        String[] split = request.queryString().split("&");
        for(String query : split) map.put(query.split("=")[0], query.split("=")[1]);
    }*/

    public void GET(String endpoint, BiFunction<Map<String, String>, Session, JSONObject> done){
        get(endpoint, (request, response) -> {
			System.out.println("HTTP GET: " + endpoint);
			response.type("application/json");
            return done.apply(request.params(), request.session());
        });
    }

    public void POST(String endpoint, BiFunction<QueryParamsMap, Session, JSONObject> done){
        post(endpoint, (request, response) -> {
			System.out.println("HTTP POST: " + endpoint);
			response.type("application/json");
            return done.apply(request.queryMap(), request.session());
        });
    }

    public void PUT(String endpoint, BiFunction<QueryParamsMap, Session, JSONObject> done){
        put(endpoint, (request, response) -> {
			System.out.println("HTTP PUT: " + endpoint);
			response.type("application/json");
            return done.apply(request.queryMap(), request.session());
        });
    }

    public void DELETE(String endpoint, BiFunction<Map<String, String>, Session, JSONObject> done){
        delete(endpoint, (request, response) -> {
			System.out.println("HTTP DELETE: " + endpoint);
			response.type("application/json");
            return done.apply(request.params(), request.session());
        });
    }
}
