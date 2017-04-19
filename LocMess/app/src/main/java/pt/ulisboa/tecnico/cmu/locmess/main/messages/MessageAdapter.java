package pt.ulisboa.tecnico.cmu.locmess.main.messages;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pt.ulisboa.tecnico.cmu.locmess.R;

public class MessageAdapter  extends ArrayAdapter<MessageModel> {

    private static class ViewHolder {
        TextView user;
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

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        MessageModel message = getItem(position);


        ViewHolder holder;

        final View result;

        if (view == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.layout_messages, parent, false);

            holder.user = (TextView) view.findViewById(R.id.user);
            holder.subject = (TextView) view.findViewById(R.id.subject);

            result = view;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
            result = view;
        }

        lastPosition = position;
        holder.user.setText(message.getUser());
        holder.subject.setText(message.getSubject());

        return result;
    }

}
