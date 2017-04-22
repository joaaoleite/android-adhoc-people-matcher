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

        Intent myIntent = getIntent();
        user.setText("From: "+ myIntent.getStringExtra("Title"));
        subject.setText("Subject: "+ myIntent.getStringExtra("Subject"));
        content.setText(myIntent.getStringExtra("Content"));
        if(myIntent.getStringExtra("type").equals("Sent")){
            Log.d("xona","xona");
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
