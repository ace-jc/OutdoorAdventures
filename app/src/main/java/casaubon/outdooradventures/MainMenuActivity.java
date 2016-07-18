package casaubon.outdooradventures;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainMenuActivity extends AppCompatActivity {

    // private variables
    private static final String TAG = "Outdoor Adventures";
    private BuildUrl url;
    AlertDialog alertDialog = null;
    private double lati = 0.0;
    private double longi = 0.0;
    private LocationManager mgr;
    private Location lastKnownLocation;

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
        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Here, thisActivity is the current activity
        Log.d(TAG, "checkSelfPermission: " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission was not granted.. we need it now.
            Log.d(TAG, "Need to check Permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            Log.d(TAG, "Permission was already set");
            moveToNextActivity();
        }
    }


    public void moveToNextActivity() {
        try {
            lastKnownLocation = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException e) {
            Log.d(TAG, "Error with the call to: mLocationManager.requestLocationUpdates(...)");
        }
        url = new BuildUrl();
        lati = lastKnownLocation.getLatitude();
        longi = lastKnownLocation.getLongitude();
        url.setLati(String.valueOf(lati));
        url.setLongi(String.valueOf(longi));
        Intent i = new Intent(this, ParkActivity.class);
        // saving variables before starting next activity
        i.putExtra("actualURL", url);
        i.putExtra("state", url);
        i.putExtra("parkActivity", url);
        i.putExtra("stateCreated", url);
        i.putExtra("parkCreated", url);
        i.putExtra("lati", url);
        i.putExtra("longi", url);
        startActivity(i);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "Granted Results [0]: " + grantResults[0]);
        Log.d(TAG, "Granted Results Length: " + grantResults.length);
        if(PackageManager.PERMISSION_GRANTED == grantResults[0]) { // if permission granted
            // creating a BuildURL object to add the state and pass to next activity
            moveToNextActivity();
        }
        else{
            Log.d(TAG, "In onRequestPermissionsResult... Permission was denied");
            //TODO: Add a toast that says we don't have the permission
            Toast.makeText(MainMenuActivity.this, "Permission was never granted to Outdoor Adventures", Toast.LENGTH_LONG).show();
        }
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
        i.putExtra("lati", url);
        i.putExtra("longi", url);
        startActivity(i);
    }

    public void locationPreferencesCall(View view) {
        // starting activity to let user save preferences
        Log.d(TAG, "In locationPreferencesCall");
        startActivity(new Intent(this, LocationPreferences.class));
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
