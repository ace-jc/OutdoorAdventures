package casaubon.outdooradventures;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import java.util.HashMap;
import java.util.Map;

public class ParkActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // private variables here
    private static final String TAG = "Outdoor Adventures";
    private BuildUrl url;
    private String tempPark;
    private Map<String, String> activityMap;
    RelativeLayout mScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScreen = (RelativeLayout) findViewById(R.id.ParkActivityMainBackground);
        setContentView(R.layout.activity_park);
        Spinner spinner = (Spinner) findViewById(R.id.parkActivitySpinner);
        spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.parkActivity, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // creating Map with park activity data
        activityMapCluster();
        // filling url object with data from previous activity
        url = (BuildUrl) getIntent().getParcelableExtra("actualURL");
        url = (BuildUrl) getIntent().getParcelableExtra("state");
        url = (BuildUrl) getIntent().getParcelableExtra("parkActivity");
        url = (BuildUrl) getIntent().getParcelableExtra("stateCreated");
        url = (BuildUrl) getIntent().getParcelableExtra("parkCreated");
        url = (BuildUrl) getIntent().getParcelableExtra("lati");
        url = (BuildUrl) getIntent().getParcelableExtra("longi");
        url = (BuildUrl) getIntent().getParcelableExtra("radius");
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // This method needs to exist :(
        // Another interface callback
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statesearch_menu, menu);
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

    public void submitAPIcall(View view) {
        Log.d(TAG, "In submitAPIcall");
        // creating a BuildURL object to add the state and pass to next activity
        Log.d(TAG, "In submitState BEFORE NEW stateCreated? " + url.addStateCheck());
        url.addParkActivity(activityMap.get(tempPark));

        Intent i = new Intent(this, ResultPagerActivity.class);
        // saving variables before starting next activity
        i.putExtra("actualURL", url);
        i.putExtra("state", url);
        i.putExtra("parkActivity", url);
        i.putExtra("stateCreated", url);
        i.putExtra("parkCreated", url);
        i.putExtra("lati", url);
        i.putExtra("longi", url);
        i.putExtra("radius", url);
        Log.d(TAG, "In submitAPIcall");
        Log.d(TAG, "In submitAPIcall stateCreated? " + url.addStateCheck());
        Log.d(TAG, "In submitAPIcall parkCreated?: " + url.addParkCheck());
        startActivity(i);
    }


    private void activityMapCluster() {
        // instantiating a HashMap and filling with data
        activityMap = new HashMap<String, String>();
        activityMap.put("Biking","4001");
        activityMap.put("Boating","4002");
        activityMap.put("Equipment Rental","4003");
        activityMap.put("Fishing","4004");
        activityMap.put("Golf","4005");
        activityMap.put("Hiking","4006");
        activityMap.put("Horseback Riding","4007");
        activityMap.put("Hunting","4008");
        activityMap.put("Recreational Activities","4009");
        activityMap.put("Scenic Trails","4010");
        activityMap.put("Sports","4011");
        activityMap.put("Beach/Water Activities","4012");
        activityMap.put("Winter Activities","4013");
        activityMap.put("No Park Activity Preference", "0");
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Log.d(TAG, "NUMBER IS: " + parent.getItemAtPosition(pos).toString());
        // saving the current (temporary) park activity in variable
        tempPark = parent.getItemAtPosition(pos).toString();
        mScreen = (RelativeLayout) findViewById(R.id.ParkActivityMainBackground);
        if(tempPark.equals("No Park Activity Preference"))
            mScreen.setBackgroundResource(R.drawable.noactivitypreference);
        if(tempPark.equals("Biking"))
            mScreen.setBackgroundResource(R.drawable.biking);
        if(tempPark.equals("Boating"))
            mScreen.setBackgroundResource(R.drawable.boating);
        if(tempPark.equals("Equipment Rental"))
            mScreen.setBackgroundResource(R.drawable.equiptmentrental);
        if(tempPark.equals("Fishing"))
            mScreen.setBackgroundResource(R.drawable.fishing);
        if(tempPark.equals("Golf"))
            mScreen.setBackgroundResource(R.drawable.golf);
        if(tempPark.equals("Hiking"))
            mScreen.setBackgroundResource(R.drawable.hiking);
        if(tempPark.equals("Horseback Riding"))
            mScreen.setBackgroundResource(R.drawable.horsebackriding);
        if(tempPark.equals("Hunting"))
            mScreen.setBackgroundResource(R.drawable.hunting);
        if(tempPark.equals("Recreational Activities"))
            mScreen.setBackgroundResource(R.drawable.recreationalactivities);
        if(tempPark.equals("Scenic Trails"))
            mScreen.setBackgroundResource(R.drawable.scenictrails);
        if(tempPark.equals("Beach/Water Activities"))
            mScreen.setBackgroundResource(R.drawable.beachwateractivities);
        if(tempPark.equals("Sports"))
            mScreen.setBackgroundResource(R.drawable.sports);
        if(tempPark.equals("Winter Activities"))
            mScreen.setBackgroundResource(R.drawable.winteractivities);
    }
}