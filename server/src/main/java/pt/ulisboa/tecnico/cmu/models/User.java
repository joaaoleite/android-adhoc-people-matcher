package pt.ulisboa.tecnico.cmu.models;

public class User extends Model{

    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);

        // for each tags

        return json;
    }

}
