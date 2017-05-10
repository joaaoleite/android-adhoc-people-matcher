package pt.ulisboa.tecnico.cmu.locmess.session.wifidirect;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

public class SendTask extends AsyncTask<String, String, Void> {

    private String ip;

    public SendTask(String ip){
        this.ip = ip;
    }

    @Override
    protected Void doInBackground(String... msg) {

        SimWifiP2pSocket socket;
        try{
            Log.d("WifiDirect","Sending to device: "+ip);
            socket = new SimWifiP2pSocket(ip, 10001);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

        try {
            OutputStream out = socket.getOutputStream();
            out.write(msg[0].getBytes());
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