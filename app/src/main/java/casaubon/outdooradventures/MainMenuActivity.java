package casaubon.outdooradventures;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainMenuActivity extends AppCompatActivity {

    // private variables
    private static final String TAG = "Outdoor Adventures";
    private BuildUrl url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Will allow the user to jump to the following menu items
        switch (item.getItemId()) {
            case R.id.LocationPreferencesMenu:
                startActivity(new Intent(this, AboutPage.class));
                return true;
            case R.id.AboutAppMenu:
                startActivity(new Intent(this, AboutPage.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void nearMeCall(View view) {
        // Will make use of GPS return position and will start activity with result saved
        Log.d(TAG, "In nearMeCall");
//        startActivity(new Intent(this, NearMe.class));
    }

    public void stateSearchCall(View view) {
        Log.d(TAG, "In stateSearchCall");
        // creating a BuildURL object to add the state and pass to next activity
        url = new BuildUrl();
        Intent i = new Intent(this, StateSearch.class);
        // saving variables before starting next activity
        i.putExtra("actualURL", url);
        i.putExtra("state", url);
        i.putExtra("parkActivity", url);
        i.putExtra("stateCreated", url);
        i.putExtra("parkCreated", url);
        startActivity(i);
    }

    public void locationPreferencesCall(View view) {
        // starting activity to let user save preferences
        Log.d(TAG, "In locationPreferencesCall");
//        startActivity(new Intent(this, LocationPreferencesCall.class));
    }

    public void aboutCall(View view) {
        // this activity will let the user know information about the app and its usage
        Log.d(TAG, "In aboutCall");
        startActivity(new Intent(this, AboutPage.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

}
