package casaubon.outdooradventures;

/**
 * Created by alepena01 on 7/12/16.
 */
public class OutdoorDetails {
    private int mID;
    private String mName;
    private String mState;
    private float mLat;
    private float mLngt;
    private boolean mAmpOutlet;
    private boolean mPetsAllowed;
    private boolean mSewerHookup;
    private boolean mWaterHookup;

    public OutdoorDetails(int id, String name, String state, float latitude, float longitude,
                          boolean hasOutlet, boolean allowsPets, boolean hasSewerHU, boolean hasWaterHU) {
        mID = id;
        mName = name;
        mState = state;
        mLat = latitude;
        mLngt = longitude;
        mAmpOutlet = hasOutlet;
        mPetsAllowed = allowsPets;
        mSewerHookup = hasSewerHU;
        mWaterHookup = hasWaterHU;
    }

    /* getter methods */
    public int getID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public String getState() {
        return mState;
    }

    public float getLatitude() {
        return mLat;
    }

    public float getLongitude() {
        return mLngt;
    }

    public boolean hasAmpOutlet() {
        return mAmpOutlet;
    }

    public boolean isPetsAllowed() {
        return mPetsAllowed;
    }

    public boolean hasSewerHookup() {
        return mSewerHookup;
    }

    public boolean hasWaterHookup() {
        return mWaterHookup;
    }


    public String toString() {
        String details = "";
        details += "facilityID: " + mID + "\n";
        details += "facilityName: " + mName + "\n";
        details += "latitude: " + mLat + "\n";
        details += "longitude: " + mLngt + "\n";
        details += "sitesWithAmps: " + ((mAmpOutlet) ? "Y" : "N") + "\n";
        details += "sitesWithPetsAllowed: " + ((mPetsAllowed) ? "Y" : "N") + "\n";
        details += "sitesWithSewerHookup: " + ((mSewerHookup) ? "Y" : "N") + "\n";
        details += "sitesWithWaterHookup: " + ((mWaterHookup) ? "Y" : "N") + "\n";
        details += "state: " + mState + "\n \n";

        return details;
    }
}
