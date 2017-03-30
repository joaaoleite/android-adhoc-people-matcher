package pt.ulisboa.tecnico.cmu.locmess.fragments.profile;

import android.view.View;

public class PairModel {

    private String key;
    private String value;
    private boolean selected;
    public View view;

    public PairModel(String key, String value){
        this.key = key;
        this.value = value;
        this.selected = false;
    }

    public String getKey(){
        return this.key;
    }

    public String getValue(){
        return this.value;
    }

    public boolean toogle(){
        this.selected = !this.selected;
        return this.selected;
    }

    public boolean isSelected(){
        return this.selected;
    }
}
