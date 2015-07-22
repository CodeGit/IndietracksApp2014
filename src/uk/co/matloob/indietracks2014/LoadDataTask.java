package uk.co.matloob.indietracks2014;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;
import uk.co.matloob.indietracks2014.data.DataBundle;
import uk.co.matloob.indietracks2014.data.DataParser;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: maq
 * Date: 12/07/2013
 * Time: 21:02
 * To change this template use File | Settings | File Templates.
 */
public class LoadDataTask extends AsyncTask<Boolean, String, DataBundle> {

    private final static String TAG = "LoadDataTask";
    private final static String LOCAL_JSON_PATH = "data/indietracks.json";
    private final static String INTERNET_JSON_PATH = "http://www.roadsidepoppies.com/indietracks/indietracks.json";

    private Activity activity;
    private ProgressDialog progressDialog;
    private AssetManager assets;
    private ProgressDialog dialog;
    private String filename;
    private Context context;

    public LoadDataTask(Activity activity) {
        this.activity = activity;
        assets = activity.getAssets();
        filename = activity.getResources().getString(R.string.IndietracksAppData);
        context = activity.getBaseContext();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");
        if (!isCancelled()) {
            dialog = ProgressDialog.show(activity, "", "Preparing Indietracks 2013 Festival Guide", true);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }
        super.onPreExecute();
    }


    @Override
    protected void onPostExecute(DataBundle dataBundle) {
        Log.d(TAG, "onPostExecute");
        dialog.dismiss();
        ((IndietracksDataBundleReciever) activity).setDataBunder(dataBundle);
        super.onPostExecute(dataBundle);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        Log.d(TAG, "onProgressUpdate(" + values[0]+ ")");
        dialog.setMessage(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected DataBundle doInBackground(Boolean... fetchDataFromInternet) {
        Log.d(TAG, "doInBackground(" + fetchDataFromInternet[0] + ")");
        DataBundle data = null;
        JSONArray jsonData = null;
        DataParser parser = new DataParser();
        if (!isCancelled()) {
            if (fetchDataFromInternet[0]) {
                try {
                    publishProgress("Looking up URL");
                    URL jsonURL = new URL(INTERNET_JSON_PATH);
                    URLConnection connection = jsonURL.openConnection();
                    connection.connect();
                    publishProgress("Fetching data from URL");
                    BufferedInputStream stream = new BufferedInputStream(jsonURL.openStream());
                    jsonData = streamToJson(stream);
                    publishProgress("Collected data");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Fetching from internet failed. Loading local file: ");
                    try {
                        jsonData = loadFromFile();
                    } catch (Exception e1) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load data from file");
                    }
                }
            } else {
                try {
                    jsonData = loadFromFile();
                    Log.d(TAG, "Succesfully loaded data from file");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Failed to load data from file");
                }
            }
            if (jsonData != null) {
                try {
                    publishProgress("Preparing Indietracks 2013 festival data");
                    data = parser.load(jsonData);
                    if (data != null) {
                        publishProgress("Saving state");
                        saveDataBundle(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    publishProgress("Error: Somethign went wrong whilst structuring the data");
                }
            } else {
                publishProgress("Error: Something went wrong loading the festival data");
            }

        }
        return data;
    }

    JSONArray loadFromFile() throws IOException, JSONException {
        Log.d(TAG, "Attempting to load data from file");
        publishProgress("Reading data from file");
        InputStream stream = stream = assets.open(LOCAL_JSON_PATH);
        JSONArray jsonData = streamToJson(new BufferedInputStream(stream));
        publishProgress("Finished reading data");
        return jsonData;
    }

    JSONArray streamToJson(BufferedInputStream stream) throws JSONException {
        StringBuilder jsonBuilder = new StringBuilder();
        Scanner scanner = new Scanner(stream, "UTF-8");

        try {
            while (scanner.hasNextLine()) {
                String jsonLine = scanner.nextLine();
                jsonBuilder.append(jsonLine);
            }
        }
        finally {
            scanner.close();
        }
        JSONTokener tokener = new JSONTokener(jsonBuilder.toString());
        JSONArray jsonData = (JSONArray) tokener.nextValue();
        return jsonData;
    }

    public void saveDataBundle(DataBundle data) throws FileNotFoundException, IOException {
        Log.d(TAG, "Saving serialised objects");
        //TODO not sure if using the activity to open the file is risky
        FileOutputStream fileStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        ObjectOutputStream outStream = new ObjectOutputStream(fileStream);
        outStream.writeObject(data);
        fileStream.getFD().sync();//ensures immediate write to disk
        outStream.close();
    }
}
