package pt.ulisboa.tecnico.cmu.models;

import org.json.JSONObject;

public class User extends Model{

	private String username;
	private String password;

    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);

        // for each tags

        return json;
    }

}
