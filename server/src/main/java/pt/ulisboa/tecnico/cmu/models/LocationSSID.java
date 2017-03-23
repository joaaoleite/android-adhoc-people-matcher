package pt.ulisboa.tecnico.cmu.models;

import org.json.JSONObject;

public class LocationSSID extends LocationAbstract {

	private String ssid;

	public LocationSSID(String name, String ssid){
		super(name);
		this.ssid = ssid;
	}

	public String getSSID(){
		return this.ssid;
	}

	@Override
	public JSONObject toJSON(){
		JSONObject obj = new JSONObject();
		obj.put("name", this.name);
		obj.put("ssid", this.ssid);
		return obj;
	}
}
