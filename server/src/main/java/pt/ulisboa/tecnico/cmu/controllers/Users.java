package pt.ulisboa.tecnico.cmu.controllers;

import pt.ulisboa.tecnico.cmu.models.User;
import pt.ulisboa.tecnico.cmu.exceptions.UserNotFoundException;
import java.util.HashMap;
import org.json.JSONObject;

public class Users extends Controller{

    public User getUserByUsername(String username) throws UserNotFoundException{
        User user = (User) get(username);
        if(user == null) throw new UserNotFoundException(username);
        else return user;
    }
}
