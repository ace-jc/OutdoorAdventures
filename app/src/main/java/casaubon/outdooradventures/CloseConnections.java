package casaubon.outdooradventures;

import android.os.AsyncTask;

/**
 * Created by alepena01 on 8/9/16.
 */
public class CloseConnections extends AsyncTask<OutdoorCoreData, Void, Void> {

    @Override
    protected Void doInBackground(OutdoorCoreData ... params) {
        OutdoorCoreData coreData = params[0];
        if (coreData != null) {
            coreData.setRunning(false);
            coreData.cleanUp();
        }

        return null;
    }
}
