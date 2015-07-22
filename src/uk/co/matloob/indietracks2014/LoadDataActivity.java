package uk.co.matloob.indietracks2014;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import uk.co.matloob.indietracks2014.data.DataBundle;

import java.io.IOException;

public class LoadDataActivity extends Activity implements IndietracksDataBundleReciever {
    private final static String TAG = "LoadDataActivity";

    private static final String DATAVERSION = "DataVersion";

    AsyncTask loadDataTask;
    IndietracksApplication application;
    SharedPreferences prefs;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        prefs = getSharedPreferences(IndietracksApplication.INDIETRACKS_PREFERENCES, MODE_PRIVATE);

        checkDataState();
    }

    public void checkDataState() {
        application = (IndietracksApplication) getApplication();
        int dataVersion = prefs.getInt(DATAVERSION, 0);
        if (application.data == null || dataVersion < 1) {
            loadData(application, false);
        } else {
            Intent intent = new Intent("uk.co.matloob.indietracks2014.INDIETRACKS");
            startActivity(intent);
        }
    }

    public void loadData(IndietracksApplication application, boolean loadFromInternet) {
        loadDataTask = new LoadDataTask(this).execute(new Boolean(loadFromInternet));
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        if (loadDataTask != null)
            loadDataTask.cancel(true);
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart");
        checkDataState();
        super.onPause();
    }

    @Override
    public void setDataBunder(DataBundle data) {
        try {
            application.setData(data);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(DATAVERSION, 1);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(("uk.co.matloob.indietracks2014.INDIETRACKS")));
    }
}
