package casaubon.outdooradventures;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainMenuActivity extends AppCompatActivity {

    // private member variables
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
        Log.d(TAG, "In nearMeCall");
//        startActivity(new Intent(this, NearMe.class));
    }

    public void stateSearchCall(View view) {
        Log.d(TAG, "In stateSearchCall");
        url = new BuildUrl();
        Intent i = new Intent(this, StateSearch.class);
        i.putExtra("actualURL", url);
        i.putExtra("state", url);
        i.putExtra("parkActivity", url);
        i.putExtra("stateCreated", url);
        i.putExtra("parkCreated", url);
        startActivity(i);
    }

    public void locationPreferencesCall(View view) {
        Log.d(TAG, "In locationPreferencesCall");
//        startActivity(new Intent(this, LocationPreferencesCall.class));
    }

    public void aboutCall(View view) {
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
