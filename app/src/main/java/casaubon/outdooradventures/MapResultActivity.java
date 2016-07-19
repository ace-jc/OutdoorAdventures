package casaubon.outdooradventures;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapResultActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String EXTRA_URL = "casaubon.outdooradventures.url";
    private static final String TAG = "MapResultActivity";
    private GoogleMap mMap;
    private String queryURL;
    private ArrayList<OutdoorDetails> mParkList = new ArrayList<OutdoorDetails>(100);
    private ArrayList<Marker> markers = new ArrayList<Marker>(100);

    public static Intent newIntent(Context packageContext, String url) {
        Intent intent = new Intent(packageContext, MapResultActivity.class);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_result);
        queryURL = getIntent().getStringExtra(EXTRA_URL);
        Log.d(TAG, "url: " + queryURL);
        QuerySearch task = new QuerySearch();
        task.execute();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        LatLng loc = marker.getPosition();
        Log.d (TAG, "marker location: " + loc.latitude + " " + loc.longitude);
        int position = getMarkerPos(marker);
        if (position != -1) {
            OutdoorDetails selectedPark = mParkList.get(position);
            Log.d(TAG, selectedPark.toString());
            //TODO: open park details activity here
        }
        else {
            //park not found
        }
    }

    public int getMarkerPos(Marker marker) {
        Log.d(TAG, "Marker selected id= " + marker.getId());
        for (int index = 0; index < markers.size(); index++) {
            Log.d(TAG, "marker i= " + index + " id= " + markers.get(index).getId());
            if (markers.get(index).getId().equals(marker.getId())) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
    }

    public void setupPins() {
        for (int i = 0; i < mParkList.size(); i++) {
            OutdoorDetails curPark = mParkList.get(i);
            LatLng curParkLoc = new LatLng(curPark.getLatitude(), curPark.getLongitude());
            if (curPark.getLatitude() != 0 && curPark.getLongitude() != 0) {
                Marker curMarker = mMap.addMarker(new MarkerOptions()
                        .position(curParkLoc)
                        .title(curPark.getName())
                        .snippet("Location: " + curPark.getLatitude() + ", " + curPark.getLongitude()));
                markers.add(curMarker);
            }
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = 175; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    private class QuerySearch extends AsyncTask<Void, Void, ArrayList<OutdoorDetails>> {
        @Override
        protected ArrayList<OutdoorDetails> doInBackground(Void... params) {
            OutdoorCoreData coreData = new OutdoorCoreData(queryURL);
            return coreData.searchQuery();
        }

        @Override
        protected void onPostExecute(ArrayList<OutdoorDetails> items) {
            mParkList = items;
            Log.d(TAG, "list size: " + mParkList.size());
            setupPins();
        }
    }
}
