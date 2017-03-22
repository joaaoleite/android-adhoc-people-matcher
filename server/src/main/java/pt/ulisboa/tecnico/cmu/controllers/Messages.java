package pt.ulisboa.tecnico.cmu.controllers;

import org.json.JSONObject;
import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.models.*;

public class Messages extends Controller{

    public JSONObject getMessagesByUsername(String username){
        ArrayList<Model> list = new ArrayList<Model>();
        // logic ...
        return super.listToJSON(list);
    }
}
