package uk.co.matloob.indietracks2014.artist;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import uk.co.matloob.indietracks2014.IndietracksApplication;
import uk.co.matloob.indietracks2014.IndietracksMainAcvitity;
import uk.co.matloob.indietracks2014.R;
import uk.co.matloob.indietracks2014.settings.SettingsActivity;
import uk.co.matloob.indietracks2014.alarm.EventAlarmManager;
import uk.co.matloob.indietracks2014.data.Artist;
import uk.co.matloob.indietracks2014.data.DataBundle;
import uk.co.matloob.indietracks2014.data.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: maq
 * Date: 14/07/2013
 * Time: 20:56
 * To change this template use File | Settings | File Templates.
 */
public class ArtistFragment extends SherlockFragment{
    private static final String TAG = "ArtistFragment";

    public static final String ARTIST_NAME = "ArtistNameField";
    static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    static SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");

    IndietracksApplication application;
    IndietracksMainAcvitity parentActivity;
    DataBundle data;
    Artist artist;
    View artistView;

    int x = android.R.color.background_light;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        parentActivity = (IndietracksMainAcvitity) getActivity();
        application = (IndietracksApplication) parentActivity.getApplication();
        data = application.getData();
        if (savedInstanceState != null && savedInstanceState.containsKey(ARTIST_NAME)) {
            String artistName = savedInstanceState.getString(ARTIST_NAME);
            artist = data.artistNameMap.get(artistName);
        }
        setupArtistFields();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        if (artist != null) {
            outState.putString(ARTIST_NAME, artist.sortName);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        artistView = inflater.inflate(R.layout.artist, container, false);
        return artistView;
    }

    public void setupArtistFields() {
        Log.d(TAG, "setupArtistFields");
        Bundle message = getArguments();
        if (message != null && message.containsKey(ARTIST_NAME)) {
            String artistName = message.getString(ARTIST_NAME);
            artist = data.artistNameMap.get(artistName);
        }

        String descText = "";
        String titleText = "";
        if (artist != null) {
            titleText = artist.displayName;
            if (artist.description != null) {
                descText = artist.description;
            } else {
                descText = "";
            }
            if (artist.link != null) {
                descText += "\n";
                descText += "\n " + artist.link + "\n";
            }

            if (artist.image != null) {
                ImageView photoView = (ImageView) artistView.findViewById(R.id.photo);
                int resource = getResources().getIdentifier(artist.image,
                        "drawable", parentActivity.getPackageName());
                photoView.setImageResource(resource);
            }
        }

        TextView title = (TextView) artistView.findViewById(R.id.displayname);

        title.setText(titleText);
        TextView description = (TextView) artistView.findViewById(R.id.description);
        description.setMovementMethod(LinkMovementMethod.getInstance());
        Linkify.addLinks(description, Linkify.WEB_URLS);
        description.setText(descText);

        if (artist != null) {
            final Event event = artist.event;
            if (event != null) {
                final SharedPreferences prefs = parentActivity.getSharedPreferences(IndietracksApplication.INDIETRACKS_PREFERENCES, Context.MODE_PRIVATE);
                TextView detailsView = (TextView) artistView.findViewById(R.id.event_details);
                String eventStart = String.format("%s - %s", timeFormat.format(event.start.getTime()), timeFormat.format(event.end.getTime()));
                String eventDay = dayFormat.format(event.start.getTime());
                detailsView.setText(eventDay + " " + eventStart + " " + event.location);
                final ImageView imageView = (ImageView) artistView.findViewById(R.id.alarm_icon);
                Drawable icon = EventAlarmManager.getIcon(event, prefs, parentActivity);
                imageView.setImageDrawable(icon);

                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String eventKey = event.key;
                        String message;
                        if (prefs.getBoolean(eventKey, false)) {
                            message = String.format("Removing alarm for %1s", artist.displayName);
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
                                        "Setting alarm for %1s %s", artist.displayName, timeFormat.format(cal.getTime()));
                                EventAlarmManager
                                        .addAlarm(v.getContext(), event, prefs);
                            } else {
                                message = String.format("%1s %s is in the past", artist.displayName, timeFormat.format(cal.getTime()));
                            }
                        }
                        Drawable icon = EventAlarmManager.getIcon(event, prefs, parentActivity);
                        imageView.setImageDrawable(icon);
                        Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();

                    }
                };
                View detailsContainer = artistView.findViewById(R.id.event_container);
                detailsContainer.setOnClickListener(clickListener);
            }
            artistView.refreshDrawableState();
        } else {
            TextView textView = (TextView) artistView.findViewById(R.id.eventHeader);
            textView.setText("");
        }
    }
}
