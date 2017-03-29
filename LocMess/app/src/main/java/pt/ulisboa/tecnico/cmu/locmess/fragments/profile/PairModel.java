package pt.ulisboa.tecnico.cmu.locmess.fragments.profile;

public class PairModel {

    private String key;
    private String value;

    public PairModel(String key, String value){
        this.key = key;
        this.value = value;
    }

    public String getKey(){
        return this.key;
    }

    public String getValue(){
        return this.value;
    }
}
