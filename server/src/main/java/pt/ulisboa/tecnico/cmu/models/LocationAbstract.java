package pt.ulisboa.tecnico.cmu.models;

import org.json.JSONObject;
import java.util.HashSet;

public abstract class LocationAbstract extends Model {

	protected String name;
	protected HashSet<String> messagesID;

	public LocationAbstract(String name){
		this.name = name;
		this.messagesID = new HashSet<String>();
	}

	public HashSet<String> getMessagesID(){
		return this.messagesID;
	}

	public void addMessageID(String messageID){
		this.messagesID.add(messageID);
	}

	public void removeMessageID(String messageID){
		this.messagesID.remove(messageID);
	}

	public abstract JSONObject toJSON();
}
