package pt.ulisboa.tecnico.cmu.controllers;

import pt.ulisboa.tecnico.cmu.models.Model;
import pt.ulisboa.tecnico.cmu.models.User;
import pt.ulisboa.tecnico.cmu.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.cmu.exceptions.UserAlreadyExistsException;
import pt.ulisboa.tecnico.cmu.exceptions.AuthenticationFailedException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

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

    private boolean verifyAuthentication(String username, String password){
        try{
            if(this.getUserByUsername(username).getPassword().equals(password)) return true;
            else return false;
        }
        catch(UserNotFoundException e){
            return false;
        }
    }

    public String verifyToken(String token){
        String tokenStr = new String(Base64.getDecoder().decode(token));
        String username = tokenStr.split(":")[0];
        String password = tokenStr.split(":")[1];
        if(verifyAuthentication(username, password)) return username;
        else return null;
    }

    public String logIn(String username, String password) throws AuthenticationFailedException{
        if(this.verifyAuthentication(username, password))
            return Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        else throw new AuthenticationFailedException(username);
    }

    public ConcurrentHashMap<String, HashSet<String>> getGlobalKeys(){
        ConcurrentHashMap<String, HashSet<String>> keys = new ConcurrentHashMap<String, HashSet<String>>();
        for(HashMap.Entry<String, Model> user : this.entrySet()){
            for(HashMap.Entry<String, String> userKey : ((User) user.getValue()).getKeys().entrySet()){
                HashSet<String> hash;
                if(keys.containsKey(userKey.getKey()))
                    hash = keys.get(userKey.getKey());
                else
                    hash = new HashSet<String>();
                hash.add(userKey.getValue());
                keys.put(userKey.getKey(), hash);
            }
        }
        return keys;
    }

    public ConcurrentHashMap<String, String> getUserKeys(String username) throws UserNotFoundException{
        return this.getUserByUsername(username).getKeys();
    }

    public void addKeyToUser(String username, String key, String value) throws UserNotFoundException{
        this.getUserByUsername(username).addKey(key, value);
    }

    public void removeKeyFromUser(String username, String key) throws UserNotFoundException{
        this.getUserByUsername(username).removeKey(key);
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
