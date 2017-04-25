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

    public static JSONObject mapToJSON(HashMap<String, String> map, String objName){
        JSONObject obj = new JSONObject();
        JSONObject keysObj = new JSONObject();
        for(HashMap.Entry<String, String> entry : map.entrySet())
            keysObj.put(entry.getKey(), entry.getValue());
        obj.put(objName, keysObj);
        obj.put("status", "ok");
        return obj;
    }

    public static JSONObject ComplexMapToJSON(HashMap<String, HashSet<String>> map, String arrayName){
        JSONObject obj = new JSONObject();
        JSONObject keysObj = new JSONObject();
        for(HashMap.Entry<String, HashSet<String>> keys : map.entrySet()){
            JSONArray valuesArray = new JSONArray();
            for(String value : keys.getValue()) valuesArray.put(value);
            keysObj.put(keys.getKey(), valuesArray);
        }
        obj.put(arrayName, keysObj);
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
