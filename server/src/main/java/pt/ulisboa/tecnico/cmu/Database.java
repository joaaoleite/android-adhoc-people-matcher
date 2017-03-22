package pt.ulisboa.tecnico.cmu;

import pt.ulisboa.tecnico.cmu.controllers.*;

public class Database{

    private static Users users;
    private static Locations locations;
    private static Messages messages;

    public static Users Users(){
        if(users == null) users = new Users();
        return users;
    }
    public static Locations Locations(){
        if(locations == null) locations = new Locations();
        return locations;
    }
    public static Messages Messages(){
        if(messages == null) messages = new Messages();
        return messages;
    }



}
