package pt.ulisboa.tecnico.cmu;

public class Database{

    private Users users;
    private Locations locations;
    private Messages messages;

    public static Users Users(){
        if(this.users == null) this.users = new Users();
        return this.users;
    }
    public static Locations Locations(){
        if(this.locations == null) this.locations = new Locations();
        return this.locations;
    }
    public static Messages Messages(){
        if(this.messages == null) this.messages = new Messages();
        return this.messages;
    }



}
