package uk.co.matloob.indietracks2014.event;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragment;
import uk.co.matloob.indietracks2014.IndietracksApplication;
import uk.co.matloob.indietracks2014.IndietracksMainAcvitity;
import uk.co.matloob.indietracks2014.R;
import uk.co.matloob.indietracks2014.artist.OnArtistSelected;
import uk.co.matloob.indietracks2014.data.DataBundle;
import uk.co.matloob.indietracks2014.data.Event;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: maq
 * Date: 17/07/2013
 * Time: 10:32
 * To change this template use File | Settings | File Templates.
 */
public class TimelineFragment extends SherlockFragment {
    private final static String TAG="TimelineFragment";
    private static final String EVENT_POSITION_IN_LIST = "EventPositionInList";

    static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    IndietracksApplication application;
    IndietracksMainAcvitity parentActivity;
    DataBundle data;

    OnArtistSelected callBack;
    List<Event> pendingEvents = new ArrayList<Event>();
    EventListAdaptor adapter;

    int currentListPosition = -1;
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        parentActivity = (IndietracksMainAcvitity) getSherlockActivity();
        application = (IndietracksApplication) parentActivity.getApplication();
        data = application.getData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        if (listView != null) {
            currentListPosition = listView.getFirstVisiblePosition();
            Log.d(TAG, "Current position in list is " + currentListPosition);
            if (currentListPosition > 0) {
                outState.putInt( EVENT_POSITION_IN_LIST, currentListPosition);
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        getPendingEvents();
        FrameLayout timelineView = (FrameLayout) inflater.inflate(R.layout.timeline, container, false);
        listView = new ListView(parentActivity);
        adapter = new EventListAdaptor(parentActivity, R.layout.timeline_row, pendingEvents, true);
        listView.setAdapter(adapter);
        timelineView.addView(listView);
        if (savedInstanceState != null && savedInstanceState.containsKey(EVENT_POSITION_IN_LIST)) {
            currentListPosition = savedInstanceState.getInt(EVENT_POSITION_IN_LIST);
            if (currentListPosition < pendingEvents.size())
                listView.setSelection(currentListPosition);
        }
        return timelineView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        getPendingEvents();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    void getPendingEvents() {
        Calendar now = GregorianCalendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
        pendingEvents.clear();
        // not the most efficient method - it might be worth considering looping
        // through days
        // and added whole days at a time and then looping through individual
        // events on current day

        for (ListIterator<Event> it = data.events
                .listIterator(data.events.size()); it.hasPrevious();) {
            Event event = it.previous();
            if (now.before(event.end)) {
                pendingEvents.add(event);
            } else {
                Log.d(TAG,
                        "Skipping all events before "+ timeFormat.format(event.end.getTime()));
                break;
            }
        }
        Collections.sort(pendingEvents);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callBack = (OnArtistSelected) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArtistSelected");
        }
    }
}
