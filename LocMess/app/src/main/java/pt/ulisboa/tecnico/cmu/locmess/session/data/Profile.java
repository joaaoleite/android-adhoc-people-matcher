package pt.ulisboa.tecnico.cmu.locmess.session.data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessageModel;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.PairModel;
import pt.ulisboa.tecnico.cmu.locmess.session.LocMessService;
import pt.ulisboa.tecnico.cmu.locmess.session.Session;

public class Profile {
    private Set<PairModel> keys;

    public Profile(Set<PairModel> keys){
        this.keys = keys;
    }

    public Profile() throws JSONException{
        keys = new HashSet<>();
        String profile = Session.getInstance().get("profile");
        JSONObject json = new JSONObject(profile);

        Iterator<?> item = json.keys();
        while( item.hasNext() ) {
            String key = (String)item.next();
            keys.add(new PairModel(key,json.getString(key)));
        }
    }

    public boolean match(MessageModel msg){
        Set<MessageModel> list = new HashSet<MessageModel>();
        list.add(msg);
        Set<MessageModel> res = match(list);
        return res.size()>0;
    }

    public Set<MessageModel> match(Set<MessageModel> msgs){
        Set<MessageModel> res = new HashSet<>();

        Log.d("Profile","match -1");

        msgloop:
        for(MessageModel msg : msgs){

            Log.d("Profile","match 0");

            if(!msg.isNow()) continue msgloop;

            Log.d("Profile","match isNow="+true);

            Set<PairModel> msgKeys = msg.getFilter();
            MessageModel.MESSAGE_POLICY policy = msg.getPolicy();

            Log.d("Profile","msgsKeys = "+msgKeys);
            Log.d("Profile","keys = "+keys);
            if(policy == MessageModel.MESSAGE_POLICY.WHITELIST) {
                keysloop:
                for (PairModel p1 : msgKeys) {
                    for (PairModel p2 : keys)
                        if (p1.getKey().equals(p2.getKey()) && p1.getValue().equals(p2.getValue()))
                            continue keysloop;
                    continue msgloop;
                }
            }

            Log.d("Profile","match 2");

            if(policy == MessageModel.MESSAGE_POLICY.BLACKLIST)
                for(PairModel p1 : msgKeys)
                    for(PairModel p2 : keys)
                        if(p1.getKey().equals(p2.getKey()) && p2.getValue().equals(p2.getValue()))
                            continue msgloop;

            Log.d("Profile","match 3");

            res.add(msg);
        }
        return res;
    }
}
