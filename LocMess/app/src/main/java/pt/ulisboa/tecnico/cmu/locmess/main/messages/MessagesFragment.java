package pt.ulisboa.tecnico.cmu.locmess.main.messages;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.MyFragment;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.PairModel;


public class MessagesFragment extends MyFragment implements AdapterView.OnItemClickListener{


    private OnFragmentInteractionListener mListener;
    private ListView list;
    private View view;
    private MessageAdapter adapter;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void deleteClicked(){

    }

    public static MessagesFragment newInstance() {
        MessagesFragment fragment = new MessagesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_messages, container, false);
        this.list = (ListView) view.findViewById(R.id.list);
        populate();
        adapter();

        final Spinner spinnerMsgType = (Spinner) view.findViewById(R.id.spinnerMessages);
        final String[] msgTypes = new String[]{"All","Received","Sent"};
        ArrayAdapter<CharSequence> msgTypes_adapter = new ArrayAdapter<CharSequence>
                (view.getContext(), android.R.layout.simple_spinner_dropdown_item, msgTypes);
        spinnerMsgType.setAdapter(msgTypes_adapter);


        spinnerMsgType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setMsgType(spinnerMsgType.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });



        return view;
    }

    public List<MessageModel> populate(){
        ArrayList<MessageModel> messages = new ArrayList<>();
        ArrayList<PairModel> pairs = new ArrayList<>();
        pairs.add(new PairModel("cor","vermelho"));
        Calendar calstart = Calendar.getInstance();
        calstart.set(2017,3,29,20,10);
        Calendar calend = Calendar.getInstance();
        calend.set(2017,3,29,20,50);
        messages.add(new MessageModel("home","Xiago", "arrendar quarto", "Whitelist", pairs, calstart, calend, "ola tas bom", "Sent"));
        messages.add(new MessageModel("school","xeite", "estudar no cafe", "blacklist", pairs, calstart, calend, "bora estudar", "Received"));
        return messages;
    }

    public void adapter(){
        adapter = new MessageAdapter(view.getContext(), this.populate());
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MessageModel message = adapter.getItem(position);

        Intent intent = new Intent(view.getContext(), MessageViewer.class);
        startActivity(intent);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
