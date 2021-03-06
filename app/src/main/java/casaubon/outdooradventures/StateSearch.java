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

public class StateSearch extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "Outdoor Adventures";
    private BuildUrl url;
    private String tempState;
    private Map<String, String> statesMap;
    Spinner spinner;
    RelativeLayout mScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScreen = (RelativeLayout) findViewById(R.id.stateSearchMainBackground);
        setContentView(R.layout.activity_state_search);
        spinner = (Spinner) findViewById(R.id.stateSpinner);
        spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.states, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // creating Map with park activity data
        stateMapCluster();
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

    public void submitState(View view) {
        Log.d(TAG, "In submitState");
        // creating a BuildURL object to add the state and pass to next activity
        Log.d(TAG, "In submitState BEFORE NEW stateCreated? " + url.addStateCheck());
//        url = new BuildUrl();
        url.addState(statesMap.get(tempState));
        Intent i = new Intent(this, ParkActivity.class);
        // saving variables before starting next activity
        i.putExtra("actualURL", url);
        i.putExtra("state", url);
        i.putExtra("parkActivity", url);
        i.putExtra("stateCreated", url);
        i.putExtra("parkCreated", url);
        i.putExtra("lati", url);
        i.putExtra("longi", url);
        i.putExtra("radius", url);
        Log.d(TAG, "In submitState");
        Log.d(TAG, "In submitState stateCreated? " + url.addStateCheck());
        Log.d(TAG, "In submitState parkCreated?: " + url.addParkCheck());
        startActivity(i);
    }

    private void stateMapCluster() {
        // instantiating a HashMap and filling with data
        statesMap = new HashMap<String, String>();
        statesMap.put("Alabama","AL");
        statesMap.put("Alaska","AK");
        statesMap.put("Arizona","AZ");
        statesMap.put("Arkansas","AR");
        statesMap.put("California","CA");
        statesMap.put("Colorado","CO");
        statesMap.put("Connecticut","CT");
        statesMap.put("Delaware","DE");
        statesMap.put("Florida","FL");
        statesMap.put("Georgia","GA");
        statesMap.put("Hawaii","HI");
        statesMap.put("Idaho","ID");
        statesMap.put("Illinois","IL");
        statesMap.put("Indiana","IN");
        statesMap.put("Iowa","IA");
        statesMap.put("Kansas","KS");
        statesMap.put("Kentucky","KY");
        statesMap.put("Louisiana","LA");
        statesMap.put("Maine","ME");
        statesMap.put("Maryland","MD");
        statesMap.put("Massachusetts","MA");
        statesMap.put("Michigan","MI");
        statesMap.put("Minnesota","MN");
        statesMap.put("Mississippi","MS");
        statesMap.put("Missouri","MO");
        statesMap.put("Montana","MT");
        statesMap.put("Nebraska","NE");
        statesMap.put("Nevada","NV");
        statesMap.put("New Hampshire","NH");
        statesMap.put("New Jersey","NJ");
        statesMap.put("New Mexico","NM");
        statesMap.put("New York","NY");
        statesMap.put("North Carolina","NC");
        statesMap.put("North Dakota","ND");
        statesMap.put("Ohio","OH");
        statesMap.put("Oklahoma","OK");
        statesMap.put("Oregon","OR");
        statesMap.put("Pennsylvania","PA");
        statesMap.put("Rhode Island","RI");
        statesMap.put("South Carolina","SC");
        statesMap.put("South Dakota","SD");
        statesMap.put("Tennessee","TN");
        statesMap.put("Texas","TX");
        statesMap.put("Utah","UT");
        statesMap.put("Vermont","VT");
        statesMap.put("Virginia","VA");
        statesMap.put("Washington","WA");
        statesMap.put("West Virginia","WV");
        statesMap.put("Wisconsin","WI");
        statesMap.put("Wyoming","WY");
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        Log.d(TAG, "NUMBER IS: " + parent.getItemAtPosition(pos).toString());
        mScreen = (RelativeLayout) findViewById(R.id.stateSearchMainBackground);
        // saving the current (temporary) park activity in variable
        tempState = parent.getItemAtPosition(pos).toString();
        if(parent.getItemAtPosition(pos).toString().equals("Alabama"))
            mScreen.setBackgroundResource(R.drawable.alabama);
        if(parent.getItemAtPosition(pos).toString().equals("Alaska"))
            mScreen.setBackgroundResource(R.drawable.alaska);
        if(parent.getItemAtPosition(pos).toString().equals("Arizona"))
            mScreen.setBackgroundResource(R.drawable.arizona);
        if(parent.getItemAtPosition(pos).toString().equals("Arkansas"))
            mScreen.setBackgroundResource(R.drawable.arkansas);
        if(parent.getItemAtPosition(pos).toString().equals("California"))
            mScreen.setBackgroundResource(R.drawable.california);
        if(parent.getItemAtPosition(pos).toString().equals("Colorado"))
            mScreen.setBackgroundResource(R.drawable.colorado);
        if(parent.getItemAtPosition(pos).toString().equals("Connecticut"))
            mScreen.setBackgroundResource(R.drawable.connecticut);
        if(parent.getItemAtPosition(pos).toString().equals("Delaware"))
            mScreen.setBackgroundResource(R.drawable.delaware);
        if(parent.getItemAtPosition(pos).toString().equals("Florida"))
            mScreen.setBackgroundResource(R.drawable.florida);
        if(parent.getItemAtPosition(pos).toString().equals("Georgia"))
            mScreen.setBackgroundResource(R.drawable.georgia);
        if(parent.getItemAtPosition(pos).toString().equals("Hawaii"))
            mScreen.setBackgroundResource(R.drawable.hawaii);
        if(parent.getItemAtPosition(pos).toString().equals("Idaho"))
            mScreen.setBackgroundResource(R.drawable.idaho);
        if(parent.getItemAtPosition(pos).toString().equals("Illinois"))
            mScreen.setBackgroundResource(R.drawable.illinois);
        if(parent.getItemAtPosition(pos).toString().equals("Indiana"))
            mScreen.setBackgroundResource(R.drawable.indiana);
        if(parent.getItemAtPosition(pos).toString().equals("Iowa"))
            mScreen.setBackgroundResource(R.drawable.iowa);
        if(parent.getItemAtPosition(pos).toString().equals("Kansas"))
            mScreen.setBackgroundResource(R.drawable.kansas);
        if(parent.getItemAtPosition(pos).toString().equals("Kentucky"))
            mScreen.setBackgroundResource(R.drawable.kentucky);
        if(parent.getItemAtPosition(pos).toString().equals("Louisiana"))
            mScreen.setBackgroundResource(R.drawable.louisiana);
        if(parent.getItemAtPosition(pos).toString().equals("Maine"))
            mScreen.setBackgroundResource(R.drawable.maine);
        if(parent.getItemAtPosition(pos).toString().equals("Maryland"))
            mScreen.setBackgroundResource(R.drawable.maryland);
        if(parent.getItemAtPosition(pos).toString().equals("Massachusetts"))
            mScreen.setBackgroundResource(R.drawable.massachusetts);
        if(parent.getItemAtPosition(pos).toString().equals("Michigan"))
            mScreen.setBackgroundResource(R.drawable.michigan);
        if(parent.getItemAtPosition(pos).toString().equals("Minnesota"))
            mScreen.setBackgroundResource(R.drawable.minnesota);
        if(parent.getItemAtPosition(pos).toString().equals("Mississippi"))
            mScreen.setBackgroundResource(R.drawable.mississippi);
        if(parent.getItemAtPosition(pos).toString().equals("Missouri"))
            mScreen.setBackgroundResource(R.drawable.missouri);
        if(parent.getItemAtPosition(pos).toString().equals("Montana"))
            mScreen.setBackgroundResource(R.drawable.montana);
        if(parent.getItemAtPosition(pos).toString().equals("Nebraska"))
            mScreen.setBackgroundResource(R.drawable.nebraska);
        if(parent.getItemAtPosition(pos).toString().equals("Nevada"))
            mScreen.setBackgroundResource(R.drawable.nevada);
        if(parent.getItemAtPosition(pos).toString().equals("New Hampshire"))
            mScreen.setBackgroundResource(R.drawable.newhampshire);
        if(parent.getItemAtPosition(pos).toString().equals("New Jersey"))
            mScreen.setBackgroundResource(R.drawable.newjersey);
        if(parent.getItemAtPosition(pos).toString().equals("New Mexico"))
            mScreen.setBackgroundResource(R.drawable.newmexico);
        if(parent.getItemAtPosition(pos).toString().equals("New York"))
            mScreen.setBackgroundResource(R.drawable.newyork);
        if(parent.getItemAtPosition(pos).toString().equals("North Carolina"))
            mScreen.setBackgroundResource(R.drawable.northcarolina);
        if(parent.getItemAtPosition(pos).toString().equals("North Dakota"))
            mScreen.setBackgroundResource(R.drawable.northdakota);
        if(parent.getItemAtPosition(pos).toString().equals("Ohio"))
            mScreen.setBackgroundResource(R.drawable.ohio);
        if(parent.getItemAtPosition(pos).toString().equals("Oklahoma"))
            mScreen.setBackgroundResource(R.drawable.oklahoma);
        if(parent.getItemAtPosition(pos).toString().equals("Oregon"))
            mScreen.setBackgroundResource(R.drawable.oregon);
        if(parent.getItemAtPosition(pos).toString().equals("Pennsylvania"))
            mScreen.setBackgroundResource(R.drawable.pennsylvania);
        if(parent.getItemAtPosition(pos).toString().equals("Rhode Island"))
            mScreen.setBackgroundResource(R.drawable.rhodeisland);
        if(parent.getItemAtPosition(pos).toString().equals("South Carolina"))
            mScreen.setBackgroundResource(R.drawable.southcarolina);
        if(parent.getItemAtPosition(pos).toString().equals("South Dakota"))
            mScreen.setBackgroundResource(R.drawable.southdakota);
        if(parent.getItemAtPosition(pos).toString().equals("Tennessee"))
            mScreen.setBackgroundResource(R.drawable.tennessee);
        if(parent.getItemAtPosition(pos).toString().equals("Texas"))
            mScreen.setBackgroundResource(R.drawable.texas);
        if(parent.getItemAtPosition(pos).toString().equals("Utah"))
            mScreen.setBackgroundResource(R.drawable.utah);
        if(parent.getItemAtPosition(pos).toString().equals("Vermont"))
            mScreen.setBackgroundResource(R.drawable.vermont);
        if(parent.getItemAtPosition(pos).toString().equals("Virginia"))
            mScreen.setBackgroundResource(R.drawable.virginia);
        if(parent.getItemAtPosition(pos).toString().equals("Washington"))
            mScreen.setBackgroundResource(R.drawable.washington);
        if(parent.getItemAtPosition(pos).toString().equals("West Virginia"))
            mScreen.setBackgroundResource(R.drawable.westvirginia);
        if(parent.getItemAtPosition(pos).toString().equals("Wisconsin"))
            mScreen.setBackgroundResource(R.drawable.wisconsin);
        if(parent.getItemAtPosition(pos).toString().equals("Wyoming"))
            mScreen.setBackgroundResource(R.drawable.wyoming);
    }
}