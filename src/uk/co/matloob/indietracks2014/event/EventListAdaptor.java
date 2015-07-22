package uk.co.matloob.indietracks2014.event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import uk.co.matloob.indietracks2014.IndietracksApplication;
import uk.co.matloob.indietracks2014.R;
import uk.co.matloob.indietracks2014.settings.SettingsActivity;
import uk.co.matloob.indietracks2014.alarm.EventAlarmManager;
import uk.co.matloob.indietracks2014.artist.OnArtistSelected;
import uk.co.matloob.indietracks2014.data.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class EventListAdaptor extends ArrayAdapter {

    private final static String TAG = "EventListAdaptor";
    List<Event> events;
    Activity activity;
    int resource;
    boolean showLocation;
    static Intent intent = new Intent();
    static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    static SimpleDateFormat threeletterDayFormat = new SimpleDateFormat("EEE");

    public EventListAdaptor(Activity activity, int resource,
                            List<Event> events, boolean showLocation) {
        super(activity, resource, events);
        this.events = events;
        this.activity = activity;
        this.resource = resource;
        this.showLocation = showLocation;
        setNotifyOnChange(true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        final View row = inflater.inflate(resource, parent, false);
        final Event event = events.get(position);
        final SharedPreferences prefs = parent.getContext().getSharedPreferences(IndietracksApplication.INDIETRACKS_PREFERENCES, Context.MODE_PRIVATE);
        final ImageView imageView = (ImageView) row.findViewById(R.id.alarm_icon);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnArtistSelected) activity).onArtistSelected(event.artist.sortName);
            }
        };

        OnLongClickListener longClickListener = new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String eventKey = event.key;
                String message;
                if (prefs.getBoolean(eventKey, false)) {
                    message = String.format("Removing alarm for %1s", event.artist.displayName);
                    redrawColours(row, R.color.event);
                    EventAlarmManager.removeAlarm(v.getContext(), event, prefs);
                } else {
                    Calendar cal = new GregorianCalendar(
                            event.start.get(Calendar.YEAR),
                            event.start.get(Calendar.MONTH),
                            event.start.get(Calendar.DATE),
                            event.start.get(Calendar.HOUR_OF_DAY),
                            event.start.get(Calendar.MINUTE));
                    cal.setTimeZone(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
                    String advanceString = prefs.getString(SettingsActivity.ALARMADVANCE, EventAlarmManager.DEFAULT_ADVANCE);
                    int advance = Integer.parseInt(advanceString);
                    cal.add(Calendar.MINUTE, - advance);
                    Calendar now = GregorianCalendar.getInstance();
                    now.setTimeZone(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
                    if (now.before(cal)) {
                        message = String.format(
                                "Setting alarm for %1s %s", event.artist.displayName, timeFormat.format(cal.getTime()));
                        redrawColours(row, R.color.selectedevent);
                        EventAlarmManager
                                .addAlarm(v.getContext(), event, prefs);
                    } else {
                        message = String.format("%1s %2tA %3tR is in the past", event.artist.displayName, timeFormat.format(cal.getTime()));
                    }
                }
                Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT)
                        .show();
                return true;
            }
        };


        ImageView nowPlaying = (ImageView) row.findViewById(R.id.now_playing);
        if (nowPlaying != null) {
            Calendar now = GregorianCalendar.getInstance();
            now.setTimeZone(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
            if (now.after(event.start) && now.before(event.end)) {
                Drawable currentIcon = nowPlaying.getDrawable();
                Log.d(TAG, String.valueOf("Found current event " + event + " checking drawable = " + currentIcon != null));
                if (currentIcon == null) {
                    Log.d(TAG, "Setting new icon for " + event.artist.displayName);
                    nowPlaying.setImageDrawable(getContext().getResources().getDrawable(R.drawable.device_access_volume_on));
                    nowPlaying.invalidate();
                }
            } else {
                Drawable currentIcon = nowPlaying.getDrawable();
                Log.d(TAG, String.valueOf("Not playing " + event + " checking drawable = " + currentIcon == null));
                if (currentIcon != null) {
                    Log.d(TAG, "Removing icon for " + event.artist.displayName);
                    nowPlaying.setImageDrawable(null);
                    nowPlaying.invalidate();
                }

            }

        }

        int colour;
        if (prefs.getBoolean(event.key, false)) {
            colour = R.color.selectedevent;
        } else {
            colour = R.color.event;
        }

        row.setOnLongClickListener(longClickListener);
        row.setLongClickable(true);

        TextView startView = (TextView) row.findViewById(R.id.event_time);
        if (startView != null) {


            String eventStart = String.format("%s - %s", timeFormat.format(event.start.getTime()), timeFormat.format(event.end.getTime()));
            startView.setPadding(5, 0, 5, 0);
            startView.setText(eventStart);
            startView.setOnClickListener(listener);
            startView.setOnLongClickListener(longClickListener);
        }

        TextView dayView = (TextView) row.findViewById(R.id.event_day);
        if (dayView != null) {
            String eventDay = threeletterDayFormat.format(event.start.getTime());
            dayView.setPadding(5, 0, 5, 0);
            dayView.setText(eventDay);
            dayView.setOnClickListener(listener);
            dayView.setOnLongClickListener(longClickListener);
        }
        TextView nameView = (TextView) row.findViewById(R.id.event_name);
        if (nameView != null) {
            nameView.setPadding(5, 0, 10, 0);
            if (showLocation) {
                nameView.setText(event.artist.displayName);
                TextView location = (TextView) row.findViewById(R.id.event_location);
                location.setText(event.location);
            }
            else
                nameView.setText(event.artist.displayName);
            nameView.setOnClickListener(listener);
            nameView.setOnLongClickListener(longClickListener);
        }

        row.setOnClickListener(listener);
        row.setOnLongClickListener(longClickListener);
        redrawColours(row, colour);
        return row;
    }

    public void redrawColours(View row, int colour) {
        ColorStateList colourStates = row.getResources().getColorStateList(
                colour);
        TextView startView = (TextView) row.findViewById(R.id.event_time);
        if (startView != null)
            startView.setTextColor(colourStates);
        TextView dayView = (TextView) row.findViewById(R.id.event_day);
        if (dayView != null)
            dayView.setTextColor(colourStates);
        TextView nameView = (TextView) row.findViewById(R.id.event_name);
        if (nameView != null)
            nameView.setTextColor(colourStates);
        row.refreshDrawableState();
    }

}
