package pt.ulisboa.tecnico.cmu.locmess.main.locations;

import android.location.Location;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationModel {

    private LOCATION_TYPE type;
    private String name;
    private String ssid;
    private double latitude;
    private double longitude;
    private int radius;
    private String mac;

    private boolean selected;
    public View view;

    public LocationModel(JSONObject json) throws JSONException {
        this.name = json.getString("name");
        if(json.has("ssid")){
            this.type = LOCATION_TYPE.SSID;
            this.ssid = json.getString("ssid");
            this.mac = json.getString("mac");
        }
        else {
            this.type = LOCATION_TYPE.GPS;
            this.latitude = json.getDouble("latitude");
            this.longitude = json.getDouble("longitude");
            this.radius = json.getInt("radius");
        }
    }

    public LocationModel(String name, double latitude, double longitude, int radius){
        this.type = LOCATION_TYPE.GPS;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.selected = false;
    }
    public LocationModel(String name, String ssid, String mac){
        this.type = LOCATION_TYPE.SSID;
        this.name = name;
        this.ssid = ssid;
        this.mac = mac;
        this.selected = false;
    }

    public LOCATION_TYPE getType(){
        return this.type;
    }

    public String getName(){
        return this.name;
    }

    public String getSsid(){
        return this.ssid;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public int getRadius(){
        return this.radius;
    }

    public String getMac(){
        return this.mac;
    }


    public void setSsid(String value){
        this.ssid = value;
    }

    public void setName(String value){ this.name = value; }

    public void setMac(String value){ this.mac = value; }

    public void setLongitude(double value){ this.longitude = value; }

    public void setLatitude(double value){ this.latitude = value; }

    public void setRadius(int value){ this.radius = value; }

    public JSONObject toJSON() throws Exception{
        JSONObject json = new JSONObject();
        json.put("type",type);
        json.put("name",name);
        if(ssid!=null) {
            json.put("ssid", ssid);
            json.put("mac",mac);
        }
        else {
            json.put("latitude", latitude);
            json.put("longitude", longitude);
            json.put("radius", radius);
        }
        return json;
    }

    public boolean toogle(){
        this.selected = !this.selected;
        return this.selected;
    }

    public boolean isSelected(){
        return this.selected;
    }

    public enum LOCATION_TYPE {
        GPS, SSID
    }
}
