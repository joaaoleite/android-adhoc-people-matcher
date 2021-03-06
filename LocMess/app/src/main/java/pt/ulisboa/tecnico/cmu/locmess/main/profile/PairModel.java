package pt.ulisboa.tecnico.cmu.locmess.main.profile;

import android.view.View;

import org.json.JSONObject;

public class PairModel {

    private String key;
    private String value;
    private boolean selected;

    public PairModel(String key, String value){
        this.key = key;
        this.value = value;
        this.selected = false;
    }

    public String getKey(){
        return this.key;
    }

    public void setValue(String value){ this.value = value; }

    public String getValue(){
        return this.value;
    }

    public boolean toogle(){
        this.selected = !this.selected;
        return this.selected;
    }
    public void deselect(){
        this.selected = false;
    }

    public boolean isSelected(){
        return this.selected;
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        try {
            json.put("key", key);
            json.put("value", value);
            return json;
        }
        catch (Exception e){
            return null;
        }
    }
}
