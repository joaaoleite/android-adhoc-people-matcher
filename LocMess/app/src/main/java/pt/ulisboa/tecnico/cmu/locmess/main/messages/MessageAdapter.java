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
    private int lastPosition = -1;
    public List<MessageModel> list;

    public MessageAdapter(Context context, List<MessageModel> list){
        super(context, R.layout.layout_messages);
        this.list = list;
    }

    public void insertItem(MessageModel p){

        list.add(p);
        notifyDataSetChanged();
    }


    @Override
    public MessageModel getItem(int position){
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        MessageModel location = list.get(position);
        ViewHolder holder;

        final View result;

        if (view == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.layout_locations, parent, false);

            holder.user = (TextView) view.findViewById(R.id.user);
            holder.subject = (TextView) view.findViewById(R.id.subject);

            result = view;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
            result = view;
        }

        lastPosition = position;
        holder.user.setText(location.getUser());
        holder.subject.setText(location.getSubject());

        return result;
    }

}
