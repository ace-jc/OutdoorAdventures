package casaubon.outdooradventures;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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


public class ParkMapFragment extends Fragment {
    private static final String EXTRA_URL = "casaubon.outdooradventures.url";
    private static final String TAG = "ParkMapFragment";
    OutdoorCoreData coreData;
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

    // Setup Map Fragment
    public void setMapFragment(@Nullable SupportMapFragment mapFragment) {
        final FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction
                    .add(R.id.map, mMapFragment)
                    .commit();
        }

        else {
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
                    //run OutdoorData
                    coreData = new OutdoorCoreData(getActivity(), queryURL, false) {
                        @Override
                        protected void updateView(ArrayList<OutdoorDetails> parks) {
                            mParkList = parks;
                            setupPins(parks);
                        }
                    };
                    coreData.startSearch();
                }
                else {
                    setupPins(mParkList);
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
    public void onDestroy() {
        super.onDestroy();

        //clean up for any open connections and garbage collection
        if (coreData != null) {
            coreData.stopSearch();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        setMapFragment(mMapFragment);
    }

    //Get marker position
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

    //setup pins in maps
    public void setupPins(ArrayList<OutdoorDetails> parks) {
        for (int i = 0; i < parks.size(); i++) {
            OutdoorDetails curPark = parks.get(i);
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
}