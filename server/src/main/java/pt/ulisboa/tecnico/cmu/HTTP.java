package pt.ulisboa.tecnico.cmu;

public class HTTP{
    public void post(String endpoint, Callable<JSONObject> done){
        post(endpoint, (request, response) -> {
            response.type("application/json");
            System.out.println("HTTP POST: " + endpoint);


            return done(params, user);
        });
    }
}
