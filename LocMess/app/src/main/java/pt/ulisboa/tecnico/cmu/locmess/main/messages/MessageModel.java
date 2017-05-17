package pt.ulisboa.tecnico.cmu.locmess.main.messages;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import pt.ulisboa.tecnico.cmu.locmess.main.locations.LocationModel;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.PairModel;
import pt.ulisboa.tecnico.cmu.locmess.session.LocMessService;
import pt.ulisboa.tecnico.cmu.locmess.session.Session;

public class MessageModel {
    private String id;

    private String user;
    private String location;

    private MESSAGE_POLICY policy;

    private Set<PairModel> filter;
    private Calendar start;
    private Calendar end;

    private String content;

    private MESSAGE_TYPE type;
    private MESSAGE_MODE mode;

    private boolean selected;

    public MessageModel(JSONObject json) throws JSONException {

        this.id = json.getString("id");

        this.location = json.getString("location");

        this.user = Session.getInstance().get("me");
        if(json.has("user"))
            this.user = json.getString("user");

        String msgPolicy = json.getString("policy");
        if(msgPolicy.equals("whitelist"))
            this.policy = MESSAGE_POLICY.WHITELIST;
        if(msgPolicy.equals("blacklist"))
            this.policy = MESSAGE_POLICY.BLACKLIST;

        JSONObject msgFilter = json.getJSONObject("filter");
        this.filter = new HashSet<>();
        Iterator<?> keys = msgFilter.keys();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            this.filter.add(new PairModel(key,msgFilter.getString(key)));
        }

        this.start = Calendar.getInstance();
        this.start.setTime(new Date(json.getLong("start")));
        this.end = Calendar.getInstance();
        this.end.setTime(new Date(json.getLong("end")));

        this.content = json.getString("content");

        this.type = MESSAGE_TYPE.SENT;
        if(json.has("type")) {
            String msgType = json.getString("type");
            if (msgType.equals("received"))
                this.type = MESSAGE_TYPE.RECEIVED;
        }

        this.mode = MESSAGE_MODE.CENTRALIZED;
        if(json.has("mode")) {
            String msgMode = json.getString("mode");
            if (msgMode.equals("decentralized"))
                this.mode = MESSAGE_MODE.DECENTRALIZED;
        }

        this.selected = false;
    }

    public MessageModel(MESSAGE_MODE mode, String id, LocationModel location, String user, MESSAGE_POLICY policy, Set<PairModel> filter, Calendar start, Calendar end, String content){
        this.id = id;

        this.location = location.getName();
        this.user = user;

        this.policy = policy;
        this.filter = filter;

        this.start = start;
        this.end = end;

        this.content = content;

        this.type = MESSAGE_TYPE.RECEIVED;
        this.mode = mode;
    }

    public MessageModel(LocationModel location, MESSAGE_POLICY policy, Set<PairModel> filter, Calendar start, Calendar end, String content){
        this.id = generateID();

        this.location = location.getName();
        this.user = Session.getInstance().get("me");

        this.policy = policy;
        this.filter = filter;

        this.start = start;
        this.end = end;

        this.content = content;

        this.type = MESSAGE_TYPE.SENT;
        this.mode = MESSAGE_MODE.DECENTRALIZED;
    }

    // ------------------------------------------------------------

    public MESSAGE_MODE getMode(){ return this.mode; }

    public String getId(){ return this.id; }

    public String getLocation(){
        return this.location;
    }

    public String getUser(){
        return this.user;
    }

    public MESSAGE_POLICY getPolicy(){
        return this.policy;
    }

    public Set<PairModel> getFilter(){
        return this.filter;
    }

    public Calendar getStart(){
        return this.start;
    }

    public Calendar getEnd(){
        return this.end;
    }

    public String getContent(){
        return this.content;
    }

    public MESSAGE_TYPE getType(){ return this.type; }

    public boolean isNow(){
        Date now = new Date();
        return start.getTime().before(now) && end.getTime().after(now);
    }

    // ------------------------------------------------------------

    public void setLocation(String value){ this.location = value; }

    public void setUser(String value){
        this.user = value;
    }

    public void setPolicy(MESSAGE_POLICY value){ this.policy = value; }

    public void setFilter(Set<PairModel> value){ this.filter = value; }

    public void setStart(Calendar value){ this.start = value; }

    public void setEnd(Calendar value){ this.end = value; }

    public void setContent(String value){ this.content = value; }

    // ------------------------------------------------------------

    public JSONObject toJSON() throws JSONException{
        JSONObject obj = new JSONObject();

        obj.put("id", id);

        obj.put("location", location);
        obj.put("user", user);

        obj.put("policy", policy.toString().toLowerCase());
        JSONObject f = new JSONObject();
        for(PairModel pair : filter)
            f.put(pair.getKey(),pair.getValue());
        obj.put("filter",f);

        obj.put("start", start.getTime().getTime());
        obj.put("end", end.getTime().getTime());

        obj.put("content", content);

        obj.put("mode",mode.toString().toLowerCase());
        obj.put("type", type.toString().toLowerCase());

        return obj;
    }

    // ------------------------------------------------------------

    public boolean toogle(){
        this.selected = !this.selected;
        return this.selected;
    }

    public boolean isSelected(){
        return this.selected;
    }

    // ------------------------------------------------------------

    public enum MESSAGE_TYPE {
        SENT,RECEIVED
    };
    public enum MESSAGE_MODE {
        CENTRALIZED,DECENTRALIZED
    };
    public enum MESSAGE_POLICY {
        WHITELIST, BLACKLIST
    }

    private String generateID(){
        return new BigInteger(100, new SecureRandom()).toString(32);
    }
}
