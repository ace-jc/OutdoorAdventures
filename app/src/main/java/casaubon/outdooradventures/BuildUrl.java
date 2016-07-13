package casaubon.outdooradventures;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by j on 7/12/16.
 */
public class BuildUrl implements Parcelable {

    // debug tag
    private static final String TAG = "Outdoor Adventures";

    // to build url
    private static final String urlStart = "http://api.amp.active.com/camping/campgrounds/?";
    private static final String apiKey = "api_key=a67c926wtb9qtwu4vnhyev53";

    // building URL here
    private String actualURL;

    // variables
    private int stateCreated;
    private int parkCreated;
    private String state;
    private String parkActivity;

    public BuildUrl() {
        // only add initial url in construction
        actualURL = urlStart;
        // initially do not have state or parkActivity fields filled
        stateCreated = 0;
        parkCreated = 0;
    }


    public void addState(String selectedState) {
        // save US state
        state = selectedState;
        stateCreated = 1;
    }


    public String checkActualURL() {
        return this.actualURL;
    }

    public void buildURLFresh() {
        // building URL with information if it exists
        actualURL = urlStart;
        if(stateCreated == 1)
            actualURL += "pstate=" + state + "&";
        if(parkCreated == 1)
            actualURL += "amenity=" + parkActivity + "&";
        // adding apikey to the end of the URL
        actualURL += apiKey;
    }

    public void addParkActivity(String selectedActivity) {
        // save park activity
        parkActivity = selectedActivity;
        parkCreated = 1;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // writing out when copying the object
        out.writeString(actualURL);
        out.writeString(state);
        out.writeString(parkActivity);
        out.writeInt(stateCreated);
        out.writeInt(parkCreated);
    }

    private BuildUrl(Parcel in) {
        // writing in when creating the object from another activity
        actualURL = in.readString();
        state = in.readString();
        parkActivity = in.readString();
        stateCreated = in.readInt();
        parkCreated = in.readInt();
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
