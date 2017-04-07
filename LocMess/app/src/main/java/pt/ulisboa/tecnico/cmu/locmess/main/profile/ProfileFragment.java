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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


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
                            PairModel keypair = new PairModel(key,value);
                            adapter.insertItem(keypair);
                            ((ListView)container.findViewById(R.id.list)).smoothScrollToPosition(adapter.getCount());
                            postKeyPairToServer(keypair);

                            alertDialogAndroid.dismiss();
                        }
                    });

                }
            });
        }
        return view;
    }

    public String[] getServerKeysAutoComplete(){
        return new String[]{"clube","cor","restaurante"};
    }

    public String[] getServerValuesAutoComplete(String key){
        return new String[]{"cvermelho","cazul","camarelo", "cona", "calhalho", "caralaocive", "caefac", "cafevrg", "caioevni","camelo","cacieoinap","cabuba"};
    }

    public boolean isTextValid(String text){
        String pattern= "^[a-zA-Z0-9 ]+$";
        return text.matches(pattern);

    }


    public void postKeyPairToServer(PairModel keypair){
        // TODO: Server Requests
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
            PairModel keypair = adapter.getItem(i);
            Log.d("profile", "i="+i);
            if(keypair.isSelected()) {
                adapter.list.remove(i);
                adapter.notifyDataSetChanged();
                i--;
                deleteKeyPairOnServer(keypair);
            }
        }
        selected=0;
        deleteButton.setVisible(selected > 0);

        return true;
    }

    public void deleteKeyPairOnServer(PairModel keypair){
        // TODO: Server Requests
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
