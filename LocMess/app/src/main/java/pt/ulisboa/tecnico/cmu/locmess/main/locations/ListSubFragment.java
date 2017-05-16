package pt.ulisboa.tecnico.cmu.locmess.main.locations;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.MainActivity;
import pt.ulisboa.tecnico.cmu.locmess.session.Session;
import pt.ulisboa.tecnico.cmu.locmess.session.requests.Request;


public class ListSubFragment extends Fragment implements AdapterView.OnItemLongClickListener  {

    private static ListSubFragment singleton;
    private int selected;
    private View view;
    private ListView list;
    public static LocationAdapter adapter;
    private ProgressDialog dialog;
    private ViewGroup container;
    private HashMap<String,String> macs = new HashMap<>();
    private BroadcastReceiver receiver;


    public static void deleteInstance(){
        singleton = null;
    }
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

        Log.d("Wifi","getSSIDs");

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Log.d("Wifi","permission granted");

            final WifiManager wifi = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi.isWifiEnabled() == false) {
                Toast.makeText(getActivity().getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
                wifi.setWifiEnabled(true);
            }

            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context c, Intent intent) {
                    List<ScanResult> results = wifi.getScanResults();
                    int size = results.size();
                    Log.d("sizeSSID",""+size);
                    ArrayList<String> ssids = new ArrayList<String>();
                    for (int i = 0; i < size; i++) {
                        if(!ssids.contains(results.get(i).SSID)) {
                            ssids.add(results.get(i).SSID);
                            Log.d("SSID","results.get(i).SSID"+results.get(i).SSID);
                            macs.put(results.get(i).SSID,results.get(i).BSSID);
                        }
                    }
                    showSSIDDialog(ssids.toArray(new String[0]));
                }
            };

            getActivity().registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

            wifi.startScan();
        }
        else{
            loadingDialog(false);
            Log.d("Wifi","permission to be granted");
            ActivityCompat.requestPermissions(getActivity(), new String[] {
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 0);
        }
    }

    public boolean isTextValid(String text){
        String pattern= "^[a-zA-Z0-9 ]+$";
        return text.matches(pattern);
    }

    public void deleteClicked() {
        Log.d("LocsList","deleteClicked");
        for (int i = 0; i < adapter.getCount(); i++) {
            LocationModel location = adapter.getItem(i);
            if(location.isSelected()) {
                Log.d("LocsList","location to delete:"+i);
                adapter.list.remove(i);
                adapter.notifyDataSetChanged();
                new Request("DELETE","/locations/"+location.getName()){
                    @Override
                    public void onResponse(JSONObject json){
                        Log.d("LocsList","onResponse");
                    }
                    @Override
                    public void onError(String msg){
                        Log.d("LocsList","onError");
                    }
                }.execute();
            }
        }
        selected=0;
        ((MainActivity)getActivity()).getMenu().getItem(0).setVisible(selected > 0);
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
        //getActivity().getSupportFragmentManager().getFragment(state, "list_sub");
        //ListView list = (ListView) view.findViewById(R.id.locationslist);
        //list.onRestoreInstanceState(state.getParcelable("listview"));
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

    private void adapter(){
        list.setOnItemLongClickListener(this);
        adapter = new LocationAdapter(view.getContext(), new ArrayList<LocationModel>());
        list.setAdapter(adapter);

        new Request("GET","/locations"){
            @Override
            public void onResponse(JSONObject json) throws JSONException {
                ArrayList<LocationModel> locations = new ArrayList<>();
                JSONArray locs = json.getJSONArray("locations");
                if(locs.length()>0) {
                    for (int i = 0; i < locs.length(); i++) {
                        JSONObject loc = locs.getJSONObject(i);
                        String name = loc.getString("name");

                        if(loc.has("ssid")) {
                            String ssid = loc.getString("ssid");
                            String mac = "...";
                            locations.add(new LocationModel(name, ssid, mac));
                        }
                        else{
                            double lat = loc.getDouble("latitude");
                            double lng = loc.getDouble("longitude");
                            int radius = loc.getInt("radius");
                            locations.add(new LocationModel(name, lat, lng, radius));
                        }
                    }
                    adapter = new LocationAdapter(view.getContext(), locations);
                    list.setAdapter(adapter);
                }
            }
            @Override
            public void onError(String msg){

            }
        }.execute();
    }

    private void loadingDialog(String message){
        dialog = ProgressDialog.show(getActivity(), "", message, true);
    }
    private void loadingDialog(Boolean state){
        if(!state) dialog.dismiss();
    }

    private void showSSIDDialog(String[] ssids){
        getActivity().unregisterReceiver(receiver);
        loadingDialog(false);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this.getContext());
        final View mView = layoutInflaterAndroid.inflate(R.layout.user_input_wifi, container, false);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this.getContext());
        alertDialogBuilderUserInput.setView(mView);

        final Spinner spinnerSelectWifi = (Spinner) mView.findViewById(R.id.spinnerWifi);
        final EditText etInputName = (EditText) mView.findViewById(R.id.inputName);

        ArrayAdapter<CharSequence> ssids_adapter = new ArrayAdapter<CharSequence>
                (view.getContext(), R.layout.spinner_wifi_item,ssids);
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
                String mac = macs.get(ssid);

                if (!isTextValid(name)){
                    TextView info = (TextView) mView.findViewById(R.id.infoInputDialog);
                    info.setText("Text fields can't be empty");
                    return;
                }
                final LocationModel location = new LocationModel(name, ssid, mac);

                HashMap<String,String> params = new HashMap<>();
                params.put("name",location.getName());
                params.put("ssid",location.getSsid());
                params.put("mac",location.getMac());
                new Request("POST","/locations",params) {
                    @Override
                    public void onResponse(JSONObject json) throws JSONException {
                        if (json.getString("status").equals("ok")) {
                            adapter.insertItem(location);
                            int size = adapter.getCount();
                            ListView l = (ListView)container.findViewById(R.id.locationslist);
                            if(l!=null) l.smoothScrollToPosition(size);
                        }
                        else ((MainActivity)getActivity()).dialogAlert("Error saving location!");
                        alertDialogAndroid.dismiss();
                    }

                    @Override
                    public void onError(String error) {
                        ((MainActivity)getActivity()).dialogAlert("Error saving location!");
                        alertDialogAndroid.dismiss();
                    }
                }.execute();
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
