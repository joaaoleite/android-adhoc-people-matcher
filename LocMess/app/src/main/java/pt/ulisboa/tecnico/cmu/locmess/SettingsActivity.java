package pt.ulisboa.tecnico.cmu.locmess;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.security.spec.ECField;

import pt.ulisboa.tecnico.cmu.locmess.session.Session;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final EditText number=(EditText)findViewById(R.id.number_third_party);
        number.setText(Session.getInstance().get("max"));
        Editable etext = number.getText();
        Selection.setSelection(etext,number.getText().toString().length());


        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = -1;
                try { num = Integer.parseInt(number.getText().toString()); }
                catch (Exception e){ }
                if(num!=-1){
                    Session.getInstance().save("max",num+"");
                    finish();
                }
                else{
                    dialogAlert("Invalid input number!");
                }
            }
        });
    }

    private void dialogAlert(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getApplicationContext().getString(R.string.settings_error));
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getApplicationContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getIntent().putExtra("delete", false);
                setResult(RESULT_OK, getIntent());
                finish();
                break;
        }
        return true;
    }

}
