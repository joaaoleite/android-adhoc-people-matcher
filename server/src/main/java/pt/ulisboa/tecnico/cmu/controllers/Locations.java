package pt.ulisboa.tecnico.cmu.controllers;

import pt.ulisboa.tecnico.cmu.models.Model;
import pt.ulisboa.tecnico.cmu.models.LocationAbstract;
import pt.ulisboa.tecnico.cmu.models.LocationGPS;
import pt.ulisboa.tecnico.cmu.models.LocationSSID;
import pt.ulisboa.tecnico.cmu.exceptions.LocationNotFoundException;
import pt.ulisboa.tecnico.cmu.exceptions.LocationAlreadyExistsException;
import java.util.HashMap;
import java.lang.Math;
import java.util.HashSet;

public class Locations extends Controller{

    public HashSet<LocationAbstract> getLocations(){
        HashSet<LocationAbstract> res = new HashSet<LocationAbstract>();
        for(HashMap.Entry<String, Model> entry : this.entrySet()) res.add((LocationAbstract) entry.getValue());
        return res;
    }

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

    private boolean checkSSID(LocationSSID loc, HashSet<String> ssids){
        return ssids.contains(loc.getSSID());
    }

    public HashSet<LocationAbstract> getLocationsNearBy(double latitude, double longitude, HashSet<String> ssids){
        HashSet<LocationAbstract> res = new HashSet<LocationAbstract>();
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

    public void createLocation(String name, double latitude, double longitude, int radius) throws LocationAlreadyExistsException{
        if(super.get(name) != null) throw new LocationAlreadyExistsException(name);
        else super.put(name, new LocationGPS(name, latitude, longitude, radius));
    }

    public void createLocation(String name, String ssid, String mac) throws LocationAlreadyExistsException{
        if(super.get(name) != null) throw new LocationAlreadyExistsException(name);
        else super.put(name, new LocationSSID(name, ssid, mac));
    }

    public void removeLocation(String name) throws LocationNotFoundException{
        if(super.get(name) == null) throw new LocationNotFoundException(name);
        else super.remove(name);
    }

    public HashSet<String> getLocationMessagesID(String location) throws LocationNotFoundException{
        return this.getLocationByName(location).getMessagesID();
    }

    public void addMessageIDToLocation(String location, String id) throws LocationNotFoundException{
        this.getLocationByName(location).addMessageID(id);
    }

    public void removeMessageIDFromLocation(String location, String id) throws LocationNotFoundException{
        this.getLocationByName(location).removeMessageID(id);
    }

}
