package pt.ulisboa.tecnico.cmu.locmess.main.messages;


import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;

import pt.ulisboa.tecnico.cmu.locmess.main.profile.PairModel;

public class MessageModel {
    private String location;
    private String user;
    private String subject;
    private String policy;
    private ArrayList<PairModel> filter;
    private Calendar start;
    private Calendar end;
    private String content;
    private String msgType;



    private boolean selected;
    public View view;

    public MessageModel(String location, String user, String subject, String policy, ArrayList<PairModel> filter, Calendar start, Calendar end, String content, String msgType){
        this.location = location;
        this.user = user;
        this.subject = subject;
        this.policy = policy;
        this.filter = filter;
        this.start = start;
        this.end = end;
        this.content = content;
        this.msgType = msgType;

        this.selected = false;
    }


    public String getLocation(){
        return this.location;
    }

    public String getUser(){
        return this.user;
    }

    public String getSubject(){
        return this.subject;
    }

    public String getPolicy(){
        return this.policy;
    }

    public ArrayList<PairModel> getFilter(){
        return this.filter;
    }

    public Calendar getStart(){
        return this.start;
    }

    public Calendar getEnd(){
        return this.end;
    }

    public String getContent(){
        return this.content;
    }

    public String getMsgType(){
        return this.msgType;
    }

    public void setLocation(String value){ this.location = value; }

    public void setUser(String value){
        this.user = value;
    }

    public void setSubject(String value){
        this.subject = value;
    }

    public void setPolicy(String value){ this.policy = value; }

    public void setFilter(ArrayList value){ this.filter = value; }

    public void setStart(Calendar value){ this.start = value; }

    public void setEnd(Calendar value){ this.end = value; }

    public void setContent(String value){ this.content = value; }





    public boolean toogle(){
        this.selected = !this.selected;
        return this.selected;
    }

    public boolean isSelected(){
        return this.selected;
    }
}
