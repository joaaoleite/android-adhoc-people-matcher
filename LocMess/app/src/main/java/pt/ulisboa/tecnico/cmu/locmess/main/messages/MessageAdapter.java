package pt.ulisboa.tecnico.cmu.locmess.main.messages;


import android.content.Context;
import android.util.Log;
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
import java.util.Date;
import java.util.List;
import java.util.Set;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.PairModel;
import pt.ulisboa.tecnico.cmu.locmess.session.LocMessService;
import pt.ulisboa.tecnico.cmu.locmess.session.Session;

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

        try {
            Set<MessageModel> received = LocMessService.getInstance().MESSAGES().received();
            if (received != null)
                this.list.addAll(received);

            Log.d("MessageAdapter","received = "+received);

            Set<MessageModel> sent = LocMessService.getInstance().MESSAGES().sent();
            if (sent != null)
                this.list.addAll(sent);

            Log.d("MessageAdapter","sent = "+sent);
        }
        catch (NullPointerException e){
            Log.d("MessageAdapter","constructor",e);
        }

        notifyDataSetChanged();
        this.msgType = "all";
    }

    public void insertItem(MessageModel p){
        list.add(p);
        notifyDataSetChanged();
    }

    @Override
    public MessageModel getItem(int position){
        int i = 0;
        try {
            for (MessageModel m : list) {
                if (m.getType().toString().toLowerCase().equals(msgType) || msgType.equals("all")) {
                    if (position == i) {
                        return m;
                    }
                    i++;
                }
            }
        }catch (NullPointerException e){}
        return null;
    }

    public void setMsgType(String type){
        this.msgType = type;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        if(msgType.equals("all")) return list.size();

        int count = 0;
        for(MessageModel m : list){
            if(m.getType().toString().toLowerCase().equals(msgType))
                count++;
        }
        return count;
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
        holder.subject.setText(message.getContent());

        return result;
    }

}
