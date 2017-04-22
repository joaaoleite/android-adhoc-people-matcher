package pt.ulisboa.tecnico.cmu.locmess.main.messages;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.MainActivity;

public class MessageViewer extends AppCompatActivity {

    private Menu menu;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_viewer);

        TextView user = (TextView) findViewById(R.id.messageUser);
        TextView subject = (TextView) findViewById(R.id.messageSubject);
        TextView content = (TextView) findViewById(R.id.messageContent);
        TextView location = (TextView) findViewById(R.id.messageLocation);
        TextView policy = (TextView) findViewById(R.id.messagePolicy);
        TextView filter = (TextView) findViewById(R.id.messageFilter);
        TextView date = (TextView) findViewById(R.id.messageCalendar);


        Intent myIntent = getIntent();
        user.setText("From: "+ myIntent.getStringExtra("user"));
        subject.setText("Subject: "+ myIntent.getStringExtra("subject"));
        content.setText("Message: " + myIntent.getStringExtra("content"));
        if(myIntent.getStringExtra("type").equals("Sent")){
            location.setText("Location: " + myIntent.getStringExtra("location"));
            policy.setText("Policy: " + myIntent.getStringExtra("policy"));
            filter.setText("Filters: " + myIntent.getStringExtra("filter"));
            date.setText("Date: " + myIntent.getStringExtra("date"));
        }
        this.position = myIntent.getIntExtra("position",-1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        super.onCreateOptionsMenu(menu);
        menu.getItem(0).setVisible(true);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                getIntent().putExtra("delete", true);
                getIntent().putExtra("position", position);
                setResult(RESULT_OK, getIntent());
                finish();
                break;
            case android.R.id.home:
                getIntent().putExtra("delete", false);
                setResult(RESULT_OK, getIntent());
                finish();
                break;
        }
        return true;
    }


}
