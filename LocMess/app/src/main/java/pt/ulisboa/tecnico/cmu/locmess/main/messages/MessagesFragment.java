package pt.ulisboa.tecnico.cmu.locmess.main.messages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.MyFragment;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.PairModel;
import pt.ulisboa.tecnico.cmu.locmess.session.Request;
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
            final String[] msgTypes = new String[]{"All", "Received", "Sent"};
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

        final ArrayList<MessageModel> messagesList = new ArrayList<>();

        SharedPreferences prefs = getActivity().getSharedPreferences(Session.APP_NAME,Context.MODE_PRIVATE);
        Set<String> received = prefs.getStringSet("messages",null);
        if(received!=null){
            for (Iterator<String> it = received.iterator(); it.hasNext(); ) {
                String msg = it.next();
                MessageModel m = MessageAdapter.parse(msg,"Received");
                if(m!=null) messagesList.add(m);
            }
        }

        new Request("GET","/messages"){
            @Override
            public void onResponse(JSONObject json) throws JSONException {
                if(json.getString("status").equals("ok")){
                    try {
                        JSONArray messages = json.getJSONArray("messages");

                        for (int i = 0; i < messages.length(); i++) {
                            JSONObject msg = messages.getJSONObject(i);
                            MessageModel m = MessageAdapter.parse(msg,"Sent");
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
                int position = data.getExtras().getInt("position");
                MessageModel message = adapter.getItem(position);
                adapter.list.remove(position);
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

        String user = message.getUser();
        String subject = message.getSubject();
        String content = message.getContent();
        String msgType = message.getMsgType();
        String location = message.getLocation();
        String policy = message.getPolicy();
        String filter = "";
        for (int i = 0; i < message.getFilter().size(); i++){
            filter = filter + message.getFilter().get(i).getKey()+ " - " + message.getFilter().get(i).getValue() + "\n";
        }

        String start = message.getStart().getTime()+"";
        String end = message.getEnd().getTime()+"";

        start = start.split(" ")[1]+" "+start.split(" ")[2]+" "+start.split(" ")[5]+" at "+start.split(" ")[3].split(":")[0]+":"+start.split(" ")[3].split(":")[1];
        end = end.split(" ")[1]+" "+end.split(" ")[2]+" "+end.split(" ")[5]+" at "+end.split(" ")[3].split(":")[0]+":"+end.split(" ")[3].split(":")[1];

        Intent intent = new Intent(view.getContext(), MessageViewer.class);
        intent.putExtra("user", user);
        intent.putExtra("subject", subject);
        intent.putExtra("content", content);
        intent.putExtra("type", msgType);
        intent.putExtra("location", location);
        intent.putExtra("policy", policy);
        intent.putExtra("filter", filter);
        intent.putExtra("start", start);
        intent.putExtra("end", end);
        intent.putExtra("position", position);

        startActivityForResult(intent,1);
    }

    @Override
    public void deleteClicked() { }

    public void deleteMessageOnServer(MessageModel message){
        new Request("DELETE","/messages/"+message.getId()){
            @Override
            public void onResponse(JSONObject json) throws JSONException {

            }
            @Override
            public void onError(String error) {

            }
        }.execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
