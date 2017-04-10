package pt.ulisboa.tecnico.cmu.locmess.main.messages;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.MyFragment;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.PairModel;


public class MessagesFragment extends MyFragment {


    private OnFragmentInteractionListener mListener;

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
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        populate();

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
        messages.add(new MessageModel("home","Xiago","Whitelist", pairs,calstart,calend,"ola tas bom"));

        return messages;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

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
