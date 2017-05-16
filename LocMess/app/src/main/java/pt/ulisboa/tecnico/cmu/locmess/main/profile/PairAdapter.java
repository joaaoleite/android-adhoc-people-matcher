package pt.ulisboa.tecnico.cmu.locmess.main.profile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.session.LocMessService;
import pt.ulisboa.tecnico.cmu.locmess.session.Session;

public class PairAdapter extends ArrayAdapter<PairModel>{

    private static class ViewHolder {
        TextView key;
        TextView value;
    }
    private int lastPosition = -1;
    public List<PairModel> list;

    public PairAdapter(Context context, List<PairModel> list){
        super(context, R.layout.layout_pair);
        this.list = list;
        notifyDataSetChanged();
    }

    public void deselectAllItems(){
        for(PairModel p : list){
            p.deselect();
        }
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
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
        Log.d("PairAdapter","notifyDataSetChanged to shared prefs");
        try{
            JSONObject json = new JSONObject();
            for(int i=0; i<list.size(); i++)
                json.put(list.get(i).getKey(),list.get(i).getValue());
            Session.getInstance().save("profile",json.toString());
        }
        catch (Exception e){
            Log.d("PairAdapter","notifyDataSetChanged error!");
        }
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
    public View getView(final int position, View view, ViewGroup parent) {

        PairModel pair = getItem(position);

        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.layout_pair, parent, false);
        }

        TextView key = (TextView) view.findViewById(R.id.key);
        TextView value = (TextView) view.findViewById(R.id.value);

        key.setText(pair.getKey());
        value.setText(pair.getValue());

        final View v = view;
        view.post(new Runnable() {
            @Override
            public void run() {
                v.setSelected(getItem(position).isSelected());
            }
        });

        return view;
    }

    public static JSONObject toJSON(Set<PairModel> profile) throws JSONException{
        JSONObject json = new JSONObject();
        for(PairModel pair : profile)
            json.put(pair.getKey(),pair.getValue());
        return json;
    }
}
