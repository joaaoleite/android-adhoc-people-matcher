package pt.ulisboa.tecnico.cmu.locmess.main.messages;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import pt.ulisboa.tecnico.cmu.locmess.R;

public class MessageCreator extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_creator);

        /*final Spinner spinnerLocation = (Spinner) findViewById(R.id.spinnerMessages);
        final String[] locations = new String[]{"All", "Received", "Sent"};
        ArrayAdapter<CharSequence> locations_adapter = new ArrayAdapter<CharSequence>
                (this, android.R.layout.simple_spinner_dropdown_item, locations);
        spinnerLocation.setAdapter(locations_adapter);


        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setMsgType(spinnerLocation.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/

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

    private void save(){

        getIntent();
        setResult(RESULT_OK, getIntent());
        finish();
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


}