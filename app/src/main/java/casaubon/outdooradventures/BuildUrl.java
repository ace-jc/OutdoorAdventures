package casaubon.outdooradventures;

/**
 * Created by j on 7/12/16.
 */
public class BuildUrl {

    // debug tag
    private static final String TAG = "Outdoor Adventures";

    // to build url
    private static final String urlStart = "http://api.amp.active.com/camping/campgrounds/?";
    private static final String apiKey = "api_key=a67c926wtb9qtwu4vnhyev53";

    // building URL here
    private String actualURL;

    // variables
    private String state;
    private String parkActivity;


    public void addState(String selectedState) {
        // save state
        state = selectedState;
        actualURL = urlStart;
        actualURL += selectedState;
    }

    public void addParkActivity() {
        
    }



}
