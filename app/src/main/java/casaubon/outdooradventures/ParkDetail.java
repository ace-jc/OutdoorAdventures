package casaubon.outdooradventures;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.query.Filter;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ParkDetail extends AppCompatActivity implements OnMapReadyCallback, OnConnectionFailedListener {

    // private variables
    private static final String TAG = "Outdoor Adventures";
    private OutdoorDetails selectedPark;
    GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_detail);
        // filling url object with data from previous activity
        selectedPark = (OutdoorDetails) getIntent().getParcelableExtra("mID");
        selectedPark = (OutdoorDetails) getIntent().getParcelableExtra("mName");
        selectedPark = (OutdoorDetails) getIntent().getParcelableExtra("mState");
        selectedPark = (OutdoorDetails) getIntent().getParcelableExtra("mLat");
        selectedPark = (OutdoorDetails) getIntent().getParcelableExtra("mLngt");
        selectedPark = (OutdoorDetails) getIntent().getParcelableExtra("mAmpOutlet");
        selectedPark = (OutdoorDetails) getIntent().getParcelableExtra("mPetsAllowed");
        selectedPark = (OutdoorDetails) getIntent().getParcelableExtra("mSewerHookup");
        selectedPark = (OutdoorDetails) getIntent().getParcelableExtra("mWaterHookup");
        selectedPark = (OutdoorDetails) getIntent().getParcelableExtra("mWaterFront");
        TextView parkName = (TextView)findViewById(R.id.textView2);
        parkName.setText(selectedPark.getName());
        TextView cityStateDistance = (TextView)findViewById(R.id.textView7);
        cityStateDistance.setText("City, " + selectedPark.getState());
        TextView amenitiesList = (TextView)findViewById(R.id.textView8);
        amenitiesList.setText("Amenities: " + selectedPark.amenitiesList());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Google Places API test
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mGoogleApiClient.connect();


        Log.i(TAG, "We are sending in the following park : " + selectedPark.getName());
        DisplayResults resultsOfQuery = new DisplayResults();
        resultsOfQuery.execute(selectedPark.getName());
        Log.i(TAG, "AFTER : " + selectedPark.getName());

    }

    private class DisplayResults extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "in doInBackground with query: " + params[0]);
            AutocompleteFilter filter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                    .build();
            PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi.getAutocompletePredictions( mGoogleApiClient, params[0], null, filter);
            AutocompletePredictionBuffer autocompletePredictions = results.await();
            return autocompletePredictions.get(0).getPlaceId();
        }

        @Override
        protected void onPostExecute(String input) {
            Log.i(TAG, "in onPostExecute with String : " + input);
            Places.GeoDataApi.getPlaceById(mGoogleApiClient, input)
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                final Place myPlace = places.get(0);
                                Log.i(TAG, "Place found: " + myPlace.getName());
                                Log.i(TAG, "Place address: " + myPlace.getAddress());
                                Log.i(TAG, "Place phone: " + myPlace.getPhoneNumber());
                                Log.i(TAG, "Place rating: " + myPlace.getRating());
                            } else {
                                Log.e(TAG, "Place not found");
                            }
//                        places.release();
//                        mGoogleApiClient.disconnect();
                        }
                    });
        }
    }


    public void onConnectionFailed (ConnectionResult result){
        //TODO: Fail gracefully?!?! when connection fails
        Log.d(TAG, "In onConnectionFailed");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        LatLng curParkLoc = new LatLng(selectedPark.getLatitude(), selectedPark.getLongitude());
        Marker curMarker = mMap.addMarker(new MarkerOptions()
                .position(curParkLoc)
                .title(selectedPark.getName()));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curParkLoc, 11));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Will allow the user to jump to the following menu items
        switch (item.getItemId()) {
            case R.id.MainMenu:
                startActivity(new Intent(this, MainMenuActivity.class));
                return true;
            case R.id.LocationPreferencesMenu:
                startActivity(new Intent(this, LocationPreferences.class));
                return true;
            case R.id.AboutAppMenu:
                startActivity(new Intent(this, AboutPage.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
