package pt.ulisboa.tecnico.cmu.locmess.main.messages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.MyFragment;
import pt.ulisboa.tecnico.cmu.locmess.session.LocMessService;
import pt.ulisboa.tecnico.cmu.locmess.session.requests.Request;
import pt.ulisboa.tecnico.cmu.locmess.session.Session;


public class MessagesFragment extends MyFragment implements AdapterView.OnItemClickListener{


    private ListView list;
    private View view;
    public MessageAdapter adapter;
    private static MessagesFragment singleton;

    public MessagesFragment() {
        // Required empty public constructor
    }

    public static void deleteInstance(){
        singleton = null;
    }
    public static MessagesFragment newInstance() {
        if(singleton==null) singleton = new MessagesFragment();
        return singleton;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(this.view==null) {
            // Inflate the layout for this fragment
            this.view = inflater.inflate(R.layout.fragment_messages, container, false);
            this.list = (ListView) view.findViewById(R.id.list);

            adapter();

            final Spinner spinnerMsgType = (Spinner) view.findViewById(R.id.spinnerMessages);
            final String[] msgTypes = new String[]{"all", "received", "sent"};
            ArrayAdapter<CharSequence> msgTypes_adapter = new ArrayAdapter<CharSequence>
                    (view.getContext(), android.R.layout.simple_spinner_dropdown_item, msgTypes);
            spinnerMsgType.setAdapter(msgTypes_adapter);


            spinnerMsgType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    adapter.setMsgType(spinnerMsgType.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), MessageCreator.class);
                startActivityForResult(intent,2);
            }
        });

        return view;
    }

    public void adapter(){
        adapter = new MessageAdapter(view.getContext(), new ArrayList<MessageModel>());
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);

        new Request("GET","/messages"){
            @Override
            public void onResponse(JSONObject json) throws JSONException {
                if(json.getString("status").equals("ok")){
                    try {
                        JSONArray messages = json.getJSONArray("messages");

                        ArrayList<MessageModel> messagesList = new ArrayList<>();
                        for (int i = 0; i < messages.length(); i++) {
                            JSONObject msg = messages.getJSONObject(i);
                            MessageModel m = new MessageModel(msg);
                            if(m!=null) messagesList.add(m);
                        }
                        adapter = new MessageAdapter(view.getContext(), messagesList);
                        list.setAdapter(adapter);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Log.d("Messages","Error fetching messages!");
                        Log.d("Messages","Error: ");
                    }
                }
            }
            @Override
            public void onError(String error) {
                Log.d("Messages","Error fetching messages!");
            }
        }.execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("profile", "delete1");
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null) {
            Boolean delete = data.getExtras().getBoolean("delete");
            Boolean creator = data.getExtras().getBoolean("creator");
            if (delete && requestCode == 1) {
                Log.d("Messages", "delete");
                String id = data.getExtras().getString("id");
                MessageModel message = LocMessService.getInstance().MESSAGES().find(id);
                for(int i=0; i<adapter.list.size(); i++)
                    if(adapter.list.get(i).getId().equals(id))
                        adapter.list.remove(i);

                adapter.notifyDataSetChanged();
                deleteMessageOnServer(message);
            }
            if (creator && requestCode == 2) {
                Log.d("Messages", "create");
                adapter();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MessageModel message = adapter.getItem(position);

        Intent intent = new Intent(view.getContext(), MessageViewer.class);
        intent.putExtra("id", message.getId());
        intent.putExtra("position",position);

        startActivityForResult(intent, 1);
    }

    @Override
    public void deleteClicked() { }

    public void deleteMessageOnServer(MessageModel message){
        if(message.getMode() == MessageModel.MESSAGE_MODE.DECENTRALIZED){
            LocMessService.getInstance().MESSAGES().remove(message.getId());
        } else {
            new Request("DELETE", "/messages/" + message.getId()) {
                @Override
                public void onResponse(JSONObject json) throws JSONException {

                }

                @Override
                public void onError(String error) {

                }
            }.execute();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
