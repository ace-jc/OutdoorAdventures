package casaubon.outdooradventures;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by alepena01 on 7/12/16.
 */
public class OutdoorCoreData {
    private static final String TAG = "OutdoorCoreData";
    private static String baseUrl = "http://api.amp.active.com/camping/campgrounds/";
    private static final String apiKey = "&api_key=72fhbjk366qjzk5y5seuxyzv";
    private static ArrayList<OutdoorDetails> parkList = new ArrayList<OutdoorDetails>(50);
    private static String queryUrl = "";

    public OutdoorCoreData(String urlString) {
        this.queryUrl = urlString;
    }

    //set the new query URL and clear previous parkList
    public void setURL(String url) {
        queryUrl = url;
    }

    //Clear saved parklist
    public static void clearParkList() {
        parkList = new ArrayList<OutdoorDetails>(50);
    }

    // Method called to start search on current set queryUrl
    // return: arraylist of parks
    public ArrayList<OutdoorDetails> searchQuery() {
        clearParkList();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(queryUrl);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    //conn.setReadTimeout(10000);
                    //conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream inputStream = conn.getInputStream();
                    XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser mParser = xmlFactoryObject.newPullParser();

                    mParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    mParser.setInput(inputStream, null);

                    parseXML(mParser);
                    inputStream.close();
                }

                catch (Exception e) {
                    Log.e(TAG, e.toString());
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        return parkList;
    }

    // Method called to start search on provided urlString
    // return: arraylist of parks
    public static ArrayList<OutdoorDetails> searchQuery(final String urlString) {
        clearParkList();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    //conn.setReadTimeout(10000);
                    //conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    Log.d(TAG, "response code: " + responseCode);

                    if (responseCode == 200) {
                        InputStream inputStream = conn.getInputStream();
                        XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                        XmlPullParser mParser = xmlFactoryObject.newPullParser();

                        mParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                        mParser.setInput(inputStream, null);

                        parseXML(mParser);
                        inputStream.close();
                    }
                    else {
                        Log.d(TAG, "BAD URL");
                        //handle bad url here
                    }
                }

                catch (Exception e) {
                    Log.e(TAG, e.toString());
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        return parkList;
    }

    // Parses and builds the park list
    private static void parseXML(XmlPullParser parser) {
        int event;
        int id;
        String idString;
        String parkName;
        String state;
        String latString;
        String lngtString;
        float lat;
        float lngt;
        boolean ampOutlet;
        boolean petsAllowed;
        boolean sewerHookup;
        boolean waterHookup;
        String trueFlag = "Y";
        OutdoorDetails curObject;

        try {
            event = parser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("result")) {
                            idString = parser.getAttributeValue(null, "facilityID");
                            id = Integer.parseInt((idString.length() != 0) ? idString : "-1");
                            parkName = parser.getAttributeValue(null, "facilityName");
                            latString = parser.getAttributeValue(null, "latitude");
                            lat = Float.parseFloat((latString.length() != 0) ? latString : "0");
                            lngtString = parser.getAttributeValue(null, "longitude");
                            lngt = Float.parseFloat((lngtString.length() != 0) ? lngtString : "0");
                            ampOutlet = trueFlag.equals(parser.getAttributeValue(null, "sitesWithAmps"));
                            petsAllowed = trueFlag.equals(parser.getAttributeValue(null, "sitesWithPetsAllowed"));
                            sewerHookup = trueFlag.equals(parser.getAttributeValue(null, "sitesWithSewerHookup"));
                            waterHookup = trueFlag.equals(parser.getAttributeValue(null, "sitesWithWaterHookup"));
                            state = parser.getAttributeValue(null, "state");
                            curObject = new OutdoorDetails(id, parkName, state, lat, lngt, ampOutlet, petsAllowed, sewerHookup,
                                    waterHookup);
                            Log.d(TAG, curObject.toString());
                            parkList.add(curObject);
                        }
                }
                event = parser.next();
            }

        }

        catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Size: " + parkList.size());
    }
}
