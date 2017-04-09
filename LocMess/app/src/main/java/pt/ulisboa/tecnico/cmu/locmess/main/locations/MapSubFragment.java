package pt.ulisboa.tecnico.cmu.locmess.main.locations;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import pt.ulisboa.tecnico.cmu.locmess.R;


public class MapSubFragment extends Fragment implements OnMapReadyCallback {

    private static MapSubFragment singleton;
    private GoogleMap map;

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
        View view = inflater.inflate(R.layout.fragment_map_sub, container, false);

        MapView mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;


        LatLng location = new LatLng(38.740561,-9.304168);
        map.addMarker(new MarkerOptions().position(location).title("É o panda!"));
        map.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
}
