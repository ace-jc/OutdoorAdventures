package casaubon.outdooradventures;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class ResultPagerActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private ViewPager mViewPager;
    private static final String TAG = "ResultPagerActivity";
    private static final String EXTRA_URL = "casaubon.outdooradventures.result_pager.extra_url";
    private String query_url;
    private String prev_query_url = null;
    private BuildUrl url;
    private boolean firstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_pager);

        firstRun = true;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        url = (BuildUrl) getIntent().getParcelableExtra("actualURL");
        url = (BuildUrl) getIntent().getParcelableExtra("state");
        url = (BuildUrl) getIntent().getParcelableExtra("parkActivity");
        url = (BuildUrl) getIntent().getParcelableExtra("stateCreated");
        url = (BuildUrl) getIntent().getParcelableExtra("parkCreated");
        url = (BuildUrl) getIntent().getParcelableExtra("lati");
        url = (BuildUrl) getIntent().getParcelableExtra("longi");
        url = (BuildUrl) getIntent().getParcelableExtra("radius");

        url.buildURLFresh(ResultPagerActivity.this);
        Log.d(TAG, "url is: " + url.checkActualURL());
        Log.d(TAG, "radius is: " + url.checkRadius());
        SharedPreferences sharedPref = getSharedPreferences("LocationPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        // saving radius and lati and longi in non-volatile memory
        editor.putInt("radius", url.checkRadius());
        editor.putFloat("lati", url.getLatiFloat());
        editor.putFloat("longi", url.getLongiFloat());
        editor.apply();
        query_url = url.checkActualURL();
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        prev_query_url = query_url;
    }

    @Override
    protected void onResume() {
        super.onResume();
        url.buildURLFresh(ResultPagerActivity.this);
        query_url = url.checkActualURL();
        // check if the user changed his preferences and does new search
        if (prev_query_url != null && !prev_query_url.equals(query_url) || firstRun) {
            //TODO: check if the user changed his preferences
            Log.d(TAG, "in onResume url is: " + url.checkActualURL());

            FragmentManager fragmentManager = getSupportFragmentManager();
            mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
                @Override
                public Fragment getItem(int position) {
                    // tab 1: List View
                    if (position == 0) {
                        return ParkListFragment.newInstance(query_url);
                    }
                    //tab 2: Map View
                    else {
                        return ParkMapFragment.newInstance(query_url);
                    }
                }

                @Override
                public int getCount() {
                    return 2;
                }

                @Override
                public CharSequence getPageTitle(int position) {
                    switch (position) {
                        case 0:
                            return "LIST VIEW";
                        case 1:
                            return "MAP VIEW";
                        default:
                            return null;
                    }
                }
            });
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
            firstRun = false;
        }
        prev_query_url = query_url;
    }

    public static Intent newIntent(Context packageContext, BuildUrl url) {
        Intent intent = new Intent(packageContext, ResultPagerActivity.class);
        intent.putExtra(EXTRA_URL, url);
        return intent;
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
        getMenuInflater().inflate(R.menu.menu_result_pager, menu);
        return true;
    }
}
