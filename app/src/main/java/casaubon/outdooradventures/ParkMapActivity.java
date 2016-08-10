package casaubon.outdooradventures;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class ParkMapActivity extends SingleFragmentActivity {
    private final static String EXTRA_URL = "casaubon.outdooradventures.park_list.extra_url";

    public static Intent newIntent(Context packageContext, String url) {
        Intent intent = new Intent(packageContext, ParkMapActivity.class);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new ParkMapFragment().newInstance(getIntent().getStringExtra(EXTRA_URL));
    }
}
