package pt.ulisboa.tecnico.cmu.locmess.session.data;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.cmu.locmess.main.locations.LocationModel;
import pt.ulisboa.tecnico.cmu.locmess.session.LocMessService;

public class Wifis {

    private Service service;
    private BroadcastReceiver receiver;
    private final WifiManager wifi;

    private HashMap<String,ScanResult> ssids;


    public Wifis(Service service){
        this.wifi = (WifiManager) service.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        this.service = service;
    }

    public void update(){

        if(!wifi.isWifiEnabled())
            wifi.setWifiEnabled(true);

        receiver = new BroadcastReceiver() {
               @Override
               public void onReceive(Context c, Intent intent) {
                   HashMap<String,ScanResult> tmp = new HashMap<>();
                    List<ScanResult> results = wifi.getScanResults();
                    for (int i = 0; i < results.size(); i++)
                        if(!ssids.containsKey(results.get(i).SSID))
                            ssids.put(results.get(i).SSID,results.get(i));
                }
        };

        service.registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi.startScan();
    }

    public boolean match(LocationModel loc){
        return this.match(loc.getSsid());
    }

    public boolean match(String ssid){
        if(ssids!=null){
            boolean match = ssids.containsKey(ssid);
            Log.d("ReceivingTask","matchWifi match="+match);
            return match;
        }else{
            return false;
        }
    }

    public HashMap<String,ScanResult> list(){
        return ssids;
    }
}
