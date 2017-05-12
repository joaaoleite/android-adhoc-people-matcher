package pt.ulisboa.tecnico.cmu.locmess.session.wifidirect;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

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
        while (!Thread.currentThread().isInterrupted()) {
            try {
                SimWifiP2pSocket sock = mSrvSocket.accept();
                Log.d("WifiDirect", "receiving accept");
                try {
                    BufferedReader sockIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                    Log.d("ReceivingTask","sockIn");
                    String received = sockIn.readLine();
                    Log.d("ReceivingTask","Message received: "+received);
                    sock.getOutputStream().write(("OK\n").getBytes());
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
}
