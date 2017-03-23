package pt.ulisboa.tecnico.cmu.models;

import java.util.HashMap;
import org.json.JSONObject;
import java.util.Date;
import java.security.SecureRandom;
import java.math.BigInteger;
import org.json.JSONArray;

public class Message extends Model {

	private LocationAbstract location;
	private String policy;
	private HashMap<String, String> tags;
	private Date start;
	private Date end;

	public Message(LocationAbstract location, String policy, HashMap<String, String> tags, Date start, Date end){
		this.location = location;
		this.policy = policy;
		this.tags = tags;
		this.start = start;
		this.end = end;
	}

	@Override
    public JSONObject toJSON(){
		JSONObject obj = new JSONObject();
		obj.put("policy", this.policy);

		JSONArray array = new JSONArray();
		for(HashMap.Entry<String, String> tag : this.tags.entrySet()){
			array.put(new String[]{tag.getKey(), tag.getValue()});
		}
		obj.put("tags", array);
		obj.put("start", this.start);
		obj.put("end", this.end);
		return obj;
    }
}
