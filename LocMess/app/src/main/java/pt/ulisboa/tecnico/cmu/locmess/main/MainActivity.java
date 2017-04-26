package pt.ulisboa.tecnico.cmu.locmess.main;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.view.View;

import pt.ulisboa.tecnico.cmu.locmess.main.locations.LocationsFragment;
import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessagesFragment;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.ProfileFragment;
import pt.ulisboa.tecnico.cmu.locmess.R;

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

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
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

        }
        return true;
    }
}
