package pt.ulisboa.tecnico.cmu.locmess.main.locations;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import pt.ulisboa.tecnico.cmu.locmess.R;


public class MapSubFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static MapSubFragment singleton;
    private GoogleMap map;
    private ProgressDialog dialog;
    private ViewGroup container;
    private View view;

    public MapSubFragment() {
        // Required empty public constructor
    }

    public static MapSubFragment newInstance() {
        Log.d("map","newInstance");
        if(singleton == null) singleton = new MapSubFragment();
        return singleton;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view==null)
            this.view = inflater.inflate(R.layout.fragment_map_sub, container, false);

        this.container = container;

        if(map==null) {
            MapView mapView = (MapView) view.findViewById(R.id.map);
            mapView.onCreate(savedInstanceState);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onMapLongClick (LatLng point) {
        addLocationDialog(point);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setOnMapLongClickListener(this);

        // my location
        LatLng location = new LatLng(38.740561,-9.304168);
        map.addMarker(new MarkerOptions().position(location).title("Your location"));
        map.moveCamera(CameraUpdateFactory.newLatLng(location));

        for(int i=0; i<ListSubFragment.adapter.getCount(); i++){
            LocationModel l = ListSubFragment.adapter.getItem(i);
            if(l.getType().equals("GPS")){
                LatLng point = new LatLng(l.getLatitude(),l.getLongitude());
                map.addMarker(new MarkerOptions().position(point).title(l.getName()));
            }
        }
    }

    private void loadingDialog(String message){
        dialog = ProgressDialog.show(getActivity(), "", message, true);
    }
    private void loadingDialog(Boolean state){
        if(!state) dialog.dismiss();
    }

    private void addLocationDialog(final LatLng point){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this.getContext());
        final View mView = layoutInflaterAndroid.inflate(R.layout.user_input_gps, container, false);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this.getContext());
        alertDialogBuilderUserInput.setView(mView);

        final EditText latInput = (EditText) mView.findViewById(R.id.lat);
        final EditText longInput = (EditText) mView.findViewById(R.id.lon);
        latInput.setText(Double.toString(point.latitude));
        longInput.setText(Double.toString(point.longitude));

        final EditText radiusInput = (EditText) mView.findViewById(R.id.radius);
        final EditText etInputName = (EditText) mView.findViewById(R.id.inputName);
        radiusInput.requestFocus();

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

                Double lat = Double.parseDouble(latInput.getText().toString().toLowerCase());
                Double lon = Double.parseDouble(longInput.getText().toString().toLowerCase());
                String name = etInputName.getText().toString().toLowerCase();
                int radius = Integer.parseInt(radiusInput.getText().toString().toLowerCase());

                if (!isTextValid(name)){
                    TextView info = (TextView) mView.findViewById(R.id.infoInputDialog);
                    info.setText("Text fields can't be empty");
                    return;
                }
                LocationModel location = new LocationModel(name, lat, lon, radius);
                ListSubFragment.adapter.insertItem(location);

                int size = ListSubFragment.adapter.getCount();
                ListView l = (ListView)container.findViewById(R.id.locationslist);
                if(l!=null) l.smoothScrollToPosition(size);
                postLocationToServer(location);
                map.addMarker(new MarkerOptions()
                        .position(point)
                        .title(name));
                alertDialogAndroid.dismiss();
            }
        });

    }

    public void postLocationToServer(LocationModel location){
        // TODO: Server Requests
    }
    public boolean isTextValid(String text){
        String pattern= "^[a-zA-Z0-9 ]+$";
        return text.matches(pattern);
    }
}
