package casaubon.outdooradventures;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Iterator;

public class ListResultActivity extends AppCompatActivity {

    // private variables
    ListView parksListView;
    ArrayList<OutdoorDetails> mParkList = new ArrayList<>(100);
    private final static String URL_EXTRA = "url";
    private String queryURL;
    private static final String TAG = "ListResultActivity";
    private OutdoorDetails selectedPark;
    SharedPreferences sharedPref;


    public static Intent newIntent (Context packageContext, String url) {
        Intent intent = new Intent(packageContext, ListResultActivity.class);
        intent.putExtra(URL_EXTRA, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE);
        int tempTesting = sharedPref.getInt("radius", -1);
        Log.d(TAG, "In ListResultActivity onCreate: " + tempTesting);
        setContentView(R.layout.activity_list_result);
        parksListView = (ListView) findViewById(R.id.list);
        queryURL = getIntent().getStringExtra(URL_EXTRA);
        QuerySearch task = new QuerySearch();
        task.execute();
        setupAdapter();
    }

    private void setupAdapter() {
        parksListView.setAdapter(new OutdoorDetailAdapter(this, mParkList));
    }

    public void parkSelected(View view) {
        selectedPark = mParkList.get((int) view.getTag());
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
        startActivity(i);
    }

    private class OutdoorDetailAdapter extends BaseAdapter {

        ArrayList<OutdoorDetails> parkList;
        private LayoutInflater detailInf;

        public OutdoorDetailAdapter(Context c, ArrayList<OutdoorDetails> list) {
            parkList = list;
            detailInf = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return parkList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LinearLayout detailLayout = (LinearLayout) detailInf.inflate(R.layout.result_item, parent, false);
            TextView nameView = (TextView) detailLayout.findViewById(R.id.park_name);
            TextView stateView = (TextView) detailLayout.findViewById(R.id.park_state);
            OutdoorDetails currPark = parkList.get(position);

            nameView.setText(currPark.getName());
            stateView.setText(currPark.getState());

            detailLayout.setTag(position);
            return detailLayout;
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
            setupAdapter();
            if(mParkList.size() == 0){
                Toast.makeText(ListResultActivity.this, "There are no Parks that meet the search criteria. " +
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result_list_menu, menu);
        return true;
    }
}