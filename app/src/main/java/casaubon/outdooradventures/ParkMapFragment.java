package casaubon.outdooradventures;

import android.content.Intent;
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

import com.google.android.gms.cast.LaunchOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by alepena01 on 7/27/16.
 */
public class ParkMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private static final String EXTRA_URL = "casaubon.outdooradventures.url";
    private static final String TAG = "ParkMapFragment";
    private GoogleMap mMap;
    private String queryURL;
    private ArrayList<OutdoorDetails> mParkList = new ArrayList<OutdoorDetails>(100);
    private ArrayList<Marker> markers = new ArrayList<Marker>(100);

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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment;
        mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }

        QuerySearch task = new QuerySearch();
        task.execute();

        return view;
    }

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
        Log.d(TAG, "onMapReady");
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
            if (lngt > -169.804687 && lngt < -66.533203
                    && lang > 24.926295 && lang < 71.497037) {
                Marker curMarker = mMap.addMarker(new MarkerOptions()
                        .position(curParkLoc)
                        .title(curPark.getName())
                        .snippet("Location: " + curPark.getLatitude() + ", " + curPark.getLongitude()));
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
            Log.d(TAG, "list size: " + mParkList.size());
            setupPins();
        }
    }
}
