package pt.ulisboa.tecnico.cmu.models;

import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class User extends Model{

	private String username;
	private String password;
	private ConcurrentHashMap<String, String> keys;
	private HashSet<String> messagesID;

	public User(String username, String password){
		this.username = username;
		this.password = password;
		this.keys = new ConcurrentHashMap<String, String>();
		this.messagesID = new HashSet<String>();
	}

	public String getPassword(){
		return this.password;
	}

	public ConcurrentHashMap<String, String> getKeys(){
		return this.keys;
	}

	public void addKey(String key, String value){
		this.keys.put(key, value);
	}

	public void removeKey(String key){
		this.keys.remove(key);
	}

	public HashSet<String> getMessagesID(){
		return this.messagesID;
	}

	public void addMessageID(String id){
		this.messagesID.add(id);
	}

	public void removeMessageID(String id){
		this.messagesID.remove(id);
	}

    @Override
    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        obj.put("username", this.username);
        obj.put("password", this.password);

		JSONArray array = new JSONArray();
		for(HashMap.Entry<String, String> key : this.keys.entrySet()){
			array.put(new String[]{key.getKey(), key.getValue()});
		}
		obj.put("keys", array);
        return obj;
    }
}
