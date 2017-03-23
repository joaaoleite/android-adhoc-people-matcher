package pt.ulisboa.tecnico.cmu.controllers;

import pt.ulisboa.tecnico.cmu.models.Model;
import java.util.HashMap;
import java.util.ArrayList;
import org.json.JSONArray;


public abstract class Controller extends HashMap<String, Model>{

    public static JSONArray listToJSON(ArrayList<Model> list){
        JSONArray array = new JSONArray();
        for(Model item : list){
			array.put(item.toJSON());
		}
        array.put(array);
        return array;
    }
}
