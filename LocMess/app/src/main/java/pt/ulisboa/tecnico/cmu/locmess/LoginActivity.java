package pt.ulisboa.tecnico.cmu.locmess;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import pt.ulisboa.tecnico.cmu.locmess.main.MainActivity;
import pt.ulisboa.tecnico.cmu.locmess.session.requests.Request;
import pt.ulisboa.tecnico.cmu.locmess.session.Session;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    ProgressDialog dialog;
    private Boolean success = false;
    private static LoginActivity singleton;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        singleton=this;

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

        if(Session.getInstance()==null)
            this.session = Session.newInstance(this);
        else
            this.session = Session.getInstance();

        if(session.isLoggedIn()) {
            Intent intent = new Intent(singleton, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void login(View view){

        //  TODO: login logic verification


        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (!isUsernameValid(username)){
            dialogAlert(getApplicationContext().getString(R.string.invalid_username));
            return;
        }

        if (!passwordEmpty(password)){
            dialogAlert(getApplicationContext().getString(R.string.password_needed));
           return;
        }

        loadingDialog(getApplicationContext().getString(R.string.wait));
        loginOnServer(username, password);
    }

    public void loginOnServer(String username, String password){

        Log.d("Session", "Login...");

        Log.d("Session", "username: "+username);
        Log.d("Session", "password: "+password);

        HashMap<String,String> params = new HashMap<>();
        params.put("username",username);
        params.put("password",password);

        new Request("POST","/login",params){
            public void onResponse(JSONObject res){
                try{
                    String token = res.getString("token");
                    String username = res.getString("username");
                    Session.getInstance().token(token,username);
                    if(token!=null) {
                        loadingDialog(false);
                        Intent intent = new Intent(singleton, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Session.getInstance().logout();
                        dialogAlert(getApplicationContext().getString(R.string.error_logging_in));
                    }

                }catch(JSONException e){
                    Session.getInstance().logout();
                    dialogAlert(getApplicationContext().getString(R.string.error_logging_in));
                }
                loadingDialog(false);
            }
            public void onError(String error){
                Session.getInstance().logout();
                dialogAlert(getApplicationContext().getString(R.string.error_logging_in));
                loadingDialog(false);
            }
        }.execute();

    }

    public void register(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private boolean passwordEmpty(String password){
        return password.length()>0;
    }

    private boolean isUsernameValid(String username) {
        String pattern= "^[a-zA-Z0-9]+$";
        return username.matches(pattern);
    }

    private void dialogAlert(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getApplicationContext().getString(R.string.login_error));
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

    private void loadingDialog(String message){
        dialog = ProgressDialog.show(this, "", message, true);
    }
    private void loadingDialog(Boolean state){
        if(!state) dialog.dismiss();
    }
}

