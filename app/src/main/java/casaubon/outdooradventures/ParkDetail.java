package casaubon.outdooradventures;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ParkDetail extends AppCompatActivity implements OnMapReadyCallback {

    // private variables
    private OutdoorDetails selectedPark;
    GoogleMap mMap;

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
        TextView parkName = (TextView)findViewById(R.id.textView6);
        parkName.setText(selectedPark.getName());
        TextView lati = (TextView)findViewById(R.id.textView7);
        lati.setText("Latitude: " + selectedPark.getLatitude());
        TextView longi = (TextView)findViewById(R.id.textView8);
        longi.setText("Longitude: " + selectedPark.getLongitude());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
            case R.id.AboutAppMenu:
                startActivity(new Intent(this, AboutPage.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
