package pt.ulisboa.tecnico.cmu.locmess.session.data;

import android.app.Service;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessageModel;
import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessagesFragment;
import pt.ulisboa.tecnico.cmu.locmess.session.LocMessService;
import pt.ulisboa.tecnico.cmu.locmess.session.Session;

public class Messages {

    private Service service;

    public Messages(Service service){
        this.service = service;
    }

    // -------------

    public void add(MessageModel msg){
        add(msg.getType().toString().toLowerCase(),msg);
    }
    public void add(String key, final MessageModel msg){
        Log.d("Messages","add key="+key+" msg="+msg.getId());
        try{
            JSONObject json = msg.toJSON();
            String saved = Session.getInstance().get(key);
            JSONArray list = new JSONArray();
            if(saved!=null)
                list = new JSONArray(saved);

            for(int i=0; i<list.length(); i++)
                if(list.getJSONObject(i).getString("id").equals(json.getString("id")))
                    return;
            list.put(json);

            if(key.equals("relay")){
                JSONArray relay = new JSONArray();
                JSONArray previous = new JSONArray();
                for (int i = list.length()-1; i>=0; i--)
                    previous.put(list.getJSONArray(i));

                int limit = 100;
                try{ limit = Integer.parseInt(Session.getInstance().get("max")); }
                catch (Exception e){ }
                for(int i=0; i<previous.length() || i<limit; i++){
                    relay.put(previous.getJSONArray(i));
                }
                Session.getInstance().save(key,relay.toString());
            }
            else Session.getInstance().save(key,list.toString());

            if(msg.getType() == MessageModel.MESSAGE_TYPE.RECEIVED) {
                LocMessService.getInstance().notification(msg);
                try {
                    MessagesFragment.newInstance().getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MessagesFragment.newInstance().adapter.insertItem(msg);
                        }
                    });
                }
                catch (NullPointerException e){}
            }


        }
        catch (JSONException e){
            Log.d("Messages","add",e);
        }
    }

    // -------------

    public void remove(String key, String id){
        Log.d("Messages","remove key="+key);
        try {
            String received = Session.getInstance().get(key);
            if(received==null) return;
            JSONArray saved = new JSONArray(received);
            JSONArray list = new JSONArray();
            for(int i=0; i<saved.length(); i++){
                if(!saved.getJSONObject(i).getString("id").equals(id)) {
                    list.put(saved.getJSONObject(i));
                }
                else{
                    Log.d("Messages","remove key="+key+" id="+id);
                    if(key.equals(MessageModel.MESSAGE_TYPE.RECEIVED.toString().toLowerCase())){
                        Log.d("Messages","removed=true");
                        JSONObject a=saved.getJSONObject(i);
                        a.put("removed",true);
                        list.put(a);
                    }
                }
            }
            Session.getInstance().save(key,list.toString());
        }
        catch (JSONException e){
            Log.d("Messages","remove ex=",e);
        }
    }
    public void remove(String id){
        Log.d("Messages","remove id="+id);
        remove(MessageModel.MESSAGE_TYPE.RECEIVED.toString().toLowerCase(),id);
        remove(MessageModel.MESSAGE_TYPE.SENT.toString().toLowerCase(),id);
        remove("relay",id);
    }

    // -------------

    public MessageModel find(String id){
        try{
            ArrayList<MessageModel> list = (ArrayList<MessageModel>) MessagesFragment.newInstance().adapter.list;
            for(int i=0; i<list.size(); i++)
                if(list.get(i).getId().equals(id))
                    return list.get(i);
        }
        catch (Exception e){}

        Set<MessageModel> all = get(MessageModel.MESSAGE_TYPE.RECEIVED.toString().toLowerCase());
        Set<MessageModel> sent = get(MessageModel.MESSAGE_TYPE.SENT.toString().toLowerCase());
        all.addAll(sent);
        for(MessageModel msg : all)
            if(msg.getId().equals(id))
                return msg;

        return null;
    }

    // -------------

    private Set<MessageModel> get(String key){
        Set<MessageModel> messages = new HashSet<>();
        String value = Session.getInstance().get(key);
        try {
            JSONArray json = new JSONArray();
            if(value!=null)
                json = new JSONArray(value);

            for (int i=0; i < json.length(); i++) {
                try {
                    if(!json.getJSONObject(i).has("removed"))
                        messages.add(new MessageModel(json.getJSONObject(i)));
                }
                catch (JSONException e) {
                    Log.d("Messages","get",e);
                }
            }
        }
        catch (JSONException e){}
        return messages;
    }
    public Set<MessageModel> received(){
        return get(MessageModel.MESSAGE_TYPE.RECEIVED.toString().toLowerCase());
    }
    public Set<MessageModel> sent(){
        return get(MessageModel.MESSAGE_TYPE.SENT.toString().toLowerCase());
    }
    public Set<MessageModel> relay(){
        return get("relay");
    }
}
