package pt.ulisboa.tecnico.cmu.locmess.session.data;

import android.app.Service;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import pt.ulisboa.tecnico.cmu.locmess.main.locations.LocationModel;
import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessageModel;
import pt.ulisboa.tecnico.cmu.locmess.session.LocMessService;
import pt.ulisboa.tecnico.cmu.locmess.session.Session;
import pt.ulisboa.tecnico.cmu.locmess.session.requests.Request;

public class Locations implements LocationListener{

    private LocationManager manager;
    private Location location;
    private String bestProvider;
    private boolean givingLocation = false;

    public Locations(Service service){
        manager = (LocationManager) service.getSystemService(Context.LOCATION_SERVICE);
        bestProvider = String.valueOf(manager.getBestProvider(new Criteria(), true));
        try{
            manager.requestLocationUpdates(bestProvider, 10000, 1, this);
        }
        catch (SecurityException e){}
    }

    public void update(){
        new Request("GET","/locations"){
            @Override
            public void onResponse(JSONObject json) throws JSONException {
                JSONArray locs = json.getJSONArray("locations");
                Log.d("Locations","update locs="+locs);
                if(locs!=null && locs.length()>0) {
                    Session.getInstance().save("locations",locs.toString());
                }
            }
            @Override
            public void onError(String msg){

            }
        }.execute();
    }

    public void give(){
        givingLocation = true;
        HashMap<String,String> params = new HashMap<>();
        if(location!=null){
            params.put("latitude",location.getLatitude()+"");
            params.put("longitude",location.getLongitude()+"");
        }
        HashMap<String,ScanResult> ssids = LocMessService.getInstance().WIFIS().list();
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
                    Log.d("Locations","giveLocation json="+json);
                    if (json != null) {
                        JSONArray messages = json.getJSONArray("messages");
                        if(messages!=null) {
                            for (int i = 0; i < messages.length(); i++) {
                                JSONObject msg = messages.getJSONObject(i);
                                msg.put("type", MessageModel.MESSAGE_TYPE.RECEIVED.toString().toLowerCase());
                                msg.put("mode", MessageModel.MESSAGE_MODE.CENTRALIZED.toString().toLowerCase());
                                LocMessService.getInstance().MESSAGES().add(new MessageModel(msg));
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

    public Location get(){ return this.location; }

    public LocationModel find(String name){
        Log.d("Locations","find name="+name);
        for(LocationModel loc : list()) {
            Log.d("Locations","find for ="+loc.getName());
            if (loc.getName().equals(name))
                return loc;
        }
        return null;
    }

    public Set<LocationModel> list(){

        Set<LocationModel> locations = new HashSet<>();

        try{
            String saved = Session.getInstance().get("locations");
            Log.d("Locations","list saved="+saved);
            JSONArray json = new JSONArray();
            if(saved!=null)
                json = new JSONArray(saved);
            for(int i=0; i<json.length(); i++) {
                locations.add(new LocationModel(json.getJSONObject(i)));
                Log.d("Locations","list add="+locations);
            }
        }
        catch (JSONException e){
            Log.d("Locations","list",e);
        }

        return locations;
    }

    public boolean match(LocationModel loc){
        return this.match(loc.getLatitude(),loc.getLongitude(),loc.getRadius());
    }

    public boolean match(double lat, double lng, int radius){
        Log.d("Locations","match location="+location);

        Location targetLoc = new Location("");
        targetLoc.setLatitude(lat);
        targetLoc.setLongitude(lng);


        return targetLoc.distanceTo(location) <= radius;

    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
}
