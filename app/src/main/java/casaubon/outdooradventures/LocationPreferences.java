package casaubon.outdooradventures;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationPreferences extends ListActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ArrayList<PreferenceRowData> list = new ArrayList<PreferenceRowData>();
        String[] prefs = getResources().getStringArray(R.array.countries);
        for (String s : prefs) {
            list.add(new PreferenceRowData(s, false));
        }
        setListAdapter(new SafeAdapter(list));
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
                    R.id.countryTextView,
                    list);
        }

        public View getView(int position, View convertView,
                            ViewGroup parent) {

            View row = super.getView(position, convertView, parent);
            Switch theSwitch = (Switch) row.getTag();
            if (theSwitch == null) {
                theSwitch = (Switch) row.findViewById(R.id.preferenceSwitch);
                row.setTag(theSwitch);

                CompoundButton.OnCheckedChangeListener l =
                        new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView,
                                                         boolean isChecked) {
                                Integer myPosition=(Integer) buttonView.getTag();
                                PreferenceRowData model = getModel(myPosition);
                                model.safe = isChecked;
                                LinearLayout parent = (LinearLayout) buttonView.getParent();
                                TextView label =
                                        (TextView)parent.findViewById(R.id.countryTextView);
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
