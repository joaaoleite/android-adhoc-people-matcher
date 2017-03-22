package pt.ulisboa.tecnico.cmu.controllers;

import org.json.JSONObject;
import java.util.HashMap;
import pt.ulisboa.tecnico.cmu.models.*;
import java.util.List;

public abstract class Controller extends HashMap<String, Model>{

    public static JSONObject listToJSON(List<Model> list){
		return null;
    }
}
