package pt.ulisboa.tecnico.cmu.locmess.main.messages;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.locations.LocationModel;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.PairModel;
import pt.ulisboa.tecnico.cmu.locmess.session.LocMessService;
import pt.ulisboa.tecnico.cmu.locmess.session.Session;
import pt.ulisboa.tecnico.cmu.locmess.session.requests.Request;

public class MessageCreator extends AppCompatActivity {

    private HashMap<String,String[]> autocomplete = new HashMap<>();
    private ArrayList<String> autolocations = new ArrayList<>();

    private HashMap<String,String> pairsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_creator);

        getServerKeysAutoComplete();

        final MessageCreator that = this;

        final EditText startDate = (EditText) findViewById(R.id.startDate);
        startDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) return;

                Calendar cal = Calendar.getInstance();
                if(!startDate.getText().equals("")) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        Date date = dateFormat.parse(startDate.getText() + "");
                        cal.setTime(date);
                    }
                    catch (Exception e){}
                }
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog picker = new DatePickerDialog(that, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month++;
                        String m = month+"";
                        String d = dayOfMonth+"";
                        if(month/10==0) m = "0"+m;
                        if(dayOfMonth/10==0) d = "0"+d;
                        startDate.setText(year+"/"+m+"/"+d);
                    }
                }, year, month, day);
                picker.show();
            }
        });


        final EditText startTime = (EditText) findViewById(R.id.startTime);
        startTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) return;

                int hours = 0;
                int minutes = 0;

                String time = startTime.getText().toString();
                if(!time.equals("")){
                    try {
                        hours = Integer.parseInt(time.split(":")[0]);
                        minutes = Integer.parseInt(time.split(":")[1]);
                    }
                    catch (Exception e){}
                }

                TimePickerDialog picker = new TimePickerDialog(that, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String h = hourOfDay+"";
                        String m = minute+"";
                        if(hourOfDay/10==0) h = "0"+h;
                        if(minute/10==0) m = "0"+m;
                        startTime.setText(h+":"+m);
                    }
                }, hours, minutes, true);
                picker.show();
            }
        });

        final EditText endDate = (EditText) findViewById(R.id.endDate);
        endDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) return;

                Calendar cal = Calendar.getInstance();
                if(!endDate.getText().equals("")) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        Date date = dateFormat.parse(endDate.getText() + "");
                        cal.setTime(date);
                    }
                    catch (Exception e){}
                }
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog picker = new DatePickerDialog(that, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month++;
                        String m = month+"";
                        String d = dayOfMonth+"";
                        if(month/10==0) m = "0"+m;
                        if(dayOfMonth/10==0) d = "0"+d;
                        endDate.setText(year+"/"+m+"/"+d);
                    }
                }, year, month, day);
                picker.show();
            }
        });
        final EditText endTime = (EditText) findViewById(R.id.endTime);
        endTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) return;

                int hours = 0;
                int minutes = 0;

                String time = endTime.getText().toString();
                if(!time.equals("")){
                    try {
                        hours = Integer.parseInt(time.split(":")[0]);
                        minutes = Integer.parseInt(time.split(":")[1]);
                    }
                    catch (Exception e){}
                }

                TimePickerDialog picker = new TimePickerDialog(that, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String h = hourOfDay+"";
                        String m = minute+"";
                        if(hourOfDay/10==0) h = "0"+h;
                        if(minute/10==0) m = "0"+m;
                        endTime.setText(h+":"+m);
                    }
                }, hours, minutes, true);
                picker.show();
            }
        });


        final Spinner modeSpinner = (Spinner) findViewById(R.id.delivery_mode);
        String[] modeArray = new String[]{"Centralized","Decentralized"};
        ArrayAdapter<CharSequence> modeAdapter = new ArrayAdapter<CharSequence>
                (getApplicationContext(), R.layout.spinner_wifi_item,modeArray);
        modeSpinner.setAdapter(modeAdapter);


        final Spinner locSpinner = (Spinner) findViewById(R.id.location);

        new Request("GET","/locations"){
            @Override
            public void onResponse(JSONObject json) throws JSONException {
                JSONArray locations = json.getJSONArray("locations");
                String[] array = new String[locations.length()];

                for(int i=0; i<locations.length(); i++)
                    array[i] = locations.getJSONObject(i).getString("name");

                ArrayAdapter<CharSequence> locAdapter = new ArrayAdapter<CharSequence>
                        (getApplicationContext(), R.layout.spinner_wifi_item,array);
                locSpinner.setAdapter(locAdapter);
            }
            @Override
            public void onError(String error) {}
        }.execute();

        // Policy
        final String[] policies = new String[]{"whitelist","blacklist"};
        final Spinner polSpinner = (Spinner) findViewById(R.id.policy);
        ArrayAdapter<CharSequence> polAdapter = new ArrayAdapter<CharSequence>
                (getApplicationContext(), R.layout.spinner_wifi_item,policies);
        polSpinner.setAdapter(polAdapter);

        // Filters
        Button addFilter = (Button) findViewById(R.id.addfilter);
        final TextView filters = (TextView) findViewById(R.id.pairs);

        addFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("AddFilter","onClick");

                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(that);
                final View mView = layoutInflaterAndroid.inflate(R.layout.user_input_profile, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(that);
                alertDialogBuilderUserInput.setView(mView);

                ((TextView)mView.findViewById(R.id.dialogTitle)).setText("Add filter");
                final AutoCompleteTextView etKeyInputDialog = (AutoCompleteTextView) mView.findViewById(R.id.keyInputDialog);
                final AutoCompleteTextView etValueInputDialog = (AutoCompleteTextView) mView.findViewById(R.id.valueInputDialog);

                etValueInputDialog.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus) {
                            String[] values = getServerValuesAutoComplete(etKeyInputDialog.getText().toString().toLowerCase());
                            ArrayAdapter<String> autocomplete = new ArrayAdapter<String>
                                    (that, R.layout.autocomplete_item,values);
                            etValueInputDialog.setAdapter(autocomplete);
                        }
                    }
                });

                String[] keys = getServerKeysAutoComplete();
                ArrayAdapter<String> autocomplete = new ArrayAdapter<String>
                        (that, R.layout.autocomplete_item,keys);
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
                            info.setText(getApplicationContext().getString(R.string.error_invalid_field));
                            return;
                        }
                        pairsMap.put(key,value);

                        String result = "";
                        for (Map.Entry<String, String> entry : pairsMap.entrySet()) {
                            String thekey = entry.getKey();
                            String thevalue = entry.getValue();
                            result = result + thekey + " = " + thevalue + "\n";
                        }
                        filters.setText(result);
                        alertDialogAndroid.dismiss();
                    }
                });

            }
        });

        // Content
        final TextView contentView = (TextView) findViewById(R.id.content);
        contentView.requestFocus();

        //Submit
        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

                    Date start = dateFormat.parse(startDate.getText() + " " + startTime.getText());
                    Date end = dateFormat.parse(endDate.getText() + " " + endTime.getText());

                    String policy = polSpinner.getSelectedItem().toString();
                    String location = locSpinner.getSelectedItem().toString();
                    String mode = modeSpinner.getSelectedItem().toString();
                    String content = contentView.getText().toString();

                    if(mode.equals("Centralized")) {

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("start", start.getTime() + "");
                        params.put("end", end.getTime() + "");
                        params.put("policy", policy);
                        params.put("location", location);
                        params.put("content", content);

                        int m = 1;
                        for (Map.Entry<String, String> entry : pairsMap.entrySet()) {
                            params.put("key" + m, entry.getKey());
                            params.put("value" + m, entry.getValue());
                            m++;
                        }

                        new Request("POST", "/messages", params) {
                            @Override
                            public void onResponse(JSONObject json) throws JSONException {
                                Log.d("MessageCreator","json="+json);
                                if (json.getString("status").equals("ok")) {
                                    getIntent().putExtra("creator", true);
                                    setResult(RESULT_OK, getIntent());
                                    finish();
                                } else dialogAlert("Error posting message to server!");
                            }

                            @Override
                            public void onError(String error) {
                                dialogAlert("Error posting message to server!");
                            }
                        }.execute();
                    }
                    if(mode.equals("Decentralized")){

                        Set<PairModel> pairs = new HashSet<>();
                        for (Map.Entry<String, String> entry : pairsMap.entrySet())
                            pairs.add(new PairModel(entry.getKey(),entry.getValue()));

                        Calendar start_cal = Calendar.getInstance();
                        start_cal.setTime(start);
                        Calendar end_cal = Calendar.getInstance();
                        end_cal.setTime(end);


                        MessageModel.MESSAGE_POLICY pol;
                        if(policy.equals("whitelist"))
                            pol = MessageModel.MESSAGE_POLICY.WHITELIST;
                        else
                            pol = MessageModel.MESSAGE_POLICY.BLACKLIST;

                        LocationModel loc = LocMessService.getInstance().LOCATIONS().find(location);
                        MessageModel message = new MessageModel(loc, pol, pairs, start_cal, end_cal, content);
                        LocMessService.getInstance().MESSAGES().add(message);
                        getIntent().putExtra("creator", true);
                        setResult(RESULT_OK, getIntent());
                        finish();
                    }
                }catch (Exception e){
                    Log.d("MessageCreator","ex",e);
                    dialogAlert("Error parsing input fields!");
                }
            }
        });


    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = super.onCreateView(name, context, attrs);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getIntent().putExtra("creator", false);
                setResult(RESULT_OK, getIntent());
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            getIntent().putExtra("creator", false);
            setResult(RESULT_OK, getIntent());
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public String[] getServerKeysAutoComplete(){

        new Request("GET","/keys"){
            @Override
            public void onResponse(JSONObject json) throws JSONException {
                JSONObject keys = json.getJSONObject("keys");
                if(keys!=null) {
                    if(keys.names()!=null) {
                        for (int i = 0; i < keys.names().length(); i++) {
                            String key = keys.names().getString(i);
                            JSONArray values = keys.getJSONArray(keys.names().getString(i));
                            String[] array = new String[values.length()];
                            for (int j = 0; j < values.length(); j++)
                                array[j] = values.getString(j);
                            autocomplete.put(key, array);
                        }
                    }
                }
            }
            @Override
            public void onError(String error) {
                dialogAlert("Error posting the message!");
            }
        }.execute();
        return autocomplete.keySet().toArray(new String[0]);
    }

    public String[] getServerValuesAutoComplete(String key){
        if(autocomplete.get(key)!=null)
        return autocomplete.get(key);
        else return new String[]{};
    }

    public boolean isTextValid(String text){
        String pattern= "^[a-zA-Z0-9 ]+$";
        return text.matches(pattern);
    }

    private void dialogAlert(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login Error");
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}