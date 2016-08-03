package casaubon.outdooradventures;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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
import java.util.Iterator;

public class MapResultActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String EXTRA_URL = "casaubon.outdooradventures.url";
    private static final String TAG = "MapResultActivity";
    private GoogleMap mMap;
    private String queryURL;
    private ArrayList<OutdoorDetails> mParkList = new ArrayList<OutdoorDetails>(100);
    private ArrayList<Marker> markers = new ArrayList<Marker>(100);
    SharedPreferences sharedPref;

    public static Intent newIntent(Context packageContext, String url) {
        Intent intent = new Intent(packageContext, MapResultActivity.class);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE);
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
            Intent i = new Intent(this, ParkDetail.class);
            i.putExtra("mID", selectedPark);
            i.putExtra("mName", selectedPark);
            i.putExtra("mState", selectedPark);
            i.putExtra("mLat", selectedPark);
            i.putExtra("mLngt", selectedPark);
            i.putExtra("mAmpOutlet", selectedPark);
            i.putExtra("mPetsAllowed", selectedPark);
            i.putExtra("mSewerHookup", selectedPark);
            i.putExtra("mWaterHookup", selectedPark);
            i.putExtra("mWaterFront", selectedPark);
            startActivity(i);
        }
        else {
            //park not found
        }
    }

    public int getMarkerPos(Marker marker) {
        Log.d(TAG, "Marker selected id= " + marker.getId());
        for (int index = 0; index < mParkList.size(); index++) {
            if (mParkList.get(index).getMarkerID().equals(marker.getId())) {
                //Log.d(TAG, "marker id= " + markers.get(index).getId());
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
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        LatLng center = new LatLng(38.68551, -96.503906);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 3));
        mMap.setOnInfoWindowClickListener(this);
    }

    public void setupPins() {
        for (int i = 0; i < mParkList.size(); i++) {
            OutdoorDetails curPark = mParkList.get(i);
            LatLng curParkLoc = new LatLng(curPark.getLatitude(), curPark.getLongitude());
            float lngt = curPark.getLongitude();
            float lang = curPark.getLatitude();
            double dist = curPark.getDistance();
            if (lngt > -169.804687 && lngt < -66.533203
                    && lang > 24.926295 && lang < 71.497037) {
                Marker curMarker;
                if(sharedPref.getFloat("lati", 0) == 0){
                    curMarker = mMap.addMarker(new MarkerOptions()
                            .position(curParkLoc)
                            .title(curPark.getName())
                            .snippet("Location: " + curPark.getLatitude() + ", " + curPark.getLongitude()));
                }
                else{
                    curMarker = mMap.addMarker(new MarkerOptions()
                            .position(curParkLoc)
                            .title(curPark.getName())
                            .snippet("Distance: " + (double)Math.round(dist * 100d) / 100d + " miles"));
                }
                markers.add(curMarker);
                curPark.setMarkerID(curMarker.getId());
            }
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        if (markers.size() > 0) {
            LatLngBounds bounds = builder.build();

            int padding = 175; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        }
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
            Log.d(TAG, "list size BEFORE radius applied: " + mParkList.size());
            ensureInRadius();
            Log.d(TAG, "list size AFTER radius applied: " + mParkList.size());
            setupPins();
            if(mParkList.size() == 0){
                Toast.makeText(MapResultActivity.this, "There are no Parks that meet the search criteria. " +
                        "Please check your location settings for any added filters.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void ensureInRadius(){
        int sizeOfRadius = sharedPref.getInt("radius", -1);
        Log.d(TAG, "Size of radius: " + sizeOfRadius);
        Iterator<OutdoorDetails> it = mParkList.iterator();
        // iterating over the list mParkList
        while(it.hasNext()){
            // setting distance for each Park
            OutdoorDetails curr = it.next();
            curr.setDistance(distanceMeasure(sharedPref.getFloat("lati",0),
                    sharedPref.getFloat("longi",0), curr.getLatitude(), curr.getLongitude()));
            // ensuring it is within radius
            Log.d(TAG, "current distance: " + curr.getDistance());
            if(curr.getDistance() > sizeOfRadius){
                it.remove();
            }
        }
    }

    // The following distance equation is from http://stackoverflow.com/questions/15890081/calculate-distance-in-x-y-between-two-gps-points
    private static double distanceMeasure(double lat1, double long1, double lat2, double long2) {
        lat1 *=Math.PI/180;
        lat2 *=Math.PI/180;
        long1*=Math.PI/180;
        long2*=Math.PI/180;

        double dlong = (long2 - long1);
        double dlat  = (lat2 - lat1);

        // Haversine formula:
        double R = 6371;
        double a = Math.sin(dlat/2)*Math.sin(dlat/2) + Math.cos(lat1)*Math.cos(lat2)*Math.sin(dlong/2)*Math.sin(dlong/2);
        double c = 2 * Math.atan2( Math.sqrt(a), Math.sqrt(1-a) );
        return R * c;
    }
}
