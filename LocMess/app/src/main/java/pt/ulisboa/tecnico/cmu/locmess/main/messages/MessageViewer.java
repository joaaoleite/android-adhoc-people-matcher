package pt.ulisboa.tecnico.cmu.locmess.main.messages;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.MainActivity;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.PairModel;
import pt.ulisboa.tecnico.cmu.locmess.session.LocMessService;

public class MessageViewer extends AppCompatActivity {

    private Menu menu;
    private int position;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_viewer);

        TextView user = (TextView) findViewById(R.id.messageUser);
        TextView mode = (TextView) findViewById(R.id.mode);
        TextView subject = (TextView) findViewById(R.id.messageSubject);
        TextView content = (TextView) findViewById(R.id.messageContent);
        TextView location = (TextView) findViewById(R.id.messageLocation);

        TextView policy = (TextView) findViewById(R.id.messagePolicy);
        TextView filter = (TextView) findViewById(R.id.messageFilter);
        TextView start = (TextView) findViewById(R.id.messageStart);
        TextView end = (TextView) findViewById(R.id.messageEnd);

        Intent myIntent = getIntent();
        id = myIntent.getStringExtra("id");

        MessageModel msg = LocMessService.getInstance().MESSAGES().find(id);

        user.setText(msg.getUser());
        subject.setText(msg.getContent());
        content.setText(msg.getContent());
        mode.setText(msg.getMode().toString().toLowerCase());
        location.setText(msg.getLocation());
        policy.setText(msg.getPolicy().toString());
        filter.setText(toString());
        String f = "";
        for(PairModel pair : msg.getFilter())
            f += pair.getKey() + "=" + pair.getValue();

        filter.setText(f);
        start.setText(msg.getStart().getTime().toString());
        end.setText(msg.getEnd().getTime().toString());

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
                getIntent().putExtra("id",id);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            getIntent().putExtra("delete", false);
            setResult(RESULT_OK, getIntent());
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
