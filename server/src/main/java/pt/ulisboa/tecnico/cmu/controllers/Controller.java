package pt.ulisboa.tecnico.cmu.controllers;

import pt.ulisboa.tecnico.cmu.models.Model;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashSet;


public abstract class Controller extends HashMap<String, Model>{

    public static JSONObject ModelSetToJSON(HashSet<? extends Model> set){
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        for(Model item : set)
			array.put(item.toJSON());
        obj.put("array", array);
        return obj;
    }

    public static JSONObject mapToJSON(HashMap<String, String> map){
        JSONObject obj = new JSONObject();
        for(HashMap.Entry<String, String> entry : map.entrySet())
			obj.put(entry.getKey(), entry.getValue());
        return obj;
    }

    public static JSONObject setToJSON(HashSet<String> set){
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        for(String item : set){
            JSONObject ref = new JSONObject();
		    ref.put("item", item);
            array.put(ref);
		}
        obj.put("array", array);
        return obj;
    }
}
