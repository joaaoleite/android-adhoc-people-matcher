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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmu.locmess.R;

public class ProfileFragment extends Fragment implements AdapterView.OnItemLongClickListener  {

    private static ProfileFragment singleton;
    private int selected;
    private View view;
    private ListView list;
    private PairAdapter adapter;
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle state) {
        if(view==null) {
            this.view = inflater.inflate(R.layout.fragment_profile, container, false);
            this.list = (ListView) view.findViewById(R.id.list);
            final Fragment f = this;
            restoreState(state);
            populate();
            adapter();

            FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fab);
            myFab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(f.getContext());
                    View mView = layoutInflaterAndroid.inflate(R.layout.user_input_profile, container, false);
                    AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(f.getContext());
                    alertDialogBuilderUserInput.setView(mView);

                    final EditText etKeyInputDialog = (EditText) mView.findViewById(R.id.keyInputDialog);
                    final EditText etValueInputDialog = (EditText) mView.findViewById(R.id.valueInputDialog);

                    alertDialogBuilderUserInput
                            .setCancelable(false)
                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {

                                    adapter.insertItem(new PairModel(etKeyInputDialog.getText().toString(),etValueInputDialog.getText().toString()));
                                    ((ListView)container.findViewById(R.id.list)).smoothScrollToPosition(adapter.getCount());
                                }
                            })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogBox, int id) {
                                            dialogBox.cancel();
                                        }
                                    });

                    AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                    alertDialogAndroid.show();
                }
            });
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

        for (int i = 0; i < adapter.getCount(); i++) {
            Log.d("profile", "i="+i);
            if(adapter.getItem(i).isSelected()) {
                adapter.list.remove(i);
                adapter.notifyDataSetChanged();
                i--;
            }
        }
        selected=0;
        deleteButton.setVisible(selected > 0);

        return true;
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

    private List<PairModel> populate(){
        ArrayList<PairModel> pairs = new ArrayList<PairModel>();
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
        return pairs;
    }
    private void adapter(){
        list.setOnItemLongClickListener(this);
        adapter = new PairAdapter(view.getContext(), this.populate());
        list.setAdapter(adapter);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View item, int position, long id) {
        Log.d("profile","onItemLongClick");
        PairModel pair = adapter.getItem(position);
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
