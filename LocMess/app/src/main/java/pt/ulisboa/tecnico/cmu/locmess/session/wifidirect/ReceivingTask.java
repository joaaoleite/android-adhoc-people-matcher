package pt.ulisboa.tecnico.cmu.locmess.session.wifidirect;

import android.content.SharedPreferences;
import android.location.Location;
import android.net.wifi.ScanResult;
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmu.locmess.main.locations.LocationModel;
import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessageAdapter;
import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessageModel;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.PairModel;
import pt.ulisboa.tecnico.cmu.locmess.session.LocMessService;
import pt.ulisboa.tecnico.cmu.locmess.session.Session;
import pt.ulisboa.tecnico.cmu.locmess.session.data.Profile;

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
                    sock.getOutputStream().write((send(received)+"\n").getBytes());
                    Log.d("ReceivingTask","Message sent: OK");
                } catch (IOException e) {
                    Log.d("WifiDirect", "Error socket: "+e.getMessage());
                } finally {
                    sock.close();
                }
            } catch (IOException e) {
                Log.d("ReceivingTask","doInBackground",e);
                Log.d("WifiDirect", "Error socket: "+e.getMessage());
                break;
            }
        }
        return null;
    }


    private String send(String received){
        Set<PairModel> pairs = new HashSet<>();
        try {
            JSONObject json = new JSONObject(received);
            Iterator<?> keys = json.keys();
            while(keys.hasNext() ) {
                String key = (String)keys.next();
                String value = (String) json.getString(key);
                pairs.add(new PairModel(key,value));
            }
        }
        catch (Exception e){}


        Set<MessageModel> messages = LocMessService.getInstance().MESSAGES().sent();
        Set<MessageModel> matches = new Profile(pairs).match(messages);

        JSONArray json = new JSONArray();
        for(MessageModel msg : matches) {
            try {
                LocationModel loc = LocMessService.getInstance().LOCATIONS().find(msg.getLocation());
                if(loc.getType() == LocationModel.LOCATION_TYPE.GPS)
                    if(!LocMessService.getInstance().LOCATIONS().match(loc)) continue;
                if(loc.getType() == LocationModel.LOCATION_TYPE.SSID)
                    if(!LocMessService.getInstance().WIFIS().match(loc)) continue;

                json.put(msg.toJSON());
            }
            catch (Exception e){}
        }

        return json.toString();
    }
}
