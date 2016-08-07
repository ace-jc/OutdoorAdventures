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
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
public class ParkDetail extends AppCompatActivity implements OnMapReadyCallback, OnConnectionFailedListener {

    // private variables
    private static final String TAG = "Outdoor Adventures";
    private OutdoorDetails selectedPark;
    GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    TextView parkName;
    TextView parkPhone;
    TextView parkAddress;
    TextView amenitiesList;



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
        // setting up the textviews
        parkName = (TextView)findViewById(R.id.textView1);
        parkPhone = (TextView)findViewById(R.id.textView2);
        parkAddress = (TextView)findViewById(R.id.textView3);
        amenitiesList = (TextView)findViewById(R.id.textView4);
        // setting the actual text
        parkName.setText(selectedPark.getName());
        parkPhone.setText("Phone number: (not available)");
        parkAddress.setText("Address: " + selectedPark.getState());
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
        resultsOfQuery.execute(selectedPark.getName() + selectedPark.getState());
        Log.i(TAG, "AFTER : " + selectedPark.getName());

    }

    private class DisplayResults extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "in doInBackground with query: " + params[0]);
            AutocompleteFilter filter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                    .build();
            PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, params[0], null, filter);
            AutocompletePredictionBuffer autocompletePredictions = results.await();
            Log.i(TAG, "Number in doInBackground: " + autocompletePredictions.getCount());
            return autocompletePredictions.get(0).getPlaceId();
        }

        @Override
        protected void onPostExecute(String input) {
            if(input != null){
                Log.i(TAG, "in onPostExecute with String : " + input);
                Places.GeoDataApi.getPlaceById(mGoogleApiClient, input)
                        .setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places) {
                                if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                    final Place myPlace = places.get(0);
                                    // debug items
                                    Log.i(TAG, "The number of places in the array is: " + places.getCount());
                                    Log.i(TAG, "Place found: " + myPlace.getName());
                                    Log.i(TAG, "Place address: " + myPlace.getAddress());
                                    Log.i(TAG, "ID is: " + myPlace.getId());
                                    Log.i(TAG, "Place phone: " + myPlace.getPhoneNumber());
                                    Log.i(TAG, "URL is: " + myPlace.getWebsiteUri());
                                    Log.i(TAG, "Price level is: " + myPlace.getPriceLevel());
                                    Log.i(TAG, "Rating is: " + myPlace.getRating());
                                    // setting text of textviews here
                                    parkName.setText(selectedPark.getName());
                                    parkPhone.setText("Phone: " + myPlace.getPhoneNumber());
                                    parkAddress.setText("Address: " + myPlace.getAddress());
                                    amenitiesList.setText("Amenities: " + selectedPark.amenitiesList());
//                                    TextView parkAddress = (TextView)findViewById(R.id.textView7);
//                                    parkAddress.setText(myPlace.getAddress());
//                                    parkPhone = (TextView)findViewById(R.id.textView3);
//                                    parkPhone.setText("Phone number: " + myPlace.getPhoneNumber());
//                                    TextView amenitiesList = (TextView)findViewById(R.id.textView8);
//                                    amenitiesList.setText("Amenities: " + selectedPark.amenitiesList());
//                                    Log.i(TAG, "Amenities: " + selectedPark.amenitiesList());

                                    // this ensures we don't have a memory leak
                                    places.release();
                                    mGoogleApiClient.disconnect();
                                } else {
                                    Log.e(TAG, "Place not found");
                                }
                            }
                        });
            }
            else{
                Log.e(TAG, "do in Background returned null");
            }

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
