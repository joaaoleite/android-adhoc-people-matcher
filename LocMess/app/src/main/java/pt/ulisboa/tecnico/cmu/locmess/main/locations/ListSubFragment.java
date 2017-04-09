package pt.ulisboa.tecnico.cmu.locmess.main.locations;

import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.MainActivity;


public class ListSubFragment extends Fragment implements AdapterView.OnItemLongClickListener  {

    private static ListSubFragment singleton;
    private int selected;
    private View view;
    private ListView list;
    private LocationAdapter adapter;
    private ProgressDialog dialog;
    private ViewGroup container;


    public ListSubFragment() {
    }

    public static ListSubFragment newInstance() {

        if(singleton == null) singleton = new ListSubFragment();
        return singleton;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle state) {
        if(view==null) {
            this.view = inflater.inflate(R.layout.fragment_list_sub, container, false);
            this.list = (ListView) view.findViewById(R.id.locationslist);
            this.container = container;
            restoreState(state);
            populate();
            adapter();

            FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fab);
            myFab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    loadingDialog("Loading SSIDs...");
                    getSSIDs();
                }
            });
        }
        return view;
    }

    public void getSSIDs(){
        final String[] ssids = new String[]{"tagus","bar","restaurante"};
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        showSSIDDialog(ssids);
                    }
                }
        ,1000);
    }

    public String getMac(String ssid){
        return "3x4mpl3";
    }

    public boolean isTextValid(String text){
        String pattern= "^[a-zA-Z0-9 ]+$";
        return text.matches(pattern);
    }


    public void postKeyPairToServer(LocationModel keypair){
        // TODO: Server Requests
    }

    public void deleteClicked() {
        for (int i = 0; i < adapter.getCount(); i++) {
            LocationModel location = adapter.getItem(i);
            if(location.isSelected()) {
                adapter.list.remove(i);
                adapter.notifyDataSetChanged();
                i--;
                deleteKeyPairOnServer(location);
            }
        }
        selected=0;
        ((MainActivity)getActivity()).getMenu().getItem(0).setVisible(selected > 0);
    }

    public void deleteKeyPairOnServer(LocationModel keypair){
        // TODO: Server Requests
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        ListView list = (ListView) view.findViewById(R.id.locationslist);
        state.putParcelable("listview", list.onSaveInstanceState());
        getFragmentManager().putFragment(state,"list_sub",this);
        Log.d("state","onSaveInstanceState");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle state) {
        super.onActivityCreated(state);
    }

    private boolean restoreState(Bundle state){
        if(state==null) return false;
        Log.d("list_sub","restoreState: "+state);
        getActivity().getSupportFragmentManager().getFragment(state, "list_sub");
        ListView list = (ListView) view.findViewById(R.id.locationslist);
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
    }

    private List<LocationModel> populate(){
        ArrayList<LocationModel> locations = new ArrayList<>();
        locations.add(new LocationModel("xenico", "eduroam","mac4dd1"));
        locations.add(new LocationModel("home", 16.213123, 13.54632, 5));
        return locations;
    }
    private void adapter(){
        list.setOnItemLongClickListener(this);
        adapter = new LocationAdapter(view.getContext(), this.populate());
        list.setAdapter(adapter);
    }

    private void loadingDialog(String message){
        dialog = ProgressDialog.show(getActivity(), "", message, true);
    }
    private void loadingDialog(Boolean state){
        if(!state) dialog.dismiss();
    }

    private void showSSIDDialog(String[] ssids){
        loadingDialog(false);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this.getContext());
        final View mView = layoutInflaterAndroid.inflate(R.layout.user_input_location, container, false);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this.getContext());
        alertDialogBuilderUserInput.setView(mView);

        final Spinner spinnerSelectWifi = (Spinner) mView.findViewById(R.id.spinnerWifi);
        final EditText etInputName = (EditText) mView.findViewById(R.id.inputName);

        ArrayAdapter<CharSequence> ssids_adapter = new ArrayAdapter<CharSequence>
                (view.getContext(), android.R.layout.simple_spinner_item,ssids);
        spinnerSelectWifi.setAdapter(ssids_adapter);

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener(){
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

                String ssid = spinnerSelectWifi.getSelectedItem().toString();
                String name = etInputName.getText().toString().toLowerCase();
                String mac = getMac(ssid);

                if (!isTextValid(name)){
                    TextView info = (TextView) mView.findViewById(R.id.infoInputDialog);
                    info.setText("Text fields can't be empty");
                    return;
                }
                LocationModel location = new LocationModel(name, ssid, mac);
                adapter.insertItem(location);

                int size = adapter.getCount();
                ListView l = (ListView)container.findViewById(R.id.locationslist);
                if(l!=null) l.smoothScrollToPosition(size);
                postKeyPairToServer(location);
                alertDialogAndroid.dismiss();
            }
        });

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View item, int position, long id) {

        LocationModel location = adapter.getItem(position);
        location.view = item;
        if(!location.isSelected()) {
            item.setSelected(location.toogle());
            selected++;
        }else{
            item.setSelected(location.toogle());
            selected--;
        }
        ((MainActivity)getActivity()).getMenu().getItem(0).setVisible(selected > 0);
        return true;
    }


}
