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
import android.widget.Spinner;

import java.util.HashMap;
import java.util.Map;

public class StateSearch extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "Outdoor Adventures";
    private BuildUrl url;
    private String tempState;
    private Map<String, String> statesMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_search);
        Spinner spinner = (Spinner) findViewById(R.id.stateSpinner);
        spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.states, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        stateMapCluster();
        url = (BuildUrl) getIntent().getParcelableExtra("actualURL");
        url = (BuildUrl) getIntent().getParcelableExtra("state");
        url = (BuildUrl) getIntent().getParcelableExtra("parkActivity");
        url = (BuildUrl) getIntent().getParcelableExtra("stateCreated");
        url = (BuildUrl) getIntent().getParcelableExtra("parkCreated");
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        Log.d(TAG, "NUMBER IS: " + parent.getItemAtPosition(pos).toString());
        tempState = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
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
        switch (item.getItemId()) {
            case R.id.MainMenu:
                startActivity(new Intent(this, MainMenuActivity.class));
                return true;
            case R.id.LocationPreferencesMenu:
                startActivity(new Intent(this, AboutPage.class));
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
        url = new BuildUrl();
        url.addState(statesMap.get(tempState));
        Intent i = new Intent(this, ParkActivity.class);
        i.putExtra("actualURL", url);
        i.putExtra("state", url);
        i.putExtra("parkActivity", url);
        i.putExtra("stateCreated", url);
        i.putExtra("parkCreated", url);
        startActivity(i);
    }

    private void stateMapCluster() {
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
}
