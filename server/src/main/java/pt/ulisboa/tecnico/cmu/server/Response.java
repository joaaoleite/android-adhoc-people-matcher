package pt.ulisboa.tecnico.cmu.server;

import org.json.JSONObject;

public class Response {
    public static final JSONObject OK = new JSONObject("{\"status\":\"ok\"}");
    public static final JSONObject ERROR = new JSONObject("{\"status\":\"error\"}");
    public static JSONObject createJSON(String key, String value){
        JSONObject obj = new JSONObject();
        obj.put(key, value);
        obj.put("status", "ok");
        return obj;
    }
    public static JSONObject setOK(JSONObject obj){
        obj.put("status", "ok");
        return obj;
    }

}
