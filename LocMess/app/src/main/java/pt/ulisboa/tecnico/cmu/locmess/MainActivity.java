package pt.ulisboa.tecnico.cmu.locmess;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import android.support.v4.app.Fragment;

import pt.ulisboa.tecnico.cmu.locmess.fragments.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private Fragment fragment;

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
                    fragment = LocationsFragment.newInstance("test","test");
                    break;
                case R.id.navigation_messages:
                    setTitle(R.string.title_messages);
                    fragment = MessagesFragment.newInstance("test","test");
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //navigation.inflateMenu(R.menu.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, ProfileFragment.newInstance()).commit();

    }
}
