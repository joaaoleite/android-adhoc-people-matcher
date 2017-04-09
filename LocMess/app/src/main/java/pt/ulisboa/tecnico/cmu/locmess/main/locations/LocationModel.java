package pt.ulisboa.tecnico.cmu.locmess.main.locations;

import android.view.View;

public class LocationModel {

    private String type;
    private String name;
    private String ssid;
    private double latitude;
    private double longitude;
    private int radius;
    private String mac;

    private boolean selected;
    public View view;

    public LocationModel(String name, double latitude, double longitude, int radius){
        this.type = "GPS";
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.selected = false;
    }
    public LocationModel(String name, String ssid, String mac){
        this.type = "SSID";
        this.name = name;
        this.ssid = ssid;
        this.mac = mac;
        this.selected = false;
    }

    public String getType(){
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

    public void setType(String value){ this.type = value; }

    public void setSsid(String value){
        this.ssid = value;
    }

    public void setName(String value){ this.name = value; }

    public void setMac(String value){ this.mac = value; }

    public void setLongitude(double value){ this.longitude = value; }

    public void setLatitude(double value){ this.latitude = value; }

    public void setRadius(int value){ this.radius = value; }





    public boolean toogle(){
        this.selected = !this.selected;
        return this.selected;
    }

    public boolean isSelected(){
        return this.selected;
    }
}
