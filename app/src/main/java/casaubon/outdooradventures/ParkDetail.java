package casaubon.outdooradventures;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

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
    boolean phoneExists;
    boolean wwwExists;
    boolean addressExists;
    String url;
    String phone;
    String address;
    ImageView imageViewContainer;



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
        // setting up imageview
        imageViewContainer = (ImageView)findViewById(R.id.imageViewImages);
        // setting the actual text
        parkName.setText(selectedPark.getName());
        parkPhone.setText("");
        parkAddress.setText("");
        amenitiesList.setText(selectedPark.amenitiesList());
        // setting up buttons to default with no functionality
        callbtn.setBackgroundResource(R.drawable.nocallbuttonpic);
        wwwbtn.setBackgroundResource(R.drawable.nowwwpic);
        addressbtn.setBackgroundResource(R.drawable.nonavigationpic);
        // ratings bar
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        // setting up bools for buttons
        phoneExists = false;
        wwwExists = false;
        addressExists = false;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // setting up strings for later use
        url = "";
        phone = "";
        address = "";


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
            Log.i(TAG, "N " + autocompletePredictions.getCount());
            if(autocompletePredictions.getCount() == 0){
                Log.i(TAG, "autocompletePredictions.get(0).getPlaceId() is zero");
                return null;
            }
            if(autocompletePredictions.get(0).getPlaceId() instanceof String){
                output = autocompletePredictions.get(0).getPlaceId();
            }else{
                Log.i(TAG, "It is NOT a string");
                return null;
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
                                        Log.e(TAG, "(myPlace.getPhoneNumber() != null");
                                        phone = myPlace.getPhoneNumber().toString();
                                        phone = (phone.replace("+1 ", "")); // making phone number nicer looking
                                        // setting up phone button
                                        if(phone.equals(""))
                                        {
                                            Log.e(TAG, "in phone.equals()");
                                            // Setting text if no phone exists
                                            phone = "Not Available";
                                            callbtn.setBackgroundResource(R.drawable.nocallbuttonpic);
                                        } else{
                                            Log.e(TAG, "in else");
                                            // if phone exists create button
                                            Log.e(TAG, "Phone Number is:" + phone);
                                            phoneExists = true;
                                            callbtn.setBackgroundResource(R.drawable.callbuttonpic);
                                        }
                                        parkPhone.setText(phone);
                                    }else{
                                        Log.e(TAG, "myPlace.getPhoneNumber() is null");
                                        phone = "Not Available";
                                        parkPhone.setText(phone);
                                    }

                                    // url setup
                                    if(myPlace.getWebsiteUri() != null){
                                        url = myPlace.getWebsiteUri().toString();
                                        // setting up www button
                                        if(url.equals("")){
                                            // no www exists
                                            url = "Not Available";
                                            wwwbtn.setBackgroundResource(R.drawable.nowwwpic);

                                        }else{
                                            // www does indeed exist
                                            wwwExists = true;
                                            wwwbtn.setBackgroundResource(R.drawable.wwwpic);
                                        }
                                    }else{
                                        Log.e(TAG, "myPlace.getWebsiteUri() is null");
                                    }

                                    // navigation setup
                                    if(myPlace.getAddress() != null){
                                        Log.e(TAG, "myPlace.getAddress() is: " + myPlace.getAddress());
                                        // setting up address button
                                        address = myPlace.getAddress().toString();
                                        if(address.equals("")){
                                            // no address exists
                                            address = selectedPark.getLatitude() + " " + selectedPark.getLongitude();
                                            addressbtn.setBackgroundResource(R.drawable.nonavigationpic);
                                        }else{
                                            // address does indeed exist
                                            addressExists = true;
                                            addressbtn.setBackgroundResource(R.drawable.navigationpic);
                                        }
                                        parkAddress.setText(address);
                                    }else{
                                        Log.e(TAG, "myPlace.getAddress() is null");
                                        address = selectedPark.getLatitude() + " " + selectedPark.getLongitude();
                                        parkAddress.setText(address);
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
                            }
                        });
            }else{
                // handles null case on input string
                Log.e(TAG, "do in Background returned null");
                phone = "Not Available";
                parkPhone.setText(phone);
                address = selectedPark.getLatitude() + " " + selectedPark.getLongitude();
                parkAddress.setText(address);
            }
            if(input != null){
                placePhotosAsync(input);
            }else{
                // Setting photo here
                Log.e(TAG, "input was null");
                Drawable myDrawable = getResources().getDrawable(R.drawable.defaultparkdetail);
                imageViewContainer.setImageDrawable(myDrawable);
            }
        }
    }


    public void phoneButtonPress(View view){
        if(phoneExists){
            Log.e(TAG, "Phone number exists it is: " + phone);
            Uri call = Uri.parse("tel:" + phone);
            Intent surf = new Intent(Intent.ACTION_DIAL, call);
            startActivity(surf);
        }else{
            Toast.makeText(ParkDetail.this, "Phone number does not exists", Toast.LENGTH_LONG).show();
        }
    }

    public void wwwButtonPress(View view){
        if(wwwExists){
            Log.e(TAG, "url exists it is: " + url);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }else{
            Toast.makeText(ParkDetail.this, "No Web Link Exists", Toast.LENGTH_LONG).show();
        }
    }

    public void addressButtonPress(View view){
        if(addressExists){
            Log.e(TAG, "address exists it is: " + address);
            String uri = String.format(Locale.ENGLISH, "google.navigation:q=%f,%f", selectedPark.getLatitude(), selectedPark.getLongitude());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        }else{
            Toast.makeText(ParkDetail.this, "Missing Physical Address", Toast.LENGTH_LONG).show();
        }
    }


    public void onConnectionFailed (ConnectionResult result){
        //Fail gracefully when connection fails
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


    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback
            = new ResultCallback<PlacePhotoResult>() {
        @Override
        public void onResult(PlacePhotoResult placePhotoResult) {
            Log.i(TAG, "In onResult of ResultCallback");
            if (!placePhotoResult.getStatus().isSuccess()) {
                return;
            }
            imageViewContainer.setImageBitmap(placePhotoResult.getBitmap());
        }
    };

    /**
     * Load a bitmap from the photos API asynchronously
     * by using buffers and result callbacks.
     */
    private void placePhotosAsync(String input) {
        Log.i(TAG, "In placePhotosAsync with input: " + input);
        final String placeId = input;
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {

                    @Override
                    public void onResult(PlacePhotoMetadataResult photos) {
                        Log.i(TAG, "In onResult of placePhotosAsync");
                        if (!photos.getStatus().isSuccess()) {
                            return;
                        }

                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        Log.i(TAG, "In onResult the photoMetadataBuffer.getCount(): " + photoMetadataBuffer.getCount());
                        if (photoMetadataBuffer.getCount() > 0) {
                            // Display the first bitmap in an ImageView in the size of the view
                            photoMetadataBuffer.get(0)
                                    .getScaledPhoto(mGoogleApiClient, imageViewContainer.getWidth(),
                                            imageViewContainer.getHeight())
                                    .setResultCallback(mDisplayPhotoResultCallback);
                        }else{
                            // Setting photo here
                            Drawable myDrawable = getResources().getDrawable(R.drawable.defaultparkdetail);
                            imageViewContainer.setImageDrawable(myDrawable);
                        }
                        photoMetadataBuffer.release();
//                        mGoogleApiClient.disconnect();
                    }
                });
    }


}