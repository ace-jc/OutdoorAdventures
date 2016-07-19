package casaubon.outdooradventures;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import java.util.ArrayList;

public class LocationPreferences extends ListActivity {

    //private variables
    private static final String TAG = "Outdoor Adventures";
    private boolean mfirstRun;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ArrayList<PreferenceRowData> list = new ArrayList<PreferenceRowData>();
        String[] prefs = getResources().getStringArray(R.array.preferenceList);
        checkForFirstRun();
        if(mfirstRun) {
            // first time running the app
            Log.d(TAG, "First time running the app");
            firstRunSetInstanceVarsFromSharedPrefs();
            for (String s : prefs) {
                list.add(new PreferenceRowData(s, false));
            }
        }
        else{
            // have run the app before and loading from memory
            Log.d(TAG, "Not first time running the app");
            for (String s : prefs) {
                list.add(new PreferenceRowData(s, savedPreferenceRestore(s)));
            }
        }
        setListAdapter(new SafeAdapter(list));
    }

    private boolean savedPreferenceRestore(String s) {
        SharedPreferences sharedPref = getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE);
//        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        return sharedPref.getBoolean(s, false);
    }

    private void checkForFirstRun() {
        SharedPreferences sharedPref = getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE);
//        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        mfirstRun = sharedPref.getBoolean("mfirstRun", true);
    }


    private void firstRunSetInstanceVarsFromSharedPrefs() {
        SharedPreferences sharedPref = getSharedPreferences("LocationPreferences", MODE_PRIVATE);
//        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        // setting up the variables for the first time in the xml file
        editor.putBoolean("mfirstRun", false); // after this not the first run
        editor.putBoolean("Sewer Hookup", false);
        editor.putBoolean("Water Hookup", false);
        editor.putBoolean("Pull Through Driveway", false);
        editor.putBoolean("Pets Allowed", false);
        editor.putBoolean("Waterfront Sites", true);
        editor.apply();
    }

    private void saveCurrentStateofPreferences(String str, boolean bool) {
        Log.d(TAG, "saving the current state to the shared preferences");
        SharedPreferences sharedPref = getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE);
//        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(str, bool);
        editor.apply();
    }


    private static class PreferenceRowData {
        private String name;
        private boolean safe;

        private PreferenceRowData(String n, boolean s) {
            name = n;
            safe = s;
        }

        public String toString() {
            return name;
        }
    }

    private PreferenceRowData getModel(int position) {
        return(((SafeAdapter)getListAdapter()).getItem(position));
    }

    // code adapted from The Busy Coder's Guide to Android Development
    // pages 1139 - 1140.
    private class SafeAdapter extends ArrayAdapter<PreferenceRowData> {

        SafeAdapter(ArrayList<PreferenceRowData> list) {
            super(LocationPreferences.this,
                    R.layout.complex_list_item,
                    R.id.preferenceTextView,
                    list);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = super.getView(position, convertView, parent);
            Switch theSwitch = (Switch) row.getTag();
            if (theSwitch == null) {
                theSwitch = (Switch) row.findViewById(R.id.preferenceSwitch);
                row.setTag(theSwitch);

                CompoundButton.OnCheckedChangeListener l = new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Integer myPosition=(Integer) buttonView.getTag();
                        PreferenceRowData model = getModel(myPosition);
                        Log.d(TAG, "isChecked: " + isChecked);
                        Log.d(TAG, "model.toString(): " + model.toString());
                        saveCurrentStateofPreferences(model.toString(), isChecked);
                        model.safe = isChecked;
                        LinearLayout parent = (LinearLayout) buttonView.getParent();
                        TextView label = (TextView)parent.findViewById(R.id.preferenceTextView);
                        label.setText(model.toString());
                    }
                };
                theSwitch.setOnCheckedChangeListener(l);
            }

            PreferenceRowData model = getModel(position);
            theSwitch.setTag(position);
            theSwitch.setChecked(model.safe);
            return(row);
        }
    }



}
