package pt.ulisboa.tecnico.cmu.models;

import java.util.HashMap;
import org.json.JSONObject;
import java.util.Date;
import java.security.SecureRandom;
import java.math.BigInteger;
import org.json.JSONArray;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class Message extends Model {

	private String id;
	private String location;
	private String user;
	private String policy;
	private HashMap<String, String> keys = null;
	private Date start;
	private Date end;
	private String content;

	public Message(String id, String location, String user, String policy, HashMap<String, String> keys, Date start, Date end, String content){
		this.id = id;
		this.location = location;
		this.user = user;
		this.policy = policy;
		this.keys = keys;
		this.start = start;
		this.end = end;
		this.content = content;
	}

	public String getLocation(){
		return this.location;
	}

	public String getUser(){
		return this.user;
	}

	public boolean isNow(){
		return (new Date().after(this.start)) && (new Date().before(this.end));
	}

	public boolean fitKeys(ConcurrentHashMap<String, String> userKeys){
		if(policy.equals("whitelist") && keys == null) return true;

		for(HashMap.Entry<String, String> entry : this.keys.entrySet()){
			if(policy.equals("whitelist")){
				if(userKeys.get(entry.getKey()) == null) return false;
				else if(!userKeys.get(entry.getKey()).equals(entry.getValue())) return false;
			}
			else if(policy.equals("blacklist")){
		   	   	if(userKeys.get(entry.getKey()) != null){
					if(userKeys.get(entry.getKey()).equals(entry.getValue())) return false;
				}
			}
		}
		return true;
	}

	@Override
    public JSONObject toJSON(){
		JSONObject obj = new JSONObject();
		obj.put("id", this.id);
		obj.put("location", this.location);
		obj.put("user", this.user);
		obj.put("policy", this.policy);

		JSONObject keys = new JSONObject();
		for(HashMap.Entry<String, String> key : this.keys.entrySet()){
			keys.put(key.getKey(), key.getValue());
		}
		obj.put("filter", keys);
		obj.put("start", this.start.getTime());
		obj.put("end", this.end.getTime());
		obj.put("content", this.content);
		return obj;
    }
}
