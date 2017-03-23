package pt.ulisboa.tecnico.cmu.locmess.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.locmess.R;

public class KeyValueAdapter extends ArrayAdapter<KeyValue>{

    public KeyValueAdapter(Context context, ArrayList<KeyValue> pairs) {
        super(context, 0, pairs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        KeyValue pair = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_keyvalue, parent, false);

        // Lookup view for data population
        EditText key = (EditText) convertView.findViewById(R.id.key);
        EditText value = (EditText) convertView.findViewById(R.id.value);

        // Populate the data into the template view using the data object
        key.setText(pair.key);
        value.setText(pair.value);

        // Return the completed view to render on screen
        return convertView;
    }
}
