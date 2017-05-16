package pt.ulisboa.tecnico.cmu.locmess;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import pt.ulisboa.tecnico.cmu.locmess.session.requests.Request;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private EditText etPasswordConfirmation;
    ProgressDialog dialog;
    private Boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPasswordConfirmation = (EditText) findViewById(R.id.etPasswordConfirmation);
    }


    private boolean isUsernameValid(String username) {
        String pattern= "^[a-zA-Z0-9]+$";
        return username.matches(pattern);
    }

    private boolean isPasswordTheSame(String password, String confirmation){
        return password.equals(confirmation);
    }

    public void register(View view){

        if (!isUsernameValid(etUsername.getText().toString())){
            dialogAlert(getApplicationContext().getString(R.string.invalid_username));
            return;
        }

        if (etPassword.getText().toString().length()<3){
            dialogAlert(getApplicationContext().getString(R.string.password_size));
            return;
        }

        if (!isPasswordTheSame(etPassword.getText().toString(), etPasswordConfirmation.getText().toString())){
            dialogAlert(getApplicationContext().getString(R.string.passwords_dont_match));
            return;
        }




        loadingDialog(getApplicationContext().getString(R.string.wait));
        registerOnServer(etUsername.getText().toString(),etPassword.getText().toString());


    }

    private void registerOnServer(String username, String password){

        HashMap<String,String> params = new HashMap<>();
        params.put("username",username);
        params.put("password",password);
        new Request("POST","/signup",params){
            @Override
            public void onResponse(JSONObject obj) throws JSONException{
                if(obj.getString("status").equals("ok")){
                    loadingDialog(false);
                    success = true;
                    dialogAlert(getApplicationContext().getString(R.string.register_successful));
                }
                else{
                    loadingDialog(false);
                    dialogAlert(getApplicationContext().getString(R.string.error_registering));
                }
            }
            @Override
            public void onError(String msg){
                loadingDialog(false);
                dialogAlert(getApplicationContext().getString(R.string.error_registering));
            }
        }.execute();
    }

    private void dialogAlert(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Register");
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getApplicationContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if(success) finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void loadingDialog(String message){
        dialog = ProgressDialog.show(this, "", message, true);
    }
    private void loadingDialog(Boolean state){
        if(!state) dialog.dismiss();
    }





}
