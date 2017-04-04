package pt.ulisboa.tecnico.cmu.models;

import org.json.JSONObject;

public class LocationGPS extends LocationAbstract {

	private double latitude;
	private double longitude;
	private int radius;

	public LocationGPS(String name, double latitude, double longitude, int radius){
		super(name);
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
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

	@Override
	public JSONObject toJSON(){
		JSONObject obj = new JSONObject();
		obj.put("name", this.name);
		obj.put("latitude", this.latitude);
		obj.put("longitude", this.longitude);
		obj.put("radius", this.radius);
		return obj;
	}
}
