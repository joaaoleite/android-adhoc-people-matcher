package pt.ulisboa.tecnico.cmu.locmess.main.locations;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.MainActivity;
import pt.ulisboa.tecnico.cmu.locmess.session.Session;
import pt.ulisboa.tecnico.cmu.locmess.session.requests.Request;


public class MapSubFragment extends Fragment implements LocationListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static MapSubFragment singleton;
    private GoogleMap map;
    private ProgressDialog dialog;
    private ViewGroup container;
    private View view;

    public MapSubFragment() {
        // Required empty public constructor
    }

    public static void deleteInstance(){
        singleton = null;
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

        map.moveCamera(CameraUpdateFactory.zoomTo(15));

        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Location","Location access true");
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);

            LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            String bestProvider = String.valueOf(manager.getBestProvider(new Criteria(), true));
            manager.requestLocationUpdates(bestProvider, 10000, 1, this);
            Location myLocation = manager.getLastKnownLocation(bestProvider);

            if(myLocation==null) {
                Log.d("Location","myLocation=null");
                myLocation = new Location(LocationManager.GPS_PROVIDER);
                myLocation.setLatitude(38.7);
                myLocation.setLongitude(-9.3);
            }
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 14));


        } else {
            Log.d("Location","Location access false");
            ActivityCompat.requestPermissions(getActivity(), new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
            }, 0);
            Location myLocation = new Location(LocationManager.GPS_PROVIDER);
            myLocation.setLatitude(38.7);
            myLocation.setLongitude(-9.3);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 14));

        }

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

                Double lat = 0.0;
                Double lon = 0.0;
                String name = "...";
                int radius = 0;

                try {
                    lat = Double.parseDouble(latInput.getText().toString().toLowerCase());
                    lon = Double.parseDouble(longInput.getText().toString().toLowerCase());
                    name = etInputName.getText().toString().toLowerCase();
                    radius = Integer.parseInt(radiusInput.getText().toString().toLowerCase());

                    if (!isTextValid(name)) {
                        TextView info = (TextView) mView.findViewById(R.id.infoInputDialog);
                        info.setText(getContext().getString(R.string.error_empty_field));
                        return;
                    }
                }
                catch (Exception e){
                    TextView info = (TextView) mView.findViewById(R.id.infoInputDialog);
                    info.setText(getContext().getString(R.string.error_invalid_field));
                    return;
                }

                final LocationModel location = new LocationModel(name, lat, lon, radius);

                HashMap<String,String> params = new HashMap<>();
                params.put("name",location.getName());
                params.put("latitude",location.getLatitude()+"");
                params.put("longitude",location.getLongitude()+"");
                params.put("radius",location.getRadius()+"");
                new Request("POST","/locations",params) {
                    @Override
                    public void onResponse(JSONObject json) throws JSONException {
                        if (json.getString("status").equals("ok")) {

                            ListSubFragment.adapter.insertItem(location);

                            int size = ListSubFragment.adapter.getCount();
                            ListView l = (ListView)container.findViewById(R.id.locationslist);
                            if(l!=null) l.smoothScrollToPosition(size);

                            map.addMarker(new MarkerOptions()
                                    .position(point)
                                    .title(location.getName()));
                            alertDialogAndroid.dismiss();
                            Session.getInstance().updateLocations();
                        }
                        else ((MainActivity)getActivity()).dialogAlert(getContext().getString(R.string.error_saving_location));
                        alertDialogAndroid.dismiss();
                    }

                    @Override
                    public void onError(String error) {
                        ((MainActivity)getActivity()).dialogAlert(getContext().getString(R.string.error_saving_location));
                        alertDialogAndroid.dismiss();
                    }
                }.execute();
            }
        });

    }

    public boolean isTextValid(String text){
        String pattern= "^[a-zA-Z0-9 ]+$";
        return text.matches(pattern);
    }

    @Override
    public void onLocationChanged(Location location) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}
}
