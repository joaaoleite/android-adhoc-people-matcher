package pt.ulisboa.tecnico.cmu.models;

import java.util.HashMap;
import org.json.JSONObject;
import java.util.Date;
import java.security.SecureRandom;
import java.math.BigInteger;
import org.json.JSONArray;

public class Message extends Model {

	private String id;
	private String location;
	private String user;
	private String policy;
	private HashMap<String, String> tags;
	private Date start;
	private Date end;
	private String content;

	public Message(String id, String location, String user, String policy, HashMap<String, String> tags, Date start, Date end, String content){
		this.id = id;
		this.location = location;
		this.user = user;
		this.policy = policy;
		this.tags = tags;
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

	@Override
    public JSONObject toJSON(){
		JSONObject obj = new JSONObject();
		obj.put("id", this.id);
		obj.put("location", this.location);
		obj.put("user", this.user);
		obj.put("policy", this.policy);

		JSONArray array = new JSONArray();
		for(HashMap.Entry<String, String> tag : this.tags.entrySet()){
			array.put(new String[]{tag.getKey(), tag.getValue()});
		}
		obj.put("tags", array);
		obj.put("start", this.start);
		obj.put("end", this.end);
		obj.put("content", this.content);
		return obj;
    }
}
