package pt.ulisboa.tecnico.cmu.locmess.main.messages;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.PairModel;

public class MessageAdapter  extends ArrayAdapter<MessageModel> {

    private static class ViewHolder {
        TextView location;
        TextView subject;
    }
    private String msgType;
    private int lastPosition = -1;
    public List<MessageModel> list;

    public MessageAdapter(Context context, List<MessageModel> list){
        super(context, R.layout.layout_messages);
        this.list = list;
        this.msgType = "All";
    }

    public void insertItem(JSONObject msg) {
        try {
            MessageModel message = parse(msg,"Received");
            list.add(message);
            notifyDataSetChanged();
        }catch(Exception e){}
    }

    public void insertItem(MessageModel p){

        list.add(p);
        notifyDataSetChanged();
    }




    @Override
    public MessageModel getItem(int position){
        int i = 0;
        for(MessageModel m : list) {
            if(m.getMsgType().equals(msgType) || msgType.equals("All")) {
                if(position == i) {
                    return m;
                }
                i++;
            }
        }
        return null;
    }

    public void setMsgType(String type){
        this.msgType = type;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        if(msgType.equals("All")) return list.size();

        int count = 0;
        for(MessageModel m : list){
            if(m.getMsgType().equals(msgType))
                count++;
        }
        return count;
    }
    public static MessageModel parse(String msg, String type) {
        try{
            return parse(new JSONObject(msg),type);
        }
        catch (JSONException e){}
        return null;
    }
    public static MessageModel parse(JSONObject msg, String type){
        try {
            String location = msg.getString("location");
            String sender = msg.getString("user");
            String content = msg.getString("content");
            String policy = msg.getString("policy");
            String id = msg.getString("id");

            Calendar start = Calendar.getInstance();
            String[] s = msg.getString("start").split(" ");
            start.setTime(new SimpleDateFormat("yyyy-MMM-dd H:m:s").parse(s[5] + "-" + s[1] + "-" + s[2] + " " + s[3]));
            Calendar end = Calendar.getInstance();
            String[] e = msg.getString("end").split(" ");
            end.setTime(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss").parse(e[5] + "-" + e[1] + "-" + e[2] + " " + e[3]));

            ArrayList<PairModel> pairs = new ArrayList<>();
            JSONObject tags = msg.getJSONObject("keys");
            if (tags != null) {
                if (tags.names() != null) {
                    for (int j = 0; j < tags.names().length(); j++)
                        pairs.add(new PairModel(tags.names().getString(j), tags.getString(tags.names().getString(j))));

                    return new MessageModel(id, location, sender, content, policy, pairs, start, end, type);
                }
            }
        }
        catch (JSONException e) { }
        catch (ParseException e){ }
        return null;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        MessageModel message = getItem(position);


        ViewHolder holder;

        final View result;

        if (view == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.layout_messages, parent, false);

            holder.location = (TextView) view.findViewById(R.id.location);
            holder.subject = (TextView) view.findViewById(R.id.subject);

            result = view;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
            result = view;
        }

        lastPosition = position;
        holder.location.setText(message.getLocation());
        holder.subject.setText(message.getSubject());

        return result;
    }

}
