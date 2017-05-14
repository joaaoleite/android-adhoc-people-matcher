package pt.ulisboa.tecnico.cmu.locmess.session;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessageAdapter;
import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessageModel;
import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessageViewer;
import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessagesFragment;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.PairModel;
import pt.ulisboa.tecnico.cmu.locmess.session.requests.Request;
import pt.ulisboa.tecnico.cmu.locmess.session.wifidirect.SimWifiP2pBroadcastReceiver;
import pt.ulisboa.tecnico.cmu.locmess.session.wifidirect.WifiDirect;

public class LocMessService extends Service implements LocationListener{

    private BroadcastReceiver receiver;
    public static HashMap<String,ScanResult> ssids;
    public static Location location;
    private boolean givingLocation = false;
    public static SharedPreferences prefs;

    private LocationManager manager;
    private String bestProvider;

    private static Context context;
    private static LocMessService singleton;
    public static Context getContext(){
        return context;
    }


    private Runnable background = new Runnable() {
        @Override
        public void run() {
            Log.d("Service","background");

            WifiDirect wifiDirect = new WifiDirect(LocMessService.this);


            while(true) {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                }
                wifiDirect.getDevices();
                getLocation();
                getSSIDs();
            }
        }
    };

    // Service default methods
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        context = getApplicationContext();
        prefs = getSharedPreferences(Session.APP_NAME,context.MODE_PRIVATE);
        Log.d("Service","onCreate");
        singleton = this;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service","onStartCommand");

        try{
            if(checkPermissions()) {
                manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                bestProvider = String.valueOf(manager.getBestProvider(new Criteria(), true));
                manager.requestLocationUpdates(bestProvider, 10000, 1, LocMessService.this);
            }
        }catch (SecurityException e){}


        new Thread(background).start();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {}

    // Service logic
    private boolean checkPermissions(){
        return ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void getLocation(){
        Log.d("Service","getLocation");
        if(checkPermissions()){
            try {
                location = manager.getLastKnownLocation(bestProvider);
                if(location!=null)
                    Log.d("Service","getLocation="+location.getLatitude()+","+location.getLongitude());
                if(!givingLocation) giveLocation();
            }
            catch (SecurityException e){ }
        }
    }
    public void getSSIDs(){
        Log.d("Service","getSSIDs");
        if(checkPermissions()) {
            Log.d("Service","check permissions ok");
            Log.d("Service","permissions ok");
            final WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if(!wifi.isWifiEnabled()){
                wifi.setWifiEnabled(true);
            }
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context c, Intent intent) {
                    ssids = new HashMap<>();
                    List<ScanResult> results = wifi.getScanResults();
                    int size = results.size();
                    for (int i = 0; i < size; i++)
                        if(!ssids.containsKey(results.get(i).SSID))
                            ssids.put(results.get(i).SSID,results.get(i));
                    if(!givingLocation) giveLocation();
                }
            };

            registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifi.startScan();
        }
    }

    private void giveLocation(){
        givingLocation = true;
        HashMap<String,String> params = new HashMap<>();
        if(location!=null){
            params.put("latitude",location.getLatitude()+"");
            params.put("longitude",location.getLongitude()+"");
        }
        if(ssids!=null){
            int i = 1;
            for (String key : ssids.keySet()) {
                params.put("ssid"+i,key);
                i++;
            }
        }
        if(params.size()>0) {
            Log.d("Service","giveLocation");
            new Request("PUT", "/locations/now", params) {
                @Override
                public void onResponse(JSONObject json) throws JSONException {
                    Log.d("Service","1");
                    if (json != null) {
                        Log.d("Service","2");
                        JSONArray messages = json.getJSONArray("messages");
                        Log.d("Service","3");
                        if(messages!=null) {
                            Log.d("Service","4");
                            for (int i = 0; i < messages.length(); i++) {
                                Log.d("Service","5");
                                message(messages.getJSONObject(i));
                                Log.d("Service","6");
                            }
                        }
                    }
                    givingLocation = false;
                }

                @Override
                public void onError(String error) {
                    givingLocation = false;
                }
            }.execute();
        }
    }
    public static void message(JSONObject msg){
        Log.d("LocMessService","message "+msg.toString());
        try{

            Set<String> set = prefs.getStringSet("received", null);
            if(set==null) set = new HashSet<String>();
            for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
                String obj = it.next();
                if (new JSONObject(obj).getString("id").equals(msg.getString("id"))) return;
            }
            set.add(msg.toString());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putStringSet("received", set);
            editor.commit();

            MessagesFragment mf = MessagesFragment.newInstance();
            if(mf!=null) {
                if(mf.adapter!=null){
                    mf.adapter.insertItem(msg);
                }
            }
            Log.d("LocMessService","singleton.launchNotification(msg)" );
            singleton.launchNotification(msg);
        }
        catch (Exception e){
            Log.d("LocMessService","message",e);
        }
    }

    // Launch  notification
    public void launchNotification(JSONObject message) {

        Log.d("LocMessService","launchNotification "+message);

        try {
            MessageModel msg = new MessageModel(message);

            final Intent intent = new Intent(this, MessageViewer.class);
            intent.putExtra("user", msg.getUser());
            intent.putExtra("subject", msg.getContent());
            intent.putExtra("content", msg.getContent());
            intent.putExtra("type", msg.getMsgType());
            intent.putExtra("location", msg.getContent());
            intent.putExtra("policy", msg.getPolicy());

            String filter = "";
            for (int i = 0; i < msg.getFilter().size(); i++) {
                filter = filter + msg.getFilter().get(i).getKey() + " - " + msg.getFilter().get(i).getValue() + "\n";
            }
            intent.putExtra("filter", filter);

            String start = msg.getStart().getTime() + "";
            String end = msg.getEnd().getTime() + "";
            start = start.split(" ")[1] + " " + start.split(" ")[2] + " " + start.split(" ")[5] + " at " + start.split(" ")[3].split(":")[0] + ":" + start.split(" ")[3].split(":")[1];
            end = end.split(" ")[1] + " " + end.split(" ")[2] + " " + end.split(" ")[5] + " at " + end.split(" ")[3].split(":")[0] + ":" + end.split(" ")[3].split(":")[1];
            intent.putExtra("start", start);
            intent.putExtra("end", end);

            intent.putExtra("position", 0);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("New message received!")
                            .setContentText("From: " + msg.getUser())
                            .setContentIntent(pendingIntent).setAutoCancel(true);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(new Random().nextInt(), mBuilder.build());
        }
        catch (Exception e){
            Log.d("LocMessService","launchNotification",e);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
