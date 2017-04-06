package pt.ulisboa.tecnico.cmu.controllers;

import pt.ulisboa.tecnico.cmu.models.Model;
import pt.ulisboa.tecnico.cmu.models.User;
import pt.ulisboa.tecnico.cmu.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.cmu.exceptions.UserAlreadyExistsException;
import pt.ulisboa.tecnico.cmu.exceptions.AuthenticationFailedException;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.HashSet;

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

    public void logIn(String username, String password) throws AuthenticationFailedException{
        try{
            if(!this.getUserByUsername(username).getPassword().equals(password))
                throw new AuthenticationFailedException(username);
        }
        catch(UserNotFoundException e){
            throw new AuthenticationFailedException(username);
        }
    }

    public HashSet<String> getGlobalKeys(){
        HashSet<String> keys = new HashSet<String>();
        for(HashMap.Entry<String, Model> user : this.entrySet()){
            keys.addAll(((User) user.getValue()).getKeys().keySet());
        }
        return keys;
    }

    public HashMap<String, String> getUserKeys(String username) throws UserNotFoundException{
        return this.getUserByUsername(username).getKeys();
    }

    public void addKeyToUser(String username, String key, String value) throws UserNotFoundException{
        this.getUserByUsername(username).addKey(key, value);
    }

    public HashSet<String> getUserMessagesID(String username) throws UserNotFoundException{
        return this.getUserByUsername(username).getMessagesID();
    }

    public void addMessageIDToUser(String username, String id) throws UserNotFoundException{
        this.getUserByUsername(username).addMessageID(id);
    }

    public void removeMessageIDFromUser(String username, String id) throws UserNotFoundException{
        this.getUserByUsername(username).removeMessageID(id);
    }
}