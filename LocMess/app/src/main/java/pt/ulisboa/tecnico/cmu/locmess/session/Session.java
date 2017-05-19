package pt.ulisboa.tecnico.cmu.locmess.session;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
    public static Session newInstance(Activity act) {
        Context context = act.getApplicationContext();
        SharedPreferences prefs = act.getSharedPreferences(APP_NAME, context.MODE_PRIVATE);
        instance = new Session(context, prefs);
        return instance;
    }

    // --------------------------------------------

    public void request(StringRequest req){
        this.queue.add(req);
    }

    public void save(String key, String value){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String get(String key){
        return prefs.getString(key,null);
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
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.putString("me",username);
            editor.putBoolean("login", true);
            editor.putString("token", newToken);
            editor.putString("max",100+"");
            editor.apply();
        }
        else logout();
    }

    // --------------------------------------------

    public void logout(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putString("me",null);
        editor.putBoolean("login",false);
        editor.putString("token",null);
        editor.putString("max",null);
        editor.apply();
    }
}
