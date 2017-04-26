package pt.ulisboa.tecnico.cmu.locmess.session;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class Request {

    private int method;
    private String url;
    private Map<String,String> data = null;

    public Request(String method, String path, Map<String,String> data){
        this.method = parseMethod(method);
        this.url = Session.getInstance().BASE_URL + path;
        this.data = data;
    }
    public Request(String method, String path){
        this.method = parseMethod(method);
        this.url = Session.getInstance().BASE_URL + path;
    }

    private int parseMethod(String m){
        if(m.equals("GET")) return com.android.volley.Request.Method.GET;
        if(m.equals("POST")) return com.android.volley.Request.Method.POST;
        if(m.equals("PUT")) return com.android.volley.Request.Method.PUT;
        return -1;
    }

    public void execute() {

        final Request that = this;

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    Log.d("HTTP","onResponse: "+res);
                    JSONObject json = new JSONObject(res);
                    that.onResponse(json);
                } catch (JSONException e) {
                    Log.d("HTTP","JSON Error: "+e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String msg = error.getMessage();
                Log.e("HTTP", "Request Error: "+msg);
                that.onError(msg);
            }
        };

        StringRequest req = new StringRequest(method, url, response, error){

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return that.data;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");

                String token = Session.getInstance().token();
                if(token != null)
                    params.put("Authorization", "Basic "+token);

                return params;
            }

        };
        Session.getInstance().request(req);
    }

    public abstract void onResponse(JSONObject json) throws JSONException;
    public abstract void onError(String error);
}
