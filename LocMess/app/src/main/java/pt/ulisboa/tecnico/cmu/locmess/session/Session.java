package pt.ulisboa.tecnico.cmu.locmess.session;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.ulisboa.tecnico.cmu.locmess.main.locations.LocationAdapter;
import pt.ulisboa.tecnico.cmu.locmess.main.locations.LocationModel;
import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessageModel;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.PairModel;
import pt.ulisboa.tecnico.cmu.locmess.session.requests.Request;

public class Session {

    public static final String APP_NAME = "pt.ulisboa.tecnico.cmu.locmess";
    public static final String BASE_URL = "https://cmu.n1z.pt:8080";

    private static Session instance = null;

    private RequestQueue queue;
    private SharedPreferences prefs;

    private Session(Context context, SharedPreferences prefs) {
        this.queue = Volley.newRequestQueue(context);
        this.prefs = prefs;
    }
    public static Session getInstance(){ return instance; }
    public static Session getInstance(Activity act) {
        if(instance==null) {
            Context context = act.getApplicationContext();
            SharedPreferences prefs = act.getSharedPreferences(APP_NAME, context.MODE_PRIVATE);
            instance = new Session(context, prefs);
            Log.d("Session", "Init");
        }
        return instance;
    }

    // --------------------------------------------

    public void request(StringRequest req){
        this.queue.add(req);
    }

    public SharedPreferences.Editor editor(){
        return prefs.edit();
    }

    // --------------------------------------------

    public boolean isLoggedIn(){
        try{
            return prefs.getBoolean("login", false);
        }
        catch (Exception e){
            return false;
        }
    }

    public String token(){
        if(isLoggedIn())
            return prefs.getString("token","...");
        else
            return null;
    }

    public void token(String newToken, String username){
        if(newToken != null){
            SharedPreferences.Editor editor = editor();
            editor.clear();
            editor.putString("username",username);
            editor.putBoolean("login", true);
            editor.putString("token", newToken);
            editor.apply();
        }
        else logout();
    }

    // --------------------------------------------

    public void logout(){
        SharedPreferences.Editor editor = editor();
        editor.clear();
        editor.putString("username",null);
        editor.putBoolean("login",false);
        editor.putString("token",null);
        editor.apply();
    }


    // --------------------------------------------

    public void updateLocations(){
        new Request("GET","/locations"){
            @Override
            public void onResponse(JSONObject json) throws JSONException {
                JSONArray locs = json.getJSONArray("locations");
                if(locs!=null && locs.length()>0) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("locations", locs.toString());
                    editor.commit();
                    Log.d("Session", "save locations complete: " + locs);
                }
            }
            @Override
            public void onError(String msg){

            }
        }.execute();
    }

    public String me(){
       return prefs.getString("username","me");
    }

    public String getLocations(){ return prefs.getString("locations",null); }

    public String getProfile(){
        return prefs.getString("profile",null);
    }

    public String getKeys(){
        return prefs.getString("messages",null);
    }

    public void saveKeys(List<PairModel> pairs){

        if(pairs!=null && pairs.size()>0) {
            JSONObject json = new JSONObject();
            try {

                for (int i = 0; i < pairs.size(); i++)
                    json.put(pairs.get(i).getKey(), pairs.get(i).getValue());

            }catch (JSONException e){}

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("profile", json.toString());
            editor.commit();

            Log.d("Session", "save pairs complete: " + json);
        }
    }

    public void deleteMsg(String id){
        try{
            String messages = prefs.getString("messages",new JSONArray().toString());
            JSONObject obj = new JSONObject("{\"messages\":"+messages+"}");
            JSONArray json = obj.getJSONArray("messages");

            JSONArray res = new JSONArray();

            for(int i=0; i<json.length(); i++)
                if(!json.getJSONObject(i).getString("id").equals(id)) {
                    res.put(json.getJSONObject(i));
                }
                else{
                    Log.d("Session","deleteMsg = "+json.getJSONObject(i));
                }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("messages", res.toString());
            editor.commit();
            Log.d("Session","delete message complete "+json);
        }
        catch (Exception e){
            Log.d("Session","deleteMsg1",e);
        }

        try{
            String messages = prefs.getString("received",new JSONArray().toString());
            JSONObject obj = new JSONObject("{\"messages\":"+messages+"}");
            JSONArray json = obj.getJSONArray("messages");

            JSONArray res = new JSONArray();

            for(int i=0; i<json.length(); i++)
                if(!json.getJSONObject(i).getString("id").equals(id))
                    res.put(json.getJSONObject(i));

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("messages", res.toString());
            editor.commit();
            Log.d("Session","delete message complete "+json);
        }
        catch (Exception e){
            Log.d("Session","deleteMsg2",e);
        }
    }

    public void saveMsg(MessageModel message){
        try{
            String messages = prefs.getString("messages",new JSONArray().toString());
            JSONObject obj = new JSONObject("{\"messages\":"+messages+"}");
            JSONArray json = obj.getJSONArray("messages");
            if(messages!=null)
                json.put(message.toJSON());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("messages", json.toString());
            editor.commit();
            Log.d("Session","save message complete: "+json);

        }
        catch (Exception e){}
    }

    public ArrayList<MessageModel> getMsgsSent(){
        try{
            String messages = prefs.getString("messages",new JSONArray().toString());
            JSONObject obj = new JSONObject("{\"messages\":"+messages+"}");
            JSONArray json = obj.getJSONArray("messages");
            ArrayList<MessageModel> msgs = new ArrayList<>();
            for(int i=0; i<json.length(); i++){
                msgs.add(new MessageModel(json.getJSONObject(i)));
            }
            Log.d("Session","getMsgs");
            return msgs;
        }
        catch (Exception e){
            Log.d("Session","getMsgs",e);
            return null;
        }
    }

    public ArrayList<MessageModel> getMsgsReceived(){
        try{
            Set<String> messages = prefs.getStringSet("received",null);

            ArrayList<MessageModel> res = new ArrayList<>();
            for(String msg : messages){
                try {
                    res.add(new MessageModel(new JSONObject(msg)));
                }
                catch (JSONException e){}
            }
            Log.d("Session","getMsgsReceived");
            return res;
        }
        catch (Exception e){
            Log.d("Session","getMsgsReceived",e);
            return new ArrayList<>();
        }
    }

    public void saveMsgs(List<MessageModel> messages){
        if(messages!=null && messages.size()>0) {
            JSONArray json = new JSONArray();

            for (int i = 0; i < messages.size(); i++)
                json.put(messages.get(i).toJSON());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("messages", json.toString());
            editor.commit();

            Log.d("Session", "save messages complete: " + json);
        }
    }
}
