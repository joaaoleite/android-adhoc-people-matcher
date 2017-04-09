package pt.ulisboa.tecnico.cmu.locmess.main.locations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.MainActivity;
import pt.ulisboa.tecnico.cmu.locmess.main.MyFragment;


public class LocationsFragment extends MyFragment {

    private static LocationsFragment singleton;
    private Fragment fragment;
    private boolean map = false;

    public LocationsFragment() {}

    public static LocationsFragment newInstance() {
        Log.d("locations","newInstance");
        if(singleton == null) singleton = new LocationsFragment();
        return singleton;
    }
    @Override
    public void deleteClicked(){
        if(fragment instanceof ListSubFragment)
            ((ListSubFragment)fragment).deleteClicked();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locations, container, false);


        fragment = ListSubFragment.newInstance();

        ((MainActivity)getActivity()).getMenu().getItem(1).setVisible(true);

        getChildFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
        return view;
    }

    public void MapClicked(MenuItem item) {

        if (map) {
            fragment = ListSubFragment.newInstance();
            item.setIcon(R.drawable.ic_satellite_black_24dp);

        } else {
            fragment = MapSubFragment.newInstance();
            item.setIcon(R.drawable.ic_list_black_24dp);
        }
        map = !map;

        getChildFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity)getActivity()).getMenu().getItem(1).setVisible(false);
    }
}
