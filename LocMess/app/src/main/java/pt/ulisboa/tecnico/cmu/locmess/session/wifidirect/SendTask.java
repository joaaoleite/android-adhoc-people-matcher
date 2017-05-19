package pt.ulisboa.tecnico.cmu.locmess.session.wifidirect;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessageModel;
import pt.ulisboa.tecnico.cmu.locmess.session.LocMessService;
import pt.ulisboa.tecnico.cmu.locmess.session.Session;

public class SendTask extends AsyncTask<Void, String, Void> {

    private String ip;

    public SendTask(String ip){
        this.ip = ip;
    }

    @Override
    protected Void doInBackground(Void... params) {

        SimWifiP2pSocket socket;
        try{
            Log.d("WifiDirect","Sending to device: "+ip);
            socket = new SimWifiP2pSocket(ip, 10001);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        if(socket==null) return null;

        try {
            OutputStream out = socket.getOutputStream();
            String sending = send();
            Log.d("SendTask","sending="+sending);
            out.write((sending+"\n").getBytes());
            Log.d("SendTask","Sending messages");
            BufferedReader sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = sockIn.readLine();
            Log.d("SendTask","Response="+response);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.d("WifiDirect","Sending complete!");
    }

    private String send(){

        Set<MessageModel> relay = LocMessService.getInstance().MESSAGES().relay();
        Set<MessageModel> sent = LocMessService.getInstance().MESSAGES().sent();
        HashSet<MessageModel> tosend = new HashSet<>();

        for(MessageModel msg : sent)
            tosend.add(msg);

        for(MessageModel msg : relay) {
            Log.d("SendTask","send() relay="+msg.toString());
            if(tosend.size()>0) {
                for (MessageModel other : tosend) {
                    Log.d("SendTask", "send() now=" + msg.isNow());
                    if (msg.getId().equals(other.getId()) || !msg.isNow()) {
                        Log.d("SendTask", "send() 123");
                        LocMessService.getInstance().MESSAGES().remove("relay", msg.getId());
                    } else
                        tosend.add(msg);
                }
            }
            else tosend.add(msg);
        }

        JSONArray json = new JSONArray();
        for(MessageModel msg : tosend) {
            try {
                json.put(msg.toJSON());
            }
            catch (JSONException e){}
        }
        return json.toString();
    }

}
