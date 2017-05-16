package pt.ulisboa.tecnico.cmu.locmess.session.data;

import android.app.Service;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import pt.ulisboa.tecnico.cmu.locmess.main.locations.LocationModel;
import pt.ulisboa.tecnico.cmu.locmess.session.LocMessService;
import pt.ulisboa.tecnico.cmu.locmess.session.Session;
import pt.ulisboa.tecnico.cmu.locmess.session.requests.Request;

public class Locations implements LocationListener{

    private LocationManager manager;
    private Location location;
    private String bestProvider;

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

    }

    public Location get(){ return this.location; }

    public LocationModel find(String name){
        for(LocationModel loc : list())
            if(loc.getName().equals(name))
                return loc;
        return null;
    }

    public Set<LocationModel> list(){

        Set<LocationModel> locations = new HashSet<>();

        try{
            String saved = Session.getInstance().get("locations");
            JSONArray json = new JSONArray();
            if(saved!=null)
                json = new JSONArray(saved);
            for(int i=0; i<json.length(); i++)
                locations.add(new LocationModel(json.getJSONObject(i)));
        }
        catch (JSONException e){}

        return locations;
    }

    public boolean match(LocationModel loc){
        return this.match(loc.getLatitude(),loc.getLongitude(),loc.getRadius());
    }

    public boolean match(double lat, double lng, int radius){
        if(location!=null){
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            boolean match = radius >= Math.acos(Math.sin(Math.toRadians(latitude)) * Math.sin(Math.toRadians(lat) +
                    Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(lat)) *
                            Math.cos(Math.toRadians(longitude) - Math.toRadians(lng)))) * 6371010;

            return match;
        }else{
            return false;
        }
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
