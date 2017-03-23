package pt.ulisboa.tecnico.cmu.models;

import org.json.JSONObject;

public abstract class LocationAbstract extends Model {

	protected String name;

	public LocationAbstract(String name){
		this.name = name;
	}

	public abstract JSONObject toJSON();
}
