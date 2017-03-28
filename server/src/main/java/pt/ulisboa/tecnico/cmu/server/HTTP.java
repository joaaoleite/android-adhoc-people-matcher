package pt.ulisboa.tecnico.cmu.server;

import static spark.Spark.*;
import java.util.function.BiFunction;
import spark.QueryParamsMap;
import org.json.JSONObject;

public class HTTP{

    public HTTP(int port){
        port(port);
    }

    public void POST(String endpoint, BiFunction<QueryParamsMap, String, JSONObject> done){
        post(endpoint, (request, response) -> {
			System.out.println("HTTP POST: " + endpoint);
			response.type("application/json");
            request.session().attribute("username", request.queryParams("username"));
            return done.apply(request.queryMap(), request.session().attribute("username"));
        });
    }
}
