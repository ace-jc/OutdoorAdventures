package casaubon.outdooradventures;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
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
    TextView parkName;
    TextView parkPhone;
    TextView parkAddress;
    TextView amenitiesList;
    TextView ratingText;
    Button callbtn;
    Button wwwbtn;
    Button addressbtn;
    Context mContext;
    GoogleApiClient mGoogleApiClient;
    RatingBar ratingBar;



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
        // setting up the textviews and buttons
        parkName = (TextView)findViewById(R.id.textView1);
        parkPhone = (TextView)findViewById(R.id.textView2);
        parkAddress = (TextView)findViewById(R.id.textView3);
        amenitiesList = (TextView)findViewById(R.id.textView4);
        ratingText = (TextView)findViewById(R.id.textView8);
        callbtn = (Button)findViewById(R.id.callbutton);
        wwwbtn = (Button)findViewById(R.id.wwwbutton);
        addressbtn = (Button)findViewById(R.id.navigatebutton);
        // setting the actual text
        parkName.setText(selectedPark.getName());
        parkPhone.setText("Phone number: (not available)");
        parkAddress.setText("Address: " + selectedPark.getState());
        amenitiesList.setText("Amenities: " + selectedPark.amenitiesList());
        callbtn.setBackgroundResource(R.drawable.callbutton);
        wwwbtn.setBackgroundResource(R.drawable.www);
        addressbtn.setBackgroundResource(R.drawable.navigation);
        // ratings bar
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Google Places API call

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mGoogleApiClient.connect();
        // saving context
        mContext = this.getApplicationContext();
        Log.i(TAG, "We are sending in the following park : " + selectedPark.getName());
        DisplayResults resultsOfQuery = new DisplayResults();
        resultsOfQuery.execute(selectedPark.getName() + " " + selectedPark.getState());
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
            String output = "";
            Log.i(TAG, "Number in doInBackground: " + autocompletePredictions.getCount());
            if(autocompletePredictions.getCount() == 0){
                Log.i(TAG, "autocompletePredictions.get(0).getPlaceId() is zero");
                return null;
            }
            if(autocompletePredictions.get(0).getPlaceId() instanceof String){
                output = autocompletePredictions.get(0).getPlaceId();
            }else{
                Log.i(TAG, "It is NOT a string or zero found");
            }
            autocompletePredictions.release();
            return output;

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
                                    // phone setup
                                    if(myPlace.getPhoneNumber() != null){
                                        String phone = myPlace.getPhoneNumber().toString();
                                        phone = (phone.replace("+1 ", "")); // making phone number nicer looking
                                        // setting up phone button
                                        if(phone.equals(""))
                                        {
                                            // Setting text if no phone exists
                                            phone = "Not Available";
                                        } else{
                                            // if phone exists create button
                                            callbtn.setVisibility(View.VISIBLE);
                                            callbtn.setBackgroundResource(R.drawable.callbutton);
                                        }
                                        parkPhone.setText(phone);
                                    }else{
                                        Log.e(TAG, "myPlace.getPhoneNumber() is null");
                                    }

                                    // url setup
                                    if(myPlace.getWebsiteUri() != null){
                                        String url = myPlace.getWebsiteUri().toString();
                                        // setting up www button
                                        if(url.equals("")){
                                            // no www exists
                                            url = "Not Available";
                                        }else{
                                            // www does indeed exist
                                            wwwbtn.setVisibility(View.VISIBLE);
                                            wwwbtn.setBackgroundResource(R.drawable.www);
                                        }
                                    }else{
                                        Log.e(TAG, "myPlace.getWebsiteUri() is null");
                                    }

                                    // navigation setup
                                    if(myPlace.getAddress() != null){
                                        // setting up www button
                                        String address = myPlace.getAddress().toString();
                                        if(address.equals("")){
                                            // no www exists
                                            address = "Not Available";
                                        }else{
                                            // address does indeed exist
                                            addressbtn.setVisibility(View.VISIBLE);
                                            addressbtn.setBackgroundResource(R.drawable.navigation);
                                        }
                                        parkAddress.setText(address);
                                    }else{
                                        Log.e(TAG, "myPlace.getAddress() is null");
                                    }

                                    //set ratingsbar
                                    float rating = myPlace.getRating();
                                    if(rating != -1){
                                        ratingBar.setRating(rating);
                                        if(rating > 4.0){
                                            ratingText.setText("Amazing Park!!!");
                                        }else if(rating > 3.0){
                                            ratingText.setText("Good Park!");
                                        }else if(rating > 2.0){
                                            ratingText.setText("OK Park!");
                                        }else if(rating > 1.0){
                                            ratingText.setText("Not a very good Park!");
                                        }else{
                                            ratingText.setText("Terrible Park!");
                                        }
                                    }else{
                                        ratingText.setText("No Reviews Available");
                                    }

                                    // Setting the remaining text
                                    parkName.setText(selectedPark.getName());
                                    amenitiesList.setText(selectedPark.amenitiesList());
                                } else {
                                    Log.e(TAG, "Place not found");
                                }
                                // the following ensures we don't have a memory leak
                                places.release();
                                mGoogleApiClient.disconnect();
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