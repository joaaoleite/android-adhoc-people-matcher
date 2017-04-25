package pt.ulisboa.tecnico.cmu.controllers;

import pt.ulisboa.tecnico.cmu.models.Model;
import pt.ulisboa.tecnico.cmu.server.Response;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashSet;


public abstract class Controller extends HashMap<String, Model>{

    public static JSONObject ModelSetToJSON(HashSet<? extends Model> set, String arrayName){
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        for(Model item : set)
			array.put(item.toJSON());
        obj.put(arrayName, array);
        obj.put("status", "ok");
        return obj;
    }

    public static JSONObject mapToJSON(HashMap<String, String> map, String arrayName){
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        for(HashMap.Entry<String, String> entry : map.entrySet()){
            JSONObject ref = new JSONObject();
            ref.put(entry.getKey(), entry.getValue());
			array.put(ref);
        }
        obj.put(arrayName, array);
        obj.put("status", "ok");
        return obj;
    }

    public static JSONObject setToJSON(HashSet<String> set, String arrayName){
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        for(String item : set){
            JSONObject ref = new JSONObject();
		    ref.put(arrayName.substring(0, arrayName.length() - 1), item);
            array.put(ref);
		}
        obj.put(arrayName, array);
        obj.put("status", "ok");
        return obj;
    }
}
