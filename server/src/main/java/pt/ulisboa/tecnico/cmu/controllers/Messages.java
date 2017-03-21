package pt.ulisboa.tecnico.cmu.controllers;

public class Messages extends Controller{

    public JSONObject getMessagesByUsername(String username){
        ArrayList<Message> list = new ArrayList<Message>();
        // logic ...
        return super.listToJSON(list);
    }
}
