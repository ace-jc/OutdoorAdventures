package casaubon.outdooradventures;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alepena01 on 7/12/16.
 */
public class OutdoorDetails implements Parcelable{
    private int mID;
    private String mName;
    private String mState;
    private float mLat;
    private float mLngt;
    private boolean mAmpOutlet;
    private boolean mPetsAllowed;
    private boolean mSewerHookup;
    private boolean mWaterHookup;
    private boolean mWaterFront;
    private String mMarkerID = "";
    double distance;

    public OutdoorDetails(int id, String name, String state, float latitude, float longitude,
                          boolean hasOutlet, boolean allowsPets, boolean hasSewerHU, boolean hasWaterHU, boolean waterFront) {
        mID = id;
        mName = (name.replace("&apos;", "'")).toUpperCase();
        mState = state;
        mLat = latitude;
        mLngt = longitude;
        // gacky fix for broken API data
        if(mName.equals("ARROWHEAD RV CAMPGROUND"))
            mLngt = (float)-89.848779;
        mAmpOutlet = hasOutlet;
        mPetsAllowed = allowsPets;
        mSewerHookup = hasSewerHU;
        mWaterHookup = hasWaterHU;
        distance = 0;
        mWaterFront= waterFront;
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

    public void setMarkerID(String markerID) {
        mMarkerID = markerID;
    }

    public String getMarkerID() {
        return mMarkerID;
    }

    public double getDistance() {return distance;};

    public void setDistance(double input){ distance = input;}

    public String amenitiesList(){
        String output = "";
        if(mPetsAllowed){
            output += "Pets allowed";
            if(mSewerHookup || mWaterHookup || mWaterFront || mAmpOutlet)
                output += ", ";
        }
        if(mSewerHookup) {
            output += "Sewer Hookup ";
            if(mWaterHookup || mWaterFront || mAmpOutlet)
                output += ", ";
        }
        if(mWaterHookup){
            output += "Water Hookup ";
            if(mWaterFront || mAmpOutlet)
                output += ", ";
        }
        if(mWaterFront){
            output += "WaterFront Sites ";
            if(mAmpOutlet)
                output += ", ";
        }
        if(mAmpOutlet){
            output += "Electric Hookup";
        }
        return output;
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
        details += "distance: " + distance + "\n";
        details += "state: " + mState + "\n \n";
        return details;
    }


    @Override
    public void writeToParcel(Parcel out, int flags) {
        // writing out when copying the object
        out.writeInt(mID);
        out.writeString(mName);
        out.writeString(mState);
        out.writeFloat(mLat);
        out.writeFloat(mLngt);
        out.writeInt(mAmpOutlet ? 1 : 0);  //mAmpOutlet
        out.writeInt(mPetsAllowed ? 1 : 0);  //mPetsAllowed
        out.writeInt(mSewerHookup ? 1 : 0);  //mSewerHookup
        out.writeInt(mWaterHookup ? 1 : 0);  //mWaterHookup
        out.writeInt(mWaterFront ? 1 : 0);  //mWaterFront
    }

    private OutdoorDetails(Parcel in) {
        // writing in when creating the object from another activity
        mID = in.readInt();
        mName = in.readString();
        mState = in.readString();
        mLat = in.readFloat();
        mLngt = in.readFloat();
        mAmpOutlet = (in.readInt()) == 1 ? true : false;
        mPetsAllowed = (in.readInt()) == 1 ? true : false;
        mSewerHookup = (in.readInt()) == 1 ? true : false;
        mWaterHookup = (in.readInt()) == 1 ? true : false;
        mWaterFront = (in.readInt()) == 1 ? true : false;
    }

    @Override
    // This is what the internet said... :O
    public int describeContents() {
        return 0;
    }


    public static final Parcelable.Creator<OutdoorDetails> CREATOR
            = new Parcelable.Creator<OutdoorDetails>() {

        @Override
        public OutdoorDetails createFromParcel(Parcel in) {
            return new OutdoorDetails(in);
        }

        @Override
        public OutdoorDetails[] newArray(int size) {
            return new OutdoorDetails[size];
        }
    };
}
