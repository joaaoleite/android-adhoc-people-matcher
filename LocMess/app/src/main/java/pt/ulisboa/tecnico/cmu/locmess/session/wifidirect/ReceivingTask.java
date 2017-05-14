package pt.ulisboa.tecnico.cmu.locmess.session.wifidirect;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessageModel;
import pt.ulisboa.tecnico.cmu.locmess.session.LocMessService;
import pt.ulisboa.tecnico.cmu.locmess.session.Session;

public class ReceivingTask extends AsyncTask<Void, String, Void> {

    private SimWifiP2pSocketServer mSrvSocket;

    @Override
    protected Void doInBackground(Void... params) {

        Log.d("WifiDirect", "ReceivingTask...");

        try {
            mSrvSocket = new SimWifiP2pSocketServer(10001);
            Log.d("WifiDirect", "Socket open!");
        } catch (IOException e) {
            Log.d("ReceivingTask","Error open server...");
            e.printStackTrace();
        }
        if(mSrvSocket==null) return null;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                SimWifiP2pSocket sock = mSrvSocket.accept();
                Log.d("WifiDirect", "receiving accept");
                try {
                    BufferedReader sockIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                    Log.d("ReceivingTask","sockIn");
                    String received = sockIn.readLine();
                    Log.d("ReceivingTask","Message received: "+received);
                    sock.getOutputStream().write((getMsgs(received).toString()+"\n").getBytes());
                    Log.d("ReceivingTask","Message sent: OK");
                } catch (IOException e) {
                    Log.d("WifiDirect", "Error socket: "+e.getMessage());
                } finally {
                    sock.close();
                }
            } catch (IOException e) {
                Log.d("WifiDirect", "Error socket: "+e.getMessage());
                break;
            }
        }
        return null;
    }

    public boolean matchLocation(double lat, double lng, int radius){
        if(LocMessService.location!=null){
            return true; // TODO: match location by args
        }else{
            return false;
        }
    }

    public boolean matchWifi(String ssid){
        if(LocMessService.ssids!=null){
            return LocMessService.ssids.containsKey(ssid);
        }else{
            return false;
        }
    }

    public JSONArray msgsNow(){
        try {
            String messages = LocMessService.prefs.getString("messages", new JSONArray().toString());
            JSONObject obj = new JSONObject("{\"messages\":" + messages + "}");
            JSONArray json = obj.getJSONArray("messages");
            JSONArray res = new JSONArray();
            for(int i=0; i<json.length(); i++){
                JSONObject msg = json.getJSONObject(i);
                String locations = LocMessService.prefs.getString("locations","[]");
                JSONObject json2 = new JSONObject("{\"locations\":"+locations+"}");
                JSONArray locs = json2.getJSONArray("locations");
                for(int l=0; l<locs.length(); l++){
                    if(locs.getJSONObject(l).getString("name").equals(msg.getString("name"))){
                        JSONObject loc = locs.getJSONObject(i);
                        if(loc.has("ssid")) {
                            if(matchWifi(loc.getString("ssid"))){
                                res.put(msg);
                            }
                        }
                        else if(matchLocation(loc.getDouble("latitude"),loc.getDouble("longitude"),loc.getInt("radius"))){
                            res.put(msg);
                        }
                    }
                }

            }
            return res;
        }catch (Exception e){
            return new JSONArray();
        }
    }

    public JSONArray getMsgs(String keys){
        try{
            JSONObject profile = new JSONObject(keys);

            JSONArray msgs = msgsNow();

            JSONArray res = new JSONArray();

            for(int i=0; i<msgs.length(); i++){
                JSONObject msg = msgs.getJSONObject(i);
                JSONObject filter = msg.getJSONObject("filter");

                //TODO: blacklist

                //whitelist
                Iterator<?> filter_keys = filter.keys();
                boolean match = true;
                while( filter_keys.hasNext() ) {
                    String key = (String)filter_keys.next();
                    if(profile.has(key)){
                        match = profile.getString(key).equals(filter.get(key));
                    }
                    else match = false;
                }

                if(match) {
                    res.put(msg);
                }
            }
            return res;
        }
        catch (JSONException e){
            return new JSONArray();
        }
    }
}
