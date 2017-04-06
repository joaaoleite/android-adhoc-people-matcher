package pt.ulisboa.tecnico.cmu.locmess;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import pt.ulisboa.tecnico.cmu.locmess.main.MainActivity;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.ProfileFragment;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    ProgressDialog dialog;
    private Boolean success = false;
    private static LoginActivity singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        singleton=this;

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

    }

    public void login(View view){

        //  TODO: login logic verification


        if (!isUsernameValid(etUsername.getText().toString())){
            dialogAlert("Invalid Username");
            return;
        }

        loadingDialog("Please wait...");
        loginOnServer();
    }

    public void loginOnServer(){
        // TODO: API Requests
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        success = true;
                        loadingDialog(false);
                        if(success) {
                            Intent intent = new Intent(singleton, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else dialogAlert("Error logging in!");
                    }
                },
                1000);
    }

    public void register(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    private boolean isUsernameValid(String username) {
        String pattern= "^[a-zA-Z0-9]+$";
        return username.matches(pattern);
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

    private void loadingDialog(String message){
        dialog = ProgressDialog.show(this, "", message, true);
    }
    private void loadingDialog(Boolean state){
        if(!state) dialog.dismiss();
    }
}

