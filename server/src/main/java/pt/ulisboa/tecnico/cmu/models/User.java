package pt.ulisboa.tecnico.cmu.models;

import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONArray;

public class User extends Model{

	private String username;
	private String password;
	private HashMap<String, String> tags;

	public User(String username, String password){
		this.username = username;
		this.password = password;
		tags = new HashMap<String, String>();
	}

	public String getPassword(){
		return this.password;
	}

    @Override
    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        obj.put("username", this.username);
        obj.put("password", this.password);

		JSONArray array = new JSONArray();
		for(HashMap.Entry<String, String> tag : this.tags.entrySet()){
			array.put(new String[]{tag.getKey(), tag.getValue()});
		}
		obj.put("tags", array);
        return obj;
    }
}
