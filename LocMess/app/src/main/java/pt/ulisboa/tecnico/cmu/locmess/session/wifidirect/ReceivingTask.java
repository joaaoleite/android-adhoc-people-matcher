package pt.ulisboa.tecnico.cmu.locmess.session.wifidirect;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmu.locmess.main.locations.LocationModel;
import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessageModel;
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
                    Log.d("ReceivingTask","received="+received);
                    receive(received);
                    Log.d("ReceivingTask","Message received: "+received);
                    sock.getOutputStream().write((("OK\n").getBytes()));
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

    public void receive(String msgs){
        try{
            JSONArray json = new JSONArray(msgs);
            for(int i=0; i<json.length(); i++) {
                JSONObject msg = json.getJSONObject(i);
                MessageModel message = new MessageModel(msg);

                Log.d("ReceivingTask","receive() msg.user="+message.getUser());
                Log.d("ReceivingTask","receive() me="+Session.getInstance().get("me"));

                if(message.getUser().equals(Session.getInstance().get("me")))
                    continue;

                Log.d("ReceivingTask","receive() add_relay");

                LocMessService.getInstance().MESSAGES().add("relay",message);


                Log.d("ReceivingTask", "receive() match");
                if(match(message)){
                    msg.put("type", MessageModel.MESSAGE_TYPE.RECEIVED.toString().toLowerCase());
                    LocMessService.getInstance().MESSAGES().add(new MessageModel(msg));
                }
            }
        }
        catch (JSONException e){
            Log.d("SendTask","received",e);
        }
    }

    private boolean match(MessageModel msg){
        try {
            Log.d("ReceivingTask","match 1");
            if(!new Profile().match(msg)) return false;
            Log.d("ReceivingTask","match 2");
            LocationModel loc = LocMessService.getInstance().LOCATIONS().find(msg.getLocation());
            Log.d("ReceivingTask","match 3");
            if(loc.getType() == LocationModel.LOCATION_TYPE.GPS) {
                Log.d("ReceivingTask","match 4");
                if (!LocMessService.getInstance().LOCATIONS().match(loc)) return false;
                Log.d("ReceivingTask","match 5");
            }
            Log.d("ReceivingTask","match 6");
            if(loc.getType() == LocationModel.LOCATION_TYPE.SSID) {
                Log.d("ReceivingTask","match 7");
                if (!LocMessService.getInstance().WIFIS().match(loc)) return false;
                Log.d("ReceivingTask","match 8");
            }
            Log.d("ReceivingTask","match 9");
        }
        catch (Exception e){
            Log.d("ReceivingTask","match",e);
            return false;
        }
        return true;
    }
}
