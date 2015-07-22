package uk.co.matloob.indietracks2014;

import android.app.Application;
import android.util.Log;
import uk.co.matloob.indietracks2014.data.DataBundle;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: maq
 * Date: 12/07/2013
 * Time: 21:32
 * To change this template use File | Settings | File Templates.
 */
public class IndietracksApplication extends Application {
    public final static String TAG = "IndietracksApplication";

    public static final String ALARMEVENT_KEY = "eventKey";
    public static final String INDIETRACKS_PREFERENCES = "IndietracksPreferences";
    public static final String TIMEZONE = "Europe/London";

    DataBundle data;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        String filename = getResources().getString(R.string.IndietracksAppData);
        Log.d(TAG, "Looking for " + filename);
        File file = new File(getFilesDir(), filename);
        if (file.exists()) {
            try {
                loadState();
            } catch (IOException e) {
                e.printStackTrace();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else
            Log.d(TAG, "Saved state not found");
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public void loadState() throws FileNotFoundException, IOException,
            ClassNotFoundException {
        Log.d(TAG, "Loading serialised objects");
        String filename = getResources().getString(R.string.IndietracksAppData);
        FileInputStream fileStream = openFileInput(filename);
        ObjectInputStream inStream = new ObjectInputStream(fileStream);
        data = (DataBundle) inStream.readObject();
        inStream.close();
    }

    public DataBundle getData() {
        return data;
    }

    public void setData(DataBundle data) throws IOException {
        this.data = data;
    }

}
