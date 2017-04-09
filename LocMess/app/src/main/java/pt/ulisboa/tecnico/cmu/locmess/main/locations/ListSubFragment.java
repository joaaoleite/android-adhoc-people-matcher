package pt.ulisboa.tecnico.cmu.locmess.main.locations;

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

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.MainActivity;
import pt.ulisboa.tecnico.cmu.locmess.main.MyFragment;


public class ListSubFragment extends Fragment implements AdapterView.OnItemLongClickListener  {

    private static ListSubFragment singleton;
    private int selected;
    private View view;
    private ListView list;
    private PairAdapter adapter;


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

                            if (!isTextValid(key) || !isTextValid(value)){
                                TextView info = (TextView) mView.findViewById(R.id.infoInputDialog);
                                info.setText("Text fields can't be empty");
                                return;
                            }
                            PairModel keypair = new PairModel(key,value);
                            adapter.insertItem(keypair);

                            int size = adapter.getCount();
                            ListView l = (ListView)container.findViewById(R.id.locationslist);
                            if(l!=null) l.smoothScrollToPosition(size);
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

    public void deleteClicked() {
        for (int i = 0; i < adapter.getCount(); i++) {
            PairModel keypair = adapter.getItem(i);
            if(keypair.isSelected()) {
                adapter.list.remove(i);
                adapter.notifyDataSetChanged();
                i--;
                deleteKeyPairOnServer(keypair);
            }
        }
        selected=0;
        ((MainActivity)getActivity()).getMenu().getItem(0).setVisible(selected > 0);
    }

    public void deleteKeyPairOnServer(PairModel keypair){
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

    private List<PairModel> populate(){
        ArrayList<PairModel> pairs = new ArrayList<PairModel>();
        pairs.add(new PairModel("restaurante","mac"));
        pairs.add(new PairModel("clube","Porcó"));
        pairs.add(new PairModel("cor","azul"));
        return pairs;
    }
    private void adapter(){
        list.setOnItemLongClickListener(this);
        adapter = new PairAdapter(view.getContext(), this.populate());
        list.setAdapter(adapter);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View item, int position, long id) {

        PairModel pair = adapter.getItem(position);
        pair.view = item;
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
