package pt.ulisboa.tecnico.cmu.locmess.fragments.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.locmess.MainActivity;
import pt.ulisboa.tecnico.cmu.locmess.R;

public class ProfileFragment extends Fragment implements AdapterView.OnItemLongClickListener {

    private static ProfileFragment singleton;
    private ArrayList<PairModel> pairs;
    private int selected;
    private View view;
    private ListView list;
    private MenuItem deleteButton;

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
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        if(view==null) {
            this.view = inflater.inflate(R.layout.fragment_profile, container, false);
            this.list = (ListView) view.findViewById(R.id.list);
            restoreState(state);
            populate();
            adapter();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_toolbar,menu);
        this.deleteButton = menu.getItem(0);
        this.deleteButton.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
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
    }

    private void populate(){
        this.pairs = new ArrayList<PairModel>();
        pairs.add(new PairModel("restaurante","capitanga"));
        pairs.add(new PairModel("clube","benfica"));
        pairs.add(new PairModel("cor","vermelho"));
        pairs.add(new PairModel("restaurante","capitanga"));
        pairs.add(new PairModel("clube","benfica"));
        pairs.add(new PairModel("cor","vermelho"));
        pairs.add(new PairModel("restaurante","capitanga"));
        pairs.add(new PairModel("clube","benfica"));
        pairs.add(new PairModel("cor","vermelho"));
        pairs.add(new PairModel("restaurante","capitanga"));
        pairs.add(new PairModel("clube","benfica"));
        pairs.add(new PairModel("cor","vermelho"));
        pairs.add(new PairModel("restaurante","capitanga"));
        pairs.add(new PairModel("clube","benfica"));
        pairs.add(new PairModel("cor","vermelho"));
        pairs.add(new PairModel("restaurante","capitanga"));
        pairs.add(new PairModel("clube","benfica"));
        pairs.add(new PairModel("cor","vermelho"));
    }
    private void adapter(){
        list.setOnItemLongClickListener(this);
        PairAdapter adapter = new PairAdapter(view.getContext(), (ArrayList) pairs);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View item, int position, long arg3) {
        PairModel pair = pairs.get(position);
        pair.view = item;
        if(!pair.isSelected()) {
            item.setSelected(pair.toogle());
            selected++;
        }else{
            item.setSelected(pair.toogle());
            selected--;
        }
        deleteButton.setVisible(selected > 0);
        return true;
    }
}
