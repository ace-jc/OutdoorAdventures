package casaubon.outdooradventures;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;

public class ParkListFragment extends Fragment {

    RecyclerView mParksRecyclerView;
    ProgressBar mProgressBar;
    ParkAdapter mAdapter;
    OutdoorCoreData coreData;
    ArrayList<OutdoorDetails> mParkList = new ArrayList<OutdoorDetails>(100);
    private final static String URL_EXTRA = "url";
    private String queryURL;
    SharedPreferences sharedPref;
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
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.VISIBLE);
        queryURL = getArguments().getString(URL_EXTRA);
        sharedPref = getActivity().getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE);
        int tempTesting = sharedPref.getInt("radius", -1);
        Log.d(TAG, "In ParkListFragment onCreateView: " + tempTesting);
        coreData = new OutdoorCoreData(getActivity(), queryURL, true) {
            @Override
            protected void updateView(ArrayList<OutdoorDetails> parks) {
                mParkList = parks;
                setupAdapter();
                if(mParkList.size() == 0){
                    Toast.makeText(getActivity(), "There are no Parks that meet the search criteria. " +
                        "Please check your location settings for any added filters.", Toast.LENGTH_LONG).show();
                }
                mProgressBar.setVisibility(View.GONE);
                ((ViewGroup) mProgressBar.getParent()).removeView(mProgressBar);
            }
        };
        coreData.startSearch();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //clean up for any open connections and garbage collection
        if (coreData != null) {
            coreData.stopSearch();
        }

    }

    public void setupAdapter() {
        mAdapter = new ParkAdapter(mParkList);
        mParksRecyclerView.setAdapter(mAdapter);
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
        private GridView mGridView;
        private CircleImageView mParkThmb;
        private ArrayList<Integer> mUtilIcons;
        private ArrayList<String> mUtilPrompts;

        public ParkHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.park_name);
            mStateTextView = (TextView) itemView.findViewById(R.id.park_state);
            mGridView = (GridView) itemView.findViewById(R.id.amenities_icons);
            mParkThmb = (CircleImageView) itemView.findViewById(R.id.park_image);
            itemView.setOnClickListener(this);
        }

        public void bindPark(OutdoorDetails park) {
            mPark = park;
            getThumbIds();
            mNameTextView.setText(mPark.getName());
            mStateTextView.setText(mPark.getState());
            mParkThmb.setImageBitmap(mPark.getImage());
            mGridView.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    int size = mUtilIcons.size();
                    return (size != 0) ? size : 1;
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
                    ImageView imageView;
                    if (convertView == null) {
                        imageView = new ImageView(getActivity());
                        //imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setPadding(8, 8, 8, 8);
                    }
                    else {
                        imageView = (ImageView) convertView;
                    }
                    if (mUtilIcons.size() != 0) {
                        imageView.setImageResource(mUtilIcons.get(position));
                        imageView.setTag(mUtilIcons.get(position));
                    }
                    else {
                        imageView.setImageResource(R.drawable.sewer_hookup);
                        imageView.setAlpha(0);
                        imageView.setEnabled(false);
                    }
                    return imageView;
                }
            });
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mUtilIcons.size() != 0) {
                        Snackbar.make(getView(), mUtilPrompts.get(position), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
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
            i.putExtra("mWaterFront", mPark);
            startActivity(i);
        }

        public void getThumbIds() {
            mUtilIcons = new ArrayList<Integer>(5);
            mUtilPrompts = new ArrayList<String>(5);
            String[] prefs = getResources().getStringArray(R.array.preferenceList);

            if (mPark.hasSewerHookup()) {
                mUtilIcons.add(R.drawable.sewer_hookup);
                mUtilPrompts.add(prefs[0]);
            }
            if (mPark.hasWaterHookup()) {
                mUtilIcons.add(R.drawable.water_hookup);
                mUtilPrompts.add(prefs[1]);
            }
            if (mPark.hasAmpOutlet()) {
                mUtilIcons.add(R.drawable.electric_hookup);
                mUtilPrompts.add(prefs[2]);
            }
            if (mPark.isPetsAllowed()) {
                mUtilIcons.add(R.drawable.pets_allowed);
                mUtilPrompts.add(prefs[3]);
            }
            if (mPark.hasWaterFront()) {
                mUtilIcons.add(R.drawable.waterfront);
                mUtilPrompts.add(prefs[4]);
            }

        }

    }
}