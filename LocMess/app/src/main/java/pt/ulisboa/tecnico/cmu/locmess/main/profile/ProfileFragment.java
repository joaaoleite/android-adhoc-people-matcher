package pt.ulisboa.tecnico.cmu.locmess.main.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.MainActivity;
import pt.ulisboa.tecnico.cmu.locmess.main.MyFragment;
import pt.ulisboa.tecnico.cmu.locmess.session.Request;

public class ProfileFragment extends MyFragment implements AdapterView.OnItemLongClickListener  {

    private static ProfileFragment singleton;
    private int selected;
    private View view;
    private ListView list;
    private PairAdapter adapter;

    private HashMap<String,String[]> autocomplete = new HashMap<>();


    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        Log.d("profile","newInstance");
        if(singleton == null) singleton = new ProfileFragment();
        return singleton;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle state) {
        if(view==null) {
            this.view = inflater.inflate(R.layout.fragment_profile, container, false);
            this.list = (ListView) view.findViewById(R.id.list);
            final Fragment f = this;
            restoreState(state);
            adapter();
            getServerKeysAutoComplete();

            FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fab);
            myFab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(f.getContext());
                    final View mView = layoutInflaterAndroid.inflate(R.layout.user_input_profile, container, false);
                    AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(f.getContext());
                    alertDialogBuilderUserInput.setView(mView);

                    final AutoCompleteTextView etKeyInputDialog = (AutoCompleteTextView) mView.findViewById(R.id.keyInputDialog);
                    final AutoCompleteTextView etValueInputDialog = (AutoCompleteTextView) mView.findViewById(R.id.valueInputDialog);

                    etValueInputDialog.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(hasFocus) {
                                String[] values = getServerValuesAutoComplete(etKeyInputDialog.getText().toString().toLowerCase());
                                ArrayAdapter<String> autocomplete = new ArrayAdapter<String>
                                        (view.getContext(), R.layout.autocomplete_item,values);
                                etValueInputDialog.setAdapter(autocomplete);
                            }
                        }
                    });

                    String[] keys = getServerKeysAutoComplete();
                    ArrayAdapter<String> autocomplete = new ArrayAdapter<String>
                            (view.getContext(), R.layout.autocomplete_item,keys);
                    etKeyInputDialog.setAdapter(autocomplete);

                    alertDialogBuilderUserInput
                            .setCancelable(false)
                            .setPositiveButton("Add",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface d, int w){}
                            })
                            .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                }
                            );

                    final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                    alertDialogAndroid.show();
                    alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String key = etKeyInputDialog.getText().toString().toLowerCase();
                            String value = etValueInputDialog.getText().toString().toLowerCase();

                            Log.d("profile","key: "+key);

                            if (!isTextValid(key) || !isTextValid(value)){
                                TextView info = (TextView) mView.findViewById(R.id.infoInputDialog);
                                info.setText("Text fields can't be empty");
                                return;
                            }
                            final PairModel keypair = new PairModel(key,value);


                            HashMap<String,String> params = new HashMap<>();
                            params.put("key", keypair.getKey());
                            params.put("value", keypair.getValue());
                            new Request("PUT","/profile",params){
                                @Override
                                public void onResponse(JSONObject json) throws JSONException{
                                    alertDialogAndroid.dismiss();
                                    if(json.getString("status").equals("ok")) {
                                        adapter.insertItem(keypair);
                                        if(((ListView)container.findViewById(R.id.list))!=null)
                                            ((ListView)container.findViewById(R.id.list)).smoothScrollToPosition(adapter.getCount());
                                    }
                                    else{
                                        ((MainActivity) getActivity()).dialogAlert("Error saving KeyPair!");
                                    }

                                }
                                @Override
                                public void onError(String msg){
                                    ((MainActivity) getActivity()).dialogAlert("Error saving KeyPair!");
                                }
                            }.execute();
                        }
                    });

                }
            });
        }
        return view;
    }

    public String[] getServerKeysAutoComplete(){

        new Request("GET","/keys"){
            @Override
            public void onResponse(JSONObject json) throws JSONException {
                JSONObject keys = json.getJSONObject("keys");
                for(int i=0; i<keys.names().length(); i++){
                    String key = keys.names().getString(i);
                    JSONArray values = keys.getJSONArray(keys.names().getString(i));
                    String[] array = new String[values.length()];
                    for(int j=0; j<values.length(); j++){
                        array[j] = values.getString(j);
                    }
                    autocomplete.put(key,array);
                }
            }
            @Override
            public void onError(String error) {}
        }.execute();
        return autocomplete.keySet().toArray(new String[0]);
    }

    public String[] getServerValuesAutoComplete(String key){
        if(autocomplete.get(key)!=null)
            return autocomplete.get(key);
        else return new String[]{};
    }

    public boolean isTextValid(String text){
        String pattern= "^[a-zA-Z0-9 ]+$";
        return text.matches(pattern);

    }


    public void postKeyPairToServer(PairModel keypair){
        // TODO: Server Requests
    }
    @Override
    public void deleteClicked() {
        Log.d("profile","delete");
        for (int i = 0; i < adapter.getCount(); i++) {
            PairModel keypair = adapter.getItem(i);
            Log.d("profile", "i="+i);
            if(keypair.isSelected()) {
                adapter.list.remove(i);
                adapter.notifyDataSetChanged();
                new Request("DELETE","/profile/"+keypair.getKey()){
                    @Override
                    public void onResponse(JSONObject json) throws JSONException{
                    }
                    @Override
                    public void onError(String msg){
                    }
                }.execute();
                i--;
            }
        }
        selected=0;
        ((MainActivity)getActivity()).getMenu().getItem(0).setVisible(selected > 0);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        ListView list = (ListView) view.findViewById(R.id.list);
        state.putParcelable("listview", list.onSaveInstanceState());
        getFragmentManager().putFragment(state,"profile",this);
        Log.d("state","onSaveInstanceState");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle state) {
        super.onActivityCreated(state);
    }

    private boolean restoreState(Bundle state){
        if(state==null) return false;
        Log.d("profile","restoreState: "+state);
        getActivity().getSupportFragmentManager().getFragment(state, "profile");
        ListView list = (ListView) view.findViewById(R.id.list);
        list.onRestoreInstanceState(state.getParcelable("listview"));
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(adapter!=null) adapter.deselectAllItems();
        if(((MainActivity)getActivity()).getMenu()!=null)
            ((MainActivity)getActivity()).getMenu().getItem(0).setVisible(false);
    }

    private void adapter(){
        list.setOnItemLongClickListener(this);
        adapter = new PairAdapter(view.getContext(), new ArrayList<PairModel>());
        list.setAdapter(adapter);

        new Request("GET","/profile"){
            @Override
            public void onResponse(JSONObject json) throws JSONException{
                ArrayList<PairModel> pairs = new ArrayList<>();
                JSONObject keys = json.getJSONObject("keys");
                if(keys.length()>0) {
                    for (int i = 0; i < keys.names().length(); i++) {
                        String key = keys.names().getString(i);
                        String value = keys.getString(keys.names().getString(i));
                        pairs.add(new PairModel(key, value));
                    }
                    adapter = new PairAdapter(view.getContext(), pairs);
                    list.setAdapter(adapter);
                }
            }
            @Override
            public void onError(String msg){

            }
        }.execute();
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View item, int position, long id) {
        Log.d("profile","onItemLongClick");
        PairModel pair = adapter.getItem(position);
        if(!pair.isSelected()) {
            item.setSelected(pair.toogle());
            selected++;
        }else{
            item.setSelected(pair.toogle());
            selected--;
        }
        ((MainActivity)getActivity()).getMenu().getItem(0).setVisible(selected > 0);
        return true;
    }


}
