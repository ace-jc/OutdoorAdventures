package casaubon.outdooradventures;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

public class MainMenuActivity extends AppCompatActivity {

    // private variables
    private static final String TAG = "MainMenuActivity";
    private BuildUrl url;
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

    public void nearMeCall(View view) {
        // Will make use of GPS return position and will start activity with result saved
        boolean network_enabled = false;
        Log.d(TAG, "In nearMeCall");
        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            network_enabled = mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {
            Log.d(TAG, "Error with: mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);");
        }
        // borrowed from http://stackoverflow.com/questions/10311834/how-to-check-if-location-services-are-enabled
        if(!network_enabled){
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(this.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // nothing
                }
            });
            dialog.show();
        }
        else{
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
    }


    public void moveToNextActivity() {
        try {
            lastKnownLocation = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException e) {
            Log.d(TAG, "Error with the call to: mLocationManager.requestLocationUpdates(...)");
        }
        if(lastKnownLocation != null){
            Log.d(TAG, "If was true and lastKnownLocation != null");
            url = new BuildUrl();
            lati = lastKnownLocation.getLatitude();
            longi = lastKnownLocation.getLongitude();
            url.setLati(String.valueOf(lati));
            url.setLongi(String.valueOf(longi));

            // Will look for a radius clicked from the user before creating the next activity
            AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this)
                    .setTitle("Select maximum distance to your Adventure")
                    .setItems(R.array.radiusmiles, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position of the selected item
                            // Should be the three radii checked in the if statement below
                            Log.d(TAG, "The index of the item selected was: " + which);
                            if( which==0 || which==1 || which==2 ){
                                // 50 miles, 100 miles, or 300 miles
                                switch (which){
                                    case 0: url.setRadius(50); break; // setting radius to 50
                                    case 1: url.setRadius(100); break; // setting radius to 100
                                    case 2: url.setRadius(300); break; // setting radius to 300
                                }
                                Intent i = new Intent(MainMenuActivity.this, ParkActivity.class);
                                // saving variables before starting next activity
                                i.putExtra("actualURL", url);
                                i.putExtra("state", url);
                                i.putExtra("parkActivity", url);
                                i.putExtra("stateCreated", url);
                                i.putExtra("parkCreated", url);
                                i.putExtra("lati", url);
                                i.putExtra("longi", url);
                                i.putExtra("radius", url);
                                startActivity(i);
                            }
                        }
                    });
            builder.create().show();
        }
        else{
            Log.d(TAG, "Fell into else case");
            Toast.makeText(MainMenuActivity.this, "Could not find your location...", Toast.LENGTH_LONG).show();
        }
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
        i.putExtra("radius", url);
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
}
