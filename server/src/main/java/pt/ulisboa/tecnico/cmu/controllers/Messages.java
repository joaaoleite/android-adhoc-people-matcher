package pt.ulisboa.tecnico.cmu.controllers;

import pt.ulisboa.tecnico.cmu.Database;
import pt.ulisboa.tecnico.cmu.models.Message;
import pt.ulisboa.tecnico.cmu.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.cmu.exceptions.LocationNotFoundException;
import pt.ulisboa.tecnico.cmu.exceptions.MessageNotFoundException;
import java.util.HashMap;
import java.util.Date;
import org.json.JSONObject;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.HashSet;

public class Messages extends Controller{

    public Message getMessageByID(String id) throws MessageNotFoundException{
        Message msg = (Message) super.get(id);
        if(msg == null) throw new MessageNotFoundException(id);
        else return msg;
    }

    public HashSet<Message> getMessagesByUser(String user) throws UserNotFoundException, MessageNotFoundException{
        HashSet<Message> res = new HashSet<Message>();
        for(String messageID : Database.Users().getUserMessagesID(user)){
            res.add(this.getMessageByID(messageID));
        }
        return res;
    }

    public void addMessage(String user, String location, String policy, HashMap<String, String> tags, Date start, Date end, String content)
                                                            throws UserNotFoundException, LocationNotFoundException{
        String messageID;
        do{
            SecureRandom random = new SecureRandom();
            messageID = new BigInteger(100, random).toString(32);
        } while(super.get(messageID) != null);

        try{
            Database.Users().addMessageIDToUser(user, messageID);
            Database.Locations().addMessageIDToLocation(location, messageID);
            super.put(messageID, new Message(messageID, location, user, policy, tags, start, end, content));
        }
        catch(LocationNotFoundException e){
            Database.Users().removeMessageIDFromUser(user, messageID);
            throw new LocationNotFoundException(location);
        }
    }

    public void removeMessage(String id)
    throws MessageNotFoundException, UserNotFoundException, LocationNotFoundException{
        Message msg = this.getMessageByID(id);
        String location = msg.getLocation();
        String user = msg.getUser();

        super.remove(id);
        Database.Locations().removeMessageIDFromLocation(location, id);
        Database.Users().removeMessageIDFromUser(user, id);
    }

    public HashSet<Message> getMatches(String username, double latitude, double longitude, HashSet<String> ssids)
    throws MessageNotFoundException, UserNotFoundException, LocationNotFoundException{
        HashSet<Message> res = new HashSet<Message>();
        HashMap<String, String> userKeys = Database.Users().getUserKeys(username);
        HashSet<String> locations = Database.Locations().getLocationsNameNearBy(latitude, longitude, ssids);
        for(String location : locations){
            HashSet<String> messageIDs = Database.Locations().getLocationMessagesID(location);
            for(String id : messageIDs){
                Message msg = this.getMessageByID(id);
                if(msg.isNow() && msg.fitKeys(userKeys)) res.add(msg);
            }
        }
        return res;
    }
}
