package pt.ulisboa.tecnico.cmu.controllers;

import pt.ulisboa.tecnico.cmu.models.LocationAbstract;
import pt.ulisboa.tecnico.cmu.models.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import org.json.JSONObject;
import java.security.SecureRandom;
import java.math.BigInteger;



public class Messages extends Controller{

    public void put(LocationAbstract location, String policy, HashMap<String, String> tags, Date start, Date end){
        String messageID;
        do{
            SecureRandom random = new SecureRandom();
            messageID = new BigInteger(100, random).toString(32);
        } while(get(messageID) != null);

        super.put(messageID, new Message(location, policy, tags, start, end));
    }
}
