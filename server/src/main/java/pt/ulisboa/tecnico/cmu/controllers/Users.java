package pt.ulisboa.tecnico.cmu.controllers;

import pt.ulisboa.tecnico.cmu.models.User;
import pt.ulisboa.tecnico.cmu.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.cmu.exceptions.UserAlreadyExistsException;
import java.util.HashMap;
import org.json.JSONObject;

public class Users extends Controller{

    public User getUserByUsername(String username) throws UserNotFoundException{
        User user = (User) super.get(username);
        if(user == null) throw new UserNotFoundException(username);
        else return user;
    }

    public void createUser(String username, String password) throws UserAlreadyExistsException{
        if(super.get(username) != null) throw new UserAlreadyExistsException(username);
        else super.put(username, new User(username, password));
    }
}
