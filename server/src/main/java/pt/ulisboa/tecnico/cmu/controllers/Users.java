package pt.ulisboa.tecnico.cmu.controllers;

public class Users extends Controller{

    public JSONObject getUserByUsername(String username){
        return get(username).toJSON();
    }
}
