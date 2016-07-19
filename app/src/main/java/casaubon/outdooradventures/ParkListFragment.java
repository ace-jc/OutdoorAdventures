package casaubon.outdooradventures;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by alepena01 on 7/19/16.
 */
public class ParkListFragment extends Fragment {

    ListView mParksListView;
    ArrayList<OutdoorDetails> parkList = new ArrayList<OutdoorDetails>(100);
    private final static String URL_EXTRA = "url";
    private String queryURL;
    private static final String TAG = "ParkListFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_park_list, container, false);

        mParksListView = (ListView) view.findViewById(R.id.part_list_view);

        return view;
    }
}
