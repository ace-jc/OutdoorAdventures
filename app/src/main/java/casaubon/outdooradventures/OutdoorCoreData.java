package casaubon.outdooradventures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by alepena01 on 7/12/16.
 */
public class OutdoorCoreData {
    private static final String TAG = "OutdoorCoreData";
    private Context context;
    private static String baseUrl = "http://api.amp.active.com/camping/campgrounds/";
    private static final String apiKey = "&api_key=72fhbjk366qjzk5y5seuxyzv";
    private static ArrayList<OutdoorDetails> parkList = new ArrayList<OutdoorDetails>(50);
    private static String queryUrl = "";
    private final static String imageBaseUrl = "http://www.reserveamerica.com/webphotos/";
    private final int REQUIRED_SIZE = 256;
    private HttpURLConnection connParser;
    private HttpURLConnection connImage;
    InputStream parserInputStream;
    InputStream imageInputStream;
    private boolean running = true;

    public OutdoorCoreData(Context context, String urlString) {
        this.context = context;
        this.queryUrl = urlString;
    }

    public void setRunning(boolean isRun) {
        running = isRun;
    }
    //set the new query URL and clear previous parkList
    public void setURL(String url) {
        queryUrl = url;
    }

    //Clear saved parklist
    public void clearParkList() {
        parkList = new ArrayList<OutdoorDetails>(50);
    }

    public void cleanUp() {
        Log.d(TAG, "cleanUp");
        if (connParser != null) {
            connParser.disconnect();
        }

        if (connImage != null) {
            connImage.disconnect();
        }

        if (parserInputStream != null) {
            try {
                parserInputStream.close();
            }
            catch (IOException e) {}
        }

        if (imageInputStream != null) {
            try {
                imageInputStream.close();
            }
            catch (IOException e) {}
        }
    }

    // Method called to start search on current set queryUrl
    // return: arraylist of parks
    public ArrayList<OutdoorDetails> searchQuery(boolean includePictures) {
        clearParkList();
        try {
            URL url = new URL(queryUrl);
            connParser = (HttpURLConnection) url.openConnection();
            connParser.setRequestMethod("GET");
            connParser.setDoInput(true);
            connParser.connect();

            int responseCode = connParser.getResponseCode();
            Log.d(TAG, "response code: " + responseCode);

            if (responseCode == 200) {
                parserInputStream = connParser.getInputStream();
                XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser mParser = xmlFactoryObject.newPullParser();

                mParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                mParser.setInput(parserInputStream, null);

                parseXML(mParser, includePictures);
            }

        }

        catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
            cleanUp();
        }
        finally {
            cleanUp();
        }
        return parkList;
    }

    // Parses and builds the park list
    private void parseXML(XmlPullParser parser, boolean includePic) {
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
        boolean waterFront;
        String imageURL;

        String trueFlag = "Y";
        OutdoorDetails curObject;

        try {
            event = parser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT && running) {
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
                            waterFront = !((parser.getAttributeValue(null, "sitesWithWaterfront")).equals(""));
                            imageURL = imageBaseUrl + parser.getAttributeValue(null, "contractID") + "/pid" + idString + "/0/540x360.jpg";


                            Bitmap ThmbBitmap = null;
                            if (includePic) {
                                ThmbBitmap = getBitmap(imageURL);
                                if (ThmbBitmap == null) {
                                    Log.d(TAG, parkName + " has no image");
                                }
                            }
                            curObject = new OutdoorDetails(id, parkName, state, lat, lngt, ampOutlet, petsAllowed, sewerHookup,
                                    waterHookup, waterFront, ThmbBitmap);
                            parkList.add(curObject);


                        }
                }
                event = parser.next();

                if (!running) {
                    Log.d(TAG, "stopped running");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Size: " + parkList.size());
    }

    private Bitmap getBitmap(String url)
    {
        //from web
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            connImage = (HttpURLConnection)imageUrl.openConnection();
            connImage.setRequestMethod("GET");
            connImage.setDoInput(true);
            connImage.connect();

            if (connImage.getResponseCode() == 200) {
                Log.d(TAG, "successful connection");
                imageInputStream = connImage.getInputStream();
                bitmap = resizeImage(imageInputStream);
                imageInputStream.close();
            }
            else {
                Log.e(TAG, "no photo");
                Bitmap originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.nophoto);
                bitmap = Bitmap.createScaledBitmap(
                        originalBitmap, REQUIRED_SIZE, REQUIRED_SIZE, false);

            }
            connImage.disconnect();
            return bitmap;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap resizeImage(InputStream is){
        try {
            //decode image size
            //BitmapFactory.Options o = new BitmapFactory.Options();
            Bitmap originalBitmap = BitmapFactory.decodeStream(is);

            //Find the correct scale value. It should be the power of 2.

            int newWidth = originalBitmap.getWidth(), newHeight = originalBitmap.getHeight();
            int scale = 1;
            while(true){
                if (newWidth / 2 < REQUIRED_SIZE || newHeight / 2 < REQUIRED_SIZE)
                    break;
                newWidth/=2;
                newHeight/=2;
                scale*=2;
            }

            //decode with inSampleSize
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                    originalBitmap, REQUIRED_SIZE, REQUIRED_SIZE, false);

            return resizedBitmap;
        } catch (Exception e) {}
        return null;
    }

}