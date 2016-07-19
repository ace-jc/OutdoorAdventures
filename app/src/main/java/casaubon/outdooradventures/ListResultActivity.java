package casaubon.outdooradventures;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ListResultActivity extends AppCompatActivity {

    ListView parksListView;
    ArrayList<OutdoorDetails> parkList = new ArrayList<OutdoorDetails>(100);
    private final static String URL_EXTRA = "url";
    private String queryURL;
    private static final String TAG = "ListResultActivity";

    public static Intent newIntent (Context packageContext, String url) {
        Intent intent = new Intent(packageContext, ListResultActivity.class);
        intent.putExtra(URL_EXTRA, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_result);
        parksListView = (ListView) findViewById(R.id.list);
        queryURL = getIntent().getStringExtra(URL_EXTRA);
        QuerySearch task = new QuerySearch();
        task.execute();
        setupAdapter();
    }

    private void setupAdapter() {
        parksListView.setAdapter(new OutdoorDetailAdapter(this, parkList));
    }

    public void parkSelected(View view) {
        Toast.makeText(this, "position: " + view.getTag(), Toast.LENGTH_SHORT).show();
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
            parkList = items;
            Log.d(TAG, "list size: " + parkList.size());
            setupAdapter();
        }
    }
}
