package pt.ulisboa.tecnico.cmu.locmess.main.locations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pt.ulisboa.tecnico.cmu.locmess.R;

public class PairAdapter extends ArrayAdapter<PairModel> {

    private static class ViewHolder {
        TextView key;
        TextView value;
    }
    private int lastPosition = -1;
    public List<PairModel> list;

    public PairAdapter(Context context, List<PairModel> list){
        super(context, R.layout.layout_pair);
        this.list = list;
    }

    public void insertItem(PairModel p){

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getKey().equals(p.getKey())){
                list.get(i).setValue(p.getValue());
                notifyDataSetChanged();
                return;
            }
        }

        list.add(p);
        notifyDataSetChanged();
    }


    @Override
    public PairModel getItem(int position){
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        PairModel pair = getItem(position);

        ViewHolder holder;

        final View result;

        if (view == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.layout_pair, parent, false);

            holder.key = (TextView) view.findViewById(R.id.key);
            holder.value = (TextView) view.findViewById(R.id.value);

            result = view;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
            result = view;
        }

        lastPosition = position;
        holder.key.setText(pair.getKey());
        holder.value.setText(pair.getValue());

        return result;
    }

}
