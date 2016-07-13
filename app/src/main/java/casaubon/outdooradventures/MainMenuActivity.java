package casaubon.outdooradventures;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
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

import java.util.List;

public class MainMenuActivity extends AppCompatActivity {

    // private variables
    private static final String TAG = "Outdoor Adventures";
    private BuildUrl url;
    AlertDialog alertDialog = null;
    private double lati = 0.0;
    private double longi = 0.0;

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

        // From stackoverflow location listed below
        if (!startService()) {
            CreateAlert("Error!", "Service Cannot be started");
        } else {
            Toast.makeText(MainMenuActivity.this, "Service Started",
                    Toast.LENGTH_LONG).show();
        }
        // creating a BuildURL object to add the state and pass to next activity
        url = new BuildUrl();
        url.setLati(String.valueOf(lati));
        url.setLongi(String.valueOf(longi));
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


    /**
     * From stackoverflow:
     * http://stackoverflow.com/questions/5676653/how-to-get-location-using-asynctask
     * */
    public class FetchCordinates extends AsyncTask<String, Integer, String> {
        ProgressDialog progDialog = null;

        public LocationManager mLocationManager;
        public VeggsterLocationListener mVeggsterLocationListener;

        @Override
        protected void onPreExecute() {
            mVeggsterLocationListener = new VeggsterLocationListener();
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            try {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mVeggsterLocationListener);
            } catch (SecurityException e) {
                Log.d(TAG, "Error with the call to: mLocationManager.requestLocationUpdates(...)");
            }




            progDialog = new ProgressDialog(MainMenuActivity.this);
            progDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    FetchCordinates.this.cancel(true);
                }
            });
            progDialog.setMessage("Loading...");
            progDialog.setIndeterminate(true);
            progDialog.setCancelable(true);
            progDialog.show();

        }

        @Override
        protected void onCancelled(){
            System.out.println("Cancelled by user!");
            progDialog.dismiss();
            try {
                mLocationManager.removeUpdates(mVeggsterLocationListener);
            } catch (SecurityException e) {
                Log.d(TAG, "Error with the call to: mLocationManager.removeUpdates(...)");
            }

        }

        @Override
        protected void onPostExecute(String result) {
            progDialog.dismiss();

            Toast.makeText(MainMenuActivity.this,
                    "LATITUDE :" + lati + " LONGITUDE :" + longi,
                    Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            while (lati == 0.0) {

            }
            return null;
        }

        public class VeggsterLocationListener implements LocationListener {

            @Override
            public void onLocationChanged(Location location) {

                int lat = (int) location.getLatitude(); // * 1E6);
                int log = (int) location.getLongitude(); // * 1E6);
                int acc = (int) (location.getAccuracy());

                String info = location.getProvider();
                try {
                    lati = location.getLatitude();
                    longi = location.getLongitude();

                } catch (Exception e) {
                    // progDailog.dismiss();
                    // Toast.makeText(getApplicationContext(),"Unable to get Location"
                    // , Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i("OnProviderDisabled", "OnProviderDisabled");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i("onProviderEnabled", "onProviderEnabled");
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                Log.i("onStatusChanged", "onStatusChanged");

            }

        }

    }
    public boolean startService() {
        try {
            FetchCordinates fetchCordinates = new FetchCordinates();
            fetchCordinates.execute();
            return true;
        } catch (Exception error) {
            return false;
        }

    }
    public AlertDialog CreateAlert(String title, String message) {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle(title);
        alert.setMessage(message);
        return alert;
    }

}
