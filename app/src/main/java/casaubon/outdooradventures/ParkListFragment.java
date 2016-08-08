package casaubon.outdooradventures;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by alepena01 on 7/19/16.
 */
public class ParkListFragment extends Fragment {

    RecyclerView mParksRecyclerView;
    ParkAdapter mAdapter;
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
        queryURL = getArguments().getString(URL_EXTRA);
        sharedPref = getActivity().getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE);
        int tempTesting = sharedPref.getInt("radius", -1);
        Log.d(TAG, "In ParkListFragment onCreateView: " + tempTesting);
        QuerySearch task = new QuerySearch();
        task.execute();
        return view;
    }

    public void setupAdapter() {
        mAdapter = new ParkAdapter(mParkList);
        mParksRecyclerView.setAdapter(mAdapter);
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
                Toast.makeText(getActivity(), "There are no Parks that meet the search criteria. " +
                        "Please check your location settings for any added filters.", Toast.LENGTH_LONG).show();
            }
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
                    return mUtilIcons.size();
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
                        //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setPadding(8, 8, 8, 8);
                    }
                    else {
                        imageView = (ImageView) convertView;
                    }
                    imageView.setImageResource(mUtilIcons.get(position));
                    imageView.setTag(mUtilIcons.get(position));
                    return imageView;
                }
            });
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Snackbar.make(getView(), mUtilPrompts.get(position), Snackbar.LENGTH_SHORT).show();
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