package pt.ulisboa.tecnico.cmu.server;

import static spark.Spark.*;
import java.util.function.BiFunction;
import spark.QueryParamsMap;
import java.util.concurrent.Callable;
import org.json.JSONObject;
import java.util.HashMap;

public class HTTP{

    public HTTP(int port){
        port(port);
    }

    public void POST(String endpoint, BiFunction<QueryParamsMap, String, JSONObject> done){
        post(endpoint, (request, response) -> {

			System.out.println("HTTP POST: " + endpoint);

			response.type("application/json");

            String body = request.body().toString();
            QueryParamsMap params = request.queryMap();

            String username = request.session().attribute("user");

            return done.apply(params, username);
        });
    }
}
