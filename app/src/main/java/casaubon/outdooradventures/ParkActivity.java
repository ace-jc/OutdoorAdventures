package casaubon.outdooradventures;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.Map;

public class ParkActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // private variables
    private static final String TAG = "Outdoor Adventures";
    private BuildUrl url;
    private String tempPark;
    private Map<String, String> activityMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Log.d(TAG, "NUMBER IS: " + parent.getItemAtPosition(pos).toString());
        // saving the current (temporary) park activity in variable
        tempPark = parent.getItemAtPosition(pos).toString();
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
        url.addParkActivity(activityMap.get(tempPark));
        url.setPreferences(addSharedPrefstoURL());
        url.buildURLFresh();
        Log.d(TAG, "In submitAPIcall and URL is: " + url.checkActualURL());
        //Start List Result Activity
        Intent intent = ResultTabBarActivity.newIntent(this, url.checkActualURL());
        startActivity(intent);
    }

    private String addSharedPrefstoURL() {
        String temp = "";
//        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences sharedPref = getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE);
        Log.d(TAG, "sewer bool is: " + sharedPref.getBoolean("Sewer Hookup", false));
        Log.d(TAG, "water bool is: " + sharedPref.getBoolean("Water Hookup", false));
        Log.d(TAG, "pulldriveway bool is: " + sharedPref.getBoolean("Pull Through Driveway", false));
        Log.d(TAG, "pets bool is: " + sharedPref.getBoolean("Pets Allowed", false));
        Log.d(TAG, "waterfront: " + sharedPref.getBoolean("Waterfront Sites", false));
        if(sharedPref.getBoolean("Sewer Hookup", false)) {
            temp += "sewer=3007&";
        }
        if(sharedPref.getBoolean("Water Hookup", false)) {
            temp += "water=3006&";
        }
        if(sharedPref.getBoolean("Pull Through Driveway", false)) {
            temp += "pull=3008&";
        }
        if(sharedPref.getBoolean("Pets Allowed", false)) {
            temp += "pets=3010&";
        }
        if(sharedPref.getBoolean("Waterfront Sites", false)) {
            temp += "waterfront=3011&";
        }
        return temp;
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

    private void preferenceCluster() {
        // instantiating a HashMap and filling with data
        activityMap = new HashMap<String, String>();
        activityMap.put("Water Hookup","3006");
        activityMap.put("Sewer Hookup","3007");
        activityMap.put("Pull Through Driveway","3008");
        activityMap.put("Pets Allowed","3010");
        activityMap.put("Waterfront Sites","3011");
    }
}
