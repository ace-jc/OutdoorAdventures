package casaubon.outdooradventures;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by alepena01 on 7/19/16.
 */
public class ParkListActivity extends SingleFragmentActivity {

    private final static String EXTRA_URL = "casaubon.outdooradventures.park_list.extra_url";

    public static Intent newIntent(Context packageContext, String url) {
        Intent intent = new Intent(packageContext, ParkListActivity.class);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new ParkListFragment().newInstance(getIntent().getStringExtra(EXTRA_URL));
    }
}
