package pt.ulisboa.tecnico.cmu.models;

import org.json.JSONObject;

public class LocationSSID extends LocationAbstract {

	private String ssid;
	private String mac;

	public LocationSSID(String name, String ssid, String mac){
		super(name);
		this.ssid = ssid;
		this.mac = mac;
	}

	public String getSSID(){
		return this.ssid;
	}

	@Override
	public JSONObject toJSON(){
		JSONObject obj = new JSONObject();
		obj.put("name", this.name);
		obj.put("ssid", this.ssid);
		obj.put("mac", this.mac);
		return obj;
	}
}
