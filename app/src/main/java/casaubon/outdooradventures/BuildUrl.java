package casaubon.outdooradventures;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by j on 7/12/16.
 */
public class BuildUrl extends AppCompatActivity implements Parcelable {

    // debug tag
    private static final String TAG = "Outdoor Adventures";

    // to build url
    private static final String urlStart = "https://www.reserveamerica.com/campgroundSearch.do?"; // http://api.amp.active.com/camping/campgrounds/?";
    private static final String apiKey = "api_key=72fhbjk366qjzk5y5seuxyzv"; //"api_key=a67c926wtb9qtwu4vnhyev53";
    private static final String outputType = "xml=true&expwith=1&expfits=1";

    // building URL here
    private String actualURL;

    // variables
    private int stateCreated;
    private int parkCreated;
    private int radius;
    private String state;
    private String parkActivity;
    private String lati;
    private String longi;
    private String sharedPreferences;
    Context ctx;

    public BuildUrl() {
        // only add initial url in construction
        actualURL = urlStart;
        state = "";
        sharedPreferences = "";
        stateCreated = 0;
        parkCreated = 0;
        lati = "0";
        longi = "0";
        radius = 1000000; // default will include all states
    }

    public void addState(String selectedState) {
        // save US state
        state = selectedState;
        stateCreated = 1;
    }


    public int addStateCheck(){
        return stateCreated;
    }


    public int addParkCheck(){
        return parkCreated;
    }


    public void setLati(String inputLat) {
        // setting latitude
        Log.d(TAG, "setLati inputLat:" + inputLat);
        lati = inputLat;
    }


    public void setLongi(String inputLongi) {
        // setting longitude
        Log.d(TAG, "setLongi inputLat:" + inputLongi);
        longi = inputLongi;
    }

    public float getLatiFloat(){
        return Float.parseFloat(lati);
    }

    public float getLongiFloat(){
        return Float.parseFloat(longi);
    }

    public String checkActualURL() {
        return this.actualURL;
    }


    public void setRadius(int input){
        radius = input;
    }


    public int checkRadius(){
        return radius;
    }


    public void buildURLFresh(Context inputContext) {
        // building URL with information if it exists
        setPreferences(inputContext);
        actualURL = urlStart;
        if(stateCreated == 1)
            actualURL += "pstate=" + state + "&";
        if(parkCreated == 1)
            actualURL += "amenity=" + parkActivity + "&";
        if(stateCreated == 0) {
            actualURL += "landmarkLat=" + lati + "&landmarkLong=" + longi + "&";
            actualURL += "landmarkName=true&";
        }
        actualURL += sharedPreferences;
        // adding apikey to the end of the URL
//        actualURL += apiKey;
        // adding output type to the end of the URL
        actualURL += outputType;
    }


    private void setPreferences(Context inputContext) {
        // useless comment
        ctx = inputContext;
        String temp = "";
        SharedPreferences sharedPref = ctx.getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE);
        Log.d(TAG, "sewer bool is: " + sharedPref.getBoolean("Sewer Hookup", false));
        Log.d(TAG, "water bool is: " + sharedPref.getBoolean("Water Hookup", false));
        Log.d(TAG, "electric hookup bool is: " + sharedPref.getBoolean("Electric Hookup", false));
        Log.d(TAG, "pets bool is: " + sharedPref.getBoolean("Pets Allowed", false));
        Log.d(TAG, "waterfront: " + sharedPref.getBoolean("Waterfront Sites", false));
        if(sharedPref.getBoolean("Sewer Hookup", false)) {
            temp += "sewer=3007&";
        }
        if(sharedPref.getBoolean("Water Hookup", false)) {
            temp += "water=3006&";
        }
        if(sharedPref.getBoolean("Electric Hookup", false)) {
            temp += "hookup=3002&";
        }
        if(sharedPref.getBoolean("Pets Allowed", false)) {
            temp += "pets=3010&";
        }
        if(sharedPref.getBoolean("Waterfront Sites", false)) {
            temp += "waterfront=3011&";
        }
        sharedPreferences = temp;
    }

    public void addParkActivity(String selectedActivity) {
        // save park activity
        if(selectedActivity.equals("0")) {
            Log.d(TAG, "No Preference is O: output is:" + selectedActivity.equals("0"));
            parkCreated = 0;
        }
        else {
            parkActivity = selectedActivity;
            parkCreated = 1;
        }
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // writing out when copying the object
        out.writeString(actualURL);
        out.writeString(state);
        out.writeString(parkActivity);
        out.writeInt(stateCreated);
        out.writeInt(parkCreated);
        out.writeString(lati);
        out.writeString(longi);
        out.writeInt(radius);
    }

    private BuildUrl(Parcel in) {
        // writing in when creating the object from another activity
        actualURL = in.readString();
        state = in.readString();
        parkActivity = in.readString();
        stateCreated = in.readInt();
        parkCreated = in.readInt();
        lati = in.readString();
        longi = in.readString();
        radius = in.readInt();
    }

    @Override
    // This is what the internet said... :O
    public int describeContents() {
        return 0;
    }


    public static final Parcelable.Creator<BuildUrl> CREATOR
            = new Parcelable.Creator<BuildUrl>() {

        @Override
        public BuildUrl createFromParcel(Parcel in) {
            return new BuildUrl(in);
        }

        @Override
        public BuildUrl[] newArray(int size) {
            return new BuildUrl[size];
        }
    };
}
