package pt.ulisboa.tecnico.cmu.locmess.main;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.Random;

import pt.ulisboa.tecnico.cmu.locmess.LoginActivity;
import pt.ulisboa.tecnico.cmu.locmess.main.locations.ListSubFragment;
import pt.ulisboa.tecnico.cmu.locmess.main.locations.LocationsFragment;
import pt.ulisboa.tecnico.cmu.locmess.main.locations.MapSubFragment;
import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessagesFragment;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.ProfileFragment;
import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.session.LocMessService;
import pt.ulisboa.tecnico.cmu.locmess.session.Session;

public class MainActivity extends AppCompatActivity {

    private static Fragment fragment;
    private Menu menu;

    public Menu getMenu() {
        return menu;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    setTitle(R.string.title_profile);
                    fragment = ProfileFragment.newInstance();
                    break;
                case R.id.navigation_locations:
                    setTitle(R.string.title_locations);
                    fragment = LocationsFragment.newInstance();
                    break;
                case R.id.navigation_messages:
                    setTitle(R.string.title_messages);
                    fragment = MessagesFragment.newInstance();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        if(this.menu==null) {
            getMenuInflater().inflate(R.menu.main_toolbar, m);
            super.onCreateOptionsMenu(m);
            this.menu = m;
            menu.getItem(2).setVisible(true);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //navigation.inflateMenu(R.menu.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragment = ProfileFragment.newInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment,"current").commit();

        if (!(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 0);
        }

        startService(new Intent(MainActivity.this, LocMessService.class));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    public void logout(MenuItem item){

        ProfileFragment.deleteInstance();
        LocationsFragment.deleteInstance();
        ListSubFragment.deleteInstance();
        MapSubFragment.deleteInstance();
        MessagesFragment.deleteInstance();
        stopService(new Intent(this, LocMessService.class));
        Session.getInstance().logout();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                ((MyFragment) fragment).deleteClicked();
                break;
            case R.id.map:
                ((LocationsFragment) fragment).MapClicked(item);
                break;
            case R.id.logout:
                this.logout(item);
                break;
        }
        return true;
    }

    public void dialogAlert(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
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
