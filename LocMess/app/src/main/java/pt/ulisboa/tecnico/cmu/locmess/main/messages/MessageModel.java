package pt.ulisboa.tecnico.cmu.locmess.main.messages;


import android.view.View;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.PairModel;
import pt.ulisboa.tecnico.cmu.locmess.session.requests.Request;

public class MessageModel {
    private String location;
    private String user;
    private String subject;
    private String policy;
    private ArrayList<PairModel> filter;
    private Calendar start;
    private Calendar end;
    private String content;
    private String id;
    private String msgType;
    private String delivery_mode;



    private boolean selected;
    public View view;

    public MessageModel(JSONObject json) throws Exception {
        this.id = json.getString("id");
        this.location = json.getString("location");
        this.user = json.getString("user");
        this.policy = json.getString("policy");
        JSONObject filter = json.getJSONObject("filter");
        ArrayList<PairModel> pairs = new ArrayList<>();
        Iterator<?> keys = filter.keys();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            pairs.add(new PairModel(key,filter.getString(key)));
        }
        this.filter = pairs;

        this.start = Calendar.getInstance();
        this.start.setTime(new Date(json.getLong("start")));
        this.end = Calendar.getInstance();
        this.end.setTime(new Date(json.getLong("end")));
        this.content = json.getString("content");
        this.msgType = json.getString("msgType");
        this.delivery_mode = "decentralized";
        this.selected = false;
    }

    public MessageModel(String mode, String id, String location, String user, String content, String policy, ArrayList<PairModel> filter, Calendar start, Calendar end){
        this.id = id;
        this.location = location;
        this.user = user;
        this.policy = policy;
        this.filter = filter;
        this.start = start;
        this.end = end;
        this.content = content;
        this.msgType = "Sent";
        this.delivery_mode = mode;
        this.selected = false;
    }

    public MessageModel(String id, String location, String user, String content, String policy, ArrayList<PairModel> filter, Calendar start, Calendar end){
        this.id = id;
        this.location = location;
        this.user = user;
        this.policy = policy;
        this.filter = filter;
        this.start = start;
        this.end = end;
        this.content = content;
        this.msgType = "Sent";
        this.delivery_mode = "centralized";
        this.selected = false;
    }

    public MessageModel(String id, String location, String user, String content, String policy, ArrayList<PairModel> filter, Calendar start, Calendar end, String msgType){
        this.id = id;
        this.location = location;
        this.user = user;
        this.policy = policy;
        this.filter = filter;
        this.start = start;
        this.end = end;
        this.content = content;
        this.msgType = msgType;
        this.delivery_mode = "centralized";

        this.selected = false;
    }

    public String getMode(){ return this.delivery_mode; }

    public String getId(){ return this.id; }

    public String getLocation(){
        return this.location;
    }

    public String getUser(){
        return this.user;
    }

    public String getSubject(){
        return this.content;
    }

    public String getPolicy(){
        return this.policy;
    }

    public ArrayList<PairModel> getFilter(){
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

    public String getMsgType(){
        return this.msgType;
    }

    public void setLocation(String value){ this.location = value; }

    public void setUser(String value){
        this.user = value;
    }

    public void setSubject(String value){
        this.subject = value;
    }

    public void setPolicy(String value){ this.policy = value; }

    public void setFilter(ArrayList value){ this.filter = value; }

    public void setStart(Calendar value){ this.start = value; }

    public void setEnd(Calendar value){ this.end = value; }

    public void setContent(String value){ this.content = value; }


    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        try {
            JSONObject l = new JSONObject();
            obj.put("location", location);
            obj.put("user", user);
            obj.put("subject", subject);
            obj.put("policy", policy);

            JSONObject f = new JSONObject();
            for(int i=0; i<filter.size(); i++)
                f.put(filter.get(i).getKey(),filter.get(i).getValue());

            obj.put("filter",f);
            obj.put("start", start.getTime().getTime());
            obj.put("end", end.getTime().getTime());
            obj.put("mode",delivery_mode);
            obj.put("content", content);
            obj.put("id", id);
            obj.put("msgType", msgType);
        }catch (JSONException e){ }

        return obj;
    }


    public boolean toogle(){
        this.selected = !this.selected;
        return this.selected;
    }

    public boolean isSelected(){
        return this.selected;
    }
}
