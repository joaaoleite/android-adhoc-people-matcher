package pt.ulisboa.tecnico.cmu.controllers;

import pt.ulisboa.tecnico.cmu.models.Model;
import pt.ulisboa.tecnico.cmu.models.LocationAbstract;
import pt.ulisboa.tecnico.cmu.models.LocationGPS;
import pt.ulisboa.tecnico.cmu.models.LocationSSID;
import pt.ulisboa.tecnico.cmu.exceptions.LocationNotFoundException;
import pt.ulisboa.tecnico.cmu.exceptions.LocationAlreadyExistsException;
import java.util.HashMap;
import java.util.ArrayList;
import java.lang.Math;

public class Locations extends Controller{

    public LocationAbstract getLocationByName(String name) throws LocationNotFoundException{
        LocationAbstract loc = (LocationAbstract) get(name);
        if(loc == null) throw new LocationNotFoundException(name);
        else return loc;
    }

    private boolean checkGPS(LocationGPS loc, double latitude, double longitude){
        return loc.getRadius() >= Math.acos(Math.sin(Math.toRadians(latitude)) * Math.sin(Math.toRadians(loc.getLatitude())) +
		      Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(loc.getLatitude())) *
		      Math.cos(Math.toRadians(longitude) - Math.toRadians(loc.getLongitude()))) * 6371010;
    }

    private boolean checkSSID(LocationSSID loc, ArrayList<String> ssids){
        return ssids.contains(loc.getSSID());
    }

    public ArrayList<LocationAbstract> getNearBy(double latitude, double longitude, ArrayList<String> ssids){
        ArrayList<LocationAbstract> res = new ArrayList<LocationAbstract>();
        for(HashMap.Entry<String, Model> entry : this.entrySet()){
            if(entry.getValue() instanceof LocationGPS){
                if(checkGPS((LocationGPS) entry.getValue(), latitude, longitude)) res.add((LocationGPS) entry.getValue());
            }
            else if(entry.getValue() instanceof LocationSSID){
                if(checkSSID((LocationSSID) entry.getValue(), ssids)) res.add((LocationSSID) entry.getValue());
            }
		}
        return res;
    }

    public void put(String name, double latitude, double longitude, int radius) throws LocationAlreadyExistsException{
        if(get(name) != null) throw new LocationAlreadyExistsException(name);
        else super.put(name, new LocationGPS(name, latitude, longitude, radius));
    }

    public void put(String name, String ssid) throws LocationAlreadyExistsException{
        if(get(name) != null) throw new LocationAlreadyExistsException(name);
        else super.put(name, new LocationSSID(name, ssid));
    }

    public void remove(String name) throws LocationNotFoundException{
        if(get(name) == null) throw new LocationNotFoundException(name);
        else super.remove(name);
    }


}
