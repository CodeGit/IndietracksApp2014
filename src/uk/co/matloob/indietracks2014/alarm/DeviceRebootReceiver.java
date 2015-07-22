package uk.co.matloob.indietracks2014.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import uk.co.matloob.indietracks2014.IndietracksApplication;

/**
 * Created with IntelliJ IDEA.
 * User: maq
 * Date: 20/07/2013
 * Time: 12:05
 * To change this template use File | Settings | File Templates.
 */
public class DeviceRebootReceiver extends BroadcastReceiver {
    public static final String TAG = "AlarmResetReciever";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive - re-registering alarms");
        SharedPreferences prefs = context.getSharedPreferences(IndietracksApplication.INDIETRACKS_PREFERENCES, Context.MODE_PRIVATE);
        EventAlarmManager.registerAlarms(context, prefs);
    }

}