package casaubon.outdooradventures;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by alepena01 on 7/19/16.
 */
public class ParkListFragment extends Fragment {

    RecyclerView mParksRecyclerView;
    ParkAdapter mAdapter;
    ArrayList<OutdoorDetails> mParkList = new ArrayList<OutdoorDetails>(100);
    private final static String URL_EXTRA = "url";
    private String queryURL;
    private static final String TAG = "ParkListFragment";

    public static ParkListFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(URL_EXTRA, url);

        ParkListFragment fragment = new ParkListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_park_list, container, false);
        mParksRecyclerView = (RecyclerView) view.findViewById(R.id.park_recycler_view);
        mParksRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        queryURL = getArguments().getString(URL_EXTRA);

        QuerySearch task = new QuerySearch();
        task.execute();
        return view;
    }

    public void setupAdapter() {
        mAdapter = new ParkAdapter(mParkList);
        mParksRecyclerView.setAdapter(mAdapter);
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
            setupAdapter();
        }
    }

    private class ParkAdapter extends RecyclerView.Adapter<ParkHolder> {
        private ArrayList<OutdoorDetails> mParks;

        public ParkAdapter(ArrayList<OutdoorDetails> parkList) {
            mParks = parkList;
        }

        @Override
        public ParkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.result_item, parent, false);
            return new ParkHolder(view);
        }

        @Override
        public void onBindViewHolder(ParkHolder holder, int position) {
            OutdoorDetails park = mParks.get(position);
            holder.bindPark(park);
        }

        @Override
        public int getItemCount() {
            return mParks.size();
        }
    }

    private class ParkHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public OutdoorDetails mPark;
        private TextView mNameTextView;
        private TextView mStateTextView;

        public ParkHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mNameTextView = (TextView) itemView.findViewById(R.id.park_name);
            mStateTextView = (TextView) itemView.findViewById(R.id.park_state);
        }

        public void bindPark(OutdoorDetails park) {
            mPark = park;
            mNameTextView.setText(mPark.getName());
            mStateTextView.setText(mPark.getState());
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, mPark.toString());
            Intent i = new Intent(getActivity(), ParkDetail.class);
            i.putExtra("mID", mPark);
            i.putExtra("mName", mPark);
            i.putExtra("mState", mPark);
            i.putExtra("mLat", mPark);
            i.putExtra("mLngt", mPark);
            i.putExtra("mAmpOutlet", mPark);
            i.putExtra("mPetsAllowed", mPark);
            i.putExtra("mSewerHookup", mPark);
            i.putExtra("mWaterHookup", mPark);
            startActivity(i);
        }
    }
}
