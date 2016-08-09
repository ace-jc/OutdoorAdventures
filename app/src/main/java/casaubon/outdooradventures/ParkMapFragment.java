package casaubon.outdooradventures;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by alepena01 on 7/27/16.
 */
public class ParkMapFragment extends Fragment {
    private static final String EXTRA_URL = "casaubon.outdooradventures.url";
    private static final String TAG = "ParkMapFragment";
    private GoogleMap mMap;
    private String queryURL;
    private ArrayList<OutdoorDetails> mParkList = new ArrayList<OutdoorDetails>(100);
    private ArrayList<Marker> markers = new ArrayList<Marker>(100);
    SharedPreferences sharedPref;
    SupportMapFragment mMapFragment;
    private boolean newSearch = true;

    public static ParkMapFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(EXTRA_URL, url);

        ParkMapFragment fragment = new ParkMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_park_map, container, false);
        queryURL = getArguments().getString(EXTRA_URL);
        sharedPref = getActivity().getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        return view;
    }

    public void setMapFragment(@Nullable SupportMapFragment mapFragment) {
        final FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (mMapFragment == null) {
            //Toast.makeText(getActivity(), "new map fragment", Toast.LENGTH_SHORT).show();
            mMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction
                    .add(R.id.map, mMapFragment)
                    .commit();
        }

        else {
            //Toast.makeText(getActivity(), "existing map fragment", Toast.LENGTH_SHORT).show();
            fragmentTransaction
                    .replace(R.id.map, mMapFragment)
                    .commit();
        }

        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "onMapReady");
                mMap = googleMap;
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        LatLng loc = marker.getPosition();
                        Log.d (TAG, "marker location: " + loc.latitude + " " + loc.longitude);
                        int position = getMarkerPos(marker);
                        if (position != -1) {
                            OutdoorDetails selectedPark = mParkList.get(position);
                            Log.d(TAG, selectedPark.toString());
                            Intent i = new Intent(getActivity(), ParkDetail.class);
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
                            // This is a useless comment
                        }
                    }
                });

                mMap.getUiSettings().setScrollGesturesEnabled(false);
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                LatLng center = new LatLng(38.68551, -96.503906);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 3));
                if (newSearch) {
                    QuerySearch task = new QuerySearch();
                    task.execute();
                }
                else {
                    setupPins();
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        final FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .remove(mMapFragment)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        setMapFragment(mMapFragment);
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
                if(sharedPref.getFloat("lati", 0) == 0) {
                    curMarker = mMap.addMarker(new MarkerOptions()
                            .position(curParkLoc)
                            .title(curPark.getName())
                            .snippet("Location: " + curPark.getLatitude() + ", " + curPark.getLongitude()));
                }
                else {
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
            OutdoorCoreData coreData = new OutdoorCoreData(getActivity(), queryURL);
            return coreData.searchQuery(false);
        }

        @Override
        protected void onPostExecute(ArrayList<OutdoorDetails> items) {
            mParkList = items;
            Log.d(TAG, "list size BEFORE radius applied: " + mParkList.size());
            ensureInRadius();
            Log.d(TAG, "list size AFTER radius applied: " + mParkList.size());
            setupPins();
            newSearch = false;
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