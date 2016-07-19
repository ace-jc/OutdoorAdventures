package casaubon.outdooradventures;

import android.support.v4.app.Fragment;

/**
 * Created by alepena01 on 7/19/16.
 */
public class ParkListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ParkListFragment();
    }
}
