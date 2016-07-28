package casaubon.outdooradventures;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

import java.util.ArrayList;

public class ResultTabBarActivity extends TabActivity implements TabHost.OnTabChangeListener {

    private final static String EXTRA_URL = "url";
    private static final String TAG = "Outdoor Adventures";
    private ViewPager mViewPager;
    private ArrayList<OutdoorDetails> mParksList;
    TabHost tabHost;
    private String queryUrl;
    private BuildUrl url;

    public static Intent newIntent (Context packageContext, String url) {
        Intent intent = new Intent(packageContext, ResultTabBarActivity.class);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "in onResume!!");
        url.buildURLFresh(ResultTabBarActivity.this);
        Log.d(TAG, "url is: " + url.checkActualURL());
        queryUrl = url.checkActualURL();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_tab_bar);

        // filling url object with data from previous activity
        url = (BuildUrl) getIntent().getParcelableExtra("actualURL");
        url = (BuildUrl) getIntent().getParcelableExtra("state");
        url = (BuildUrl) getIntent().getParcelableExtra("parkActivity");
        url = (BuildUrl) getIntent().getParcelableExtra("stateCreated");
        url = (BuildUrl) getIntent().getParcelableExtra("parkCreated");
        url = (BuildUrl) getIntent().getParcelableExtra("lati");
        url = (BuildUrl) getIntent().getParcelableExtra("longi");


        Log.d(TAG, "In onCreate in ResultTabBarActivity");
        url.buildURLFresh(ResultTabBarActivity.this);
        Log.d(TAG, "url is: " + url.checkActualURL());
        queryUrl = url.checkActualURL();

//        queryUrl = getIntent().getStringExtra(EXTRA_URL);
        tabHost = getTabHost();

        tabHost.setOnTabChangedListener(this);

        TabHost.TabSpec spec;
        Intent intent;

        /* List View */
        intent = ListResultActivity.newIntent(this, queryUrl);
        spec = tabHost.newTabSpec("First").setIndicator("List View")
                .setContent(intent);

        tabHost.addTab(spec);

        /* Map View */
        intent = MapResultActivity.newIntent(this, queryUrl);
        spec = tabHost.newTabSpec("Second").setIndicator("Map View")
                .setContent(intent);

        tabHost.addTab(spec);

    }

    @Override
    public void onTabChanged(String tabId) {
        //take care of icon changes here
    }

}