package pt.ulisboa.tecnico.cmu.locmess.session.wifidirect;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
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

            String profile = LocMessService.prefs.getString("profile","{}");

            out.write((profile+"\n").getBytes());
            Log.d("SendTask","Sending keys "+profile);
            BufferedReader sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = sockIn.readLine();
            Log.d("SendTask","Response "+response);
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
}