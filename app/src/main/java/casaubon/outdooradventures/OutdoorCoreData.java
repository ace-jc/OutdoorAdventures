package casaubon.outdooradventures;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by alepena01 on 8/9/16.
 */
public abstract class OutdoorCoreData {

    private Context context;
    private static String queryUrl = "";
    private boolean queryPic;
    private final static String imageBaseUrl = "http://www.reserveamerica.com/webphotos/";
    private ArrayList<OutdoorDetails> parkList = new ArrayList<OutdoorDetails>(50);
    SharedPreferences sharedPref;
    private final static String TAG = "OutdoorData";
    private final int REQUIRED_SIZE = 256;
    protected abstract void updateView(ArrayList<OutdoorDetails> parks);
    private QuerySearch task;

    public OutdoorCoreData(Context context, String urlString, boolean includePicture) {
        this.context = context;
        this.queryUrl = urlString;
        queryPic = includePicture;
        sharedPref = context.getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE);
    }

    //
    public void startSearch() {
        task = new QuerySearch();
        task.execute();
    }

    public void stopSearch() {
        if (task != null) {
            task.cancel(true);
        }
    }

    private class QuerySearch extends AsyncTask<Void, Void, ArrayList<OutdoorDetails>> {

        XmlPullParser mParser;
        private HttpURLConnection connParser;
        private HttpURLConnection connImage;
        private InputStream parserInputStream;
        private InputStream imageInputStream;
        private ArrayList<OutdoorDetails> mParks = new ArrayList<OutdoorDetails>(50);

        @Override
        protected ArrayList<OutdoorDetails> doInBackground(Void... params) {
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

            //set up connection and query
            mParser = setConnection();

            //parse
            try {
                event = mParser.getEventType();

                while (!isCancelled() && event != XmlPullParser.END_DOCUMENT) {
                    String name = mParser.getName();

                    switch (event) {
                        case XmlPullParser.START_TAG:
                            break;
                        case XmlPullParser.TEXT:
                            break;
                        case XmlPullParser.END_TAG:
                            if (name.equals("result")) {
                                idString = mParser.getAttributeValue(null, "facilityID");
                                id = Integer.parseInt((idString.length() != 0) ? idString : "-1");
                                parkName = mParser.getAttributeValue(null, "facilityName");
                                latString = mParser.getAttributeValue(null, "latitude");
                                lat = Float.parseFloat((latString.length() != 0) ? latString : "0");
                                lngtString = mParser.getAttributeValue(null, "longitude");
                                lngt = Float.parseFloat((lngtString.length() != 0) ? lngtString : "0");
                                ampOutlet = trueFlag.equals(mParser.getAttributeValue(null, "sitesWithAmps"));
                                petsAllowed = trueFlag.equals(mParser.getAttributeValue(null, "sitesWithPetsAllowed"));
                                sewerHookup = trueFlag.equals(mParser.getAttributeValue(null, "sitesWithSewerHookup"));
                                waterHookup = trueFlag.equals(mParser.getAttributeValue(null, "sitesWithWaterHookup"));
                                state = mParser.getAttributeValue(null, "state");
                                waterFront = !((mParser.getAttributeValue(null, "sitesWithWaterfront")).equals(""));
                                imageURL = imageBaseUrl + mParser.getAttributeValue(null, "contractID") + "/pid" + idString + "/0/540x360.jpg";


                                Bitmap ThmbBitmap = null;
                                if (queryPic) {
                                    ThmbBitmap = getBitmap(imageURL);
                                    if (ThmbBitmap == null) {
                                        Log.d(TAG, parkName + " has no image");
                                    }
                                }
                                curObject = new OutdoorDetails(id, parkName, state, lat, lngt, ampOutlet, petsAllowed, sewerHookup,
                                        waterHookup, waterFront, ThmbBitmap);
                                mParks.add(curObject);
                            }
                    }
                    event = mParser.next();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "Size: " + mParks.size());

            return mParks;
        }

        @Override
        protected void onPostExecute(ArrayList<OutdoorDetails> items) {
            Log.d(TAG, "onPostExecute");
            parkList = items;
            Log.d(TAG, "list size BEFORE radius applied: " + parkList.size());
            ensureInRadius();
            Log.d(TAG, "list size AFTER radius applied: " + parkList.size());
            updateView(parkList);
            cleanUp();
        }

        public XmlPullParser setConnection() {

            XmlPullParser mParser = null;
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
                    mParser = xmlFactoryObject.newPullParser();

                    mParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    mParser.setInput(parserInputStream, null);
                }


            } catch (Exception e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
            }

            return mParser;
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

        private void ensureInRadius(){
            int sizeOfRadius = sharedPref.getInt("radius", -1);
            Log.d(TAG, "Size of radius: " + sizeOfRadius);
            Iterator<OutdoorDetails> it = parkList.iterator();
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
        private double distanceMeasure(double lat1, double long1, double lat2, double long2) {
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
    }
}
