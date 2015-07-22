package uk.co.matloob.indietracks2014.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import uk.co.matloob.indietracks2014.IndietracksApplication;
import uk.co.matloob.indietracks2014.R;
import uk.co.matloob.indietracks2014.settings.SettingsActivity;
import uk.co.matloob.indietracks2014.data.DataBundle;
import uk.co.matloob.indietracks2014.data.Event;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class EventAlarmManager {
	public static final String TAG = "EventAlarmManager";
    public static final String DEFAULT_ADVANCE = "5";
    public static final String ALARMKEY_PREFIX = "EVENT_ALARM";

	public static void addAlarm(Context context, Event event,
			SharedPreferences prefs) {
		Log.d(TAG, "Adding Alarm " + event.key);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(event.key, true);
        editor.commit();

        String advanceString = prefs.getString(SettingsActivity.ALARMADVANCE, DEFAULT_ADVANCE);
		Log.d(TAG, "Predelay = " + advanceString);
        int advance = Integer.parseInt(advanceString);
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
		cal.add(Calendar.MINUTE, advance);
		if (cal.before(event.start)) {
			Intent intent = new Intent(context, EventAlarmReceiver.class);
			intent.setAction("uk.co.matloob.indietracks2014.EVENTALARM");
			intent.setData(Uri.parse("timer:" + event.key.hashCode()));
			intent.putExtra(IndietracksApplication.ALARMEVENT_KEY,
					event.key);
			PendingIntent pending = PendingIntent.getBroadcast(context,
					event.artist.sortName.hashCode(), intent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			cal.set(event.start.get(Calendar.YEAR),
					event.start.get(Calendar.MONTH),
					event.start.get(Calendar.DATE),
					event.start.get(Calendar.HOUR_OF_DAY),
					event.start.get(Calendar.MINUTE));
			cal.add(Calendar.MINUTE, -advance);
			Log.d(TAG, "Setting alarm for " + event.key
					+ " to go off at " + String.format("%tF %tR", cal, cal));
			Log.d(TAG, "The time now is " + System.currentTimeMillis()
					+ " the alarm is set for " + cal.getTimeInMillis());
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);
			// MAQ - debugging alarm
			//alarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() - 10000L), pending);
            //Log.d(TAG, "The time now is " + System.currentTimeMillis() + " the alarm is set for " + (System.currentTimeMillis() - 10000L));

        }
	}

	public static void removeAlarm(Context context, Event event,
			SharedPreferences prefs) {
		Log.d(TAG, "Removing Alarm " + event.key);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(event.key);
        editor.commit();

        String advanceString = prefs.getString(SettingsActivity.ALARMADVANCE, DEFAULT_ADVANCE);
        int advance = Integer.parseInt(advanceString);
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
		cal.add(Calendar.MINUTE, advance);
		if (cal.before(event.start)) {
			Intent intent = new Intent(context, EventAlarmReceiver.class);
			intent.setAction("uk.co.matloob.indietracks2014.EVENTALARM");
			intent.setData(Uri.parse("timer:" + event.key.hashCode()));
			intent.putExtra(IndietracksApplication.ALARMEVENT_KEY, event.key);
			PendingIntent pending = PendingIntent.getBroadcast(context, event.key.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
			cal.set(event.start.get(Calendar.YEAR),
					event.start.get(Calendar.MONTH),
					event.start.get(Calendar.DATE),
					event.start.get(Calendar.HOUR_OF_DAY),
					event.start.get(Calendar.MINUTE));
			cal.add(Calendar.MINUTE, -advance);
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(pending);
		}
	}

    public static void registerAlarms(Context context, SharedPreferences prefs) {
		Log.d(TAG, "Re-registering alarms");
		IndietracksApplication application = (IndietracksApplication) context
				.getApplicationContext();
		if (application.getData() == null) {
			try {
				application.loadState();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		Map alarms = prefs.getAll();
        DataBundle data = application.getData();
		for (Iterator<String> it = alarms.keySet().iterator(); it.hasNext();) {
			String eventKey = it.next();
            Log.d(TAG, "Event Key: " + eventKey);
			if(eventKey.startsWith(ALARMKEY_PREFIX)) {
                if (prefs.getBoolean(eventKey, false)) {
                    Log.d(TAG, " application contains " + eventKey + " => "
                            + data.eventKeyMap.containsKey(eventKey));
                    if (data != null
                            && data.eventKeyMap.containsKey(eventKey)) {
                        Event event = data.eventKeyMap.get(eventKey);
                        addAlarm(context, event, prefs);
                    }
                }
            }
		}
	}

    public static boolean eventHasAlarm(Event event, SharedPreferences prefs) {
        boolean hasAlarm = prefs.getBoolean(event.key, false);
        return hasAlarm;
    }

    public static Drawable getIcon(Event event, SharedPreferences prefs, Context context) {
        Drawable icon;
        if (prefs.getBoolean(event.key, false))
            icon = context.getResources().getDrawable(R.drawable.access_alarms);
        else
            icon = context.getResources().getDrawable(R.drawable.add_alarm);
        return icon;
    }
}
