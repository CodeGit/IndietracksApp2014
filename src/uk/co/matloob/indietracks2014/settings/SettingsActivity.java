package uk.co.matloob.indietracks2014.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import uk.co.matloob.indietracks2014.IndietracksApplication;
import uk.co.matloob.indietracks2014.R;
import uk.co.matloob.indietracks2014.alarm.EventAlarmManager;

/**
 * Created with IntelliJ IDEA.
 * User: maq
 * Date: 21/07/2013
 * Time: 13:35
 * To change this template use File | Settings | File Templates.
 */
public class SettingsActivity extends SherlockPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String TAG = "SettingsActivity";
    public static final String ALARMADVANCE = "pref_alarmAdvance";
    private static Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                intent.setAction("uk.co.matloob.indietracks2014.INDIETRACKS");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences tempPrefs, String s) {
        Log.d(TAG, "Updating alarm times");
        if (s.equals(SettingsActivity.ALARMADVANCE)) {
            SharedPreferences appPrefs = this.getSharedPreferences(IndietracksApplication.INDIETRACKS_PREFERENCES, Context.MODE_PRIVATE);

            Log.d(TAG, "New Pre-delay value = " + tempPrefs.getString(ALARMADVANCE, "no value") + " current value = " + appPrefs.getString(ALARMADVANCE, "no value"));

            SharedPreferences.Editor editor = appPrefs.edit();
            editor.putString(SettingsActivity.ALARMADVANCE, tempPrefs.getString(ALARMADVANCE, getString(R.string.default_predelay)));
            editor.commit();
            EventAlarmManager alarmManager = new EventAlarmManager();
            alarmManager.registerAlarms(this, appPrefs);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }
}
