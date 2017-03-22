package pt.ulisboa.tecnico.cmu.server;

import org.json.JSONObject;

public class Response {
    public static final JSONObject OK = new JSONObject("{\"status\":\"ok\"}");
    public static final JSONObject ERROR = new JSONObject("{\"status\":\"error\"}");
}
