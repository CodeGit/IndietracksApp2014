package uk.co.matloob.indietracks2014.schedule;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragment;
import uk.co.matloob.indietracks2014.IndietracksApplication;
import uk.co.matloob.indietracks2014.IndietracksMainAcvitity;
import uk.co.matloob.indietracks2014.R;
import uk.co.matloob.indietracks2014.artist.OnArtistSelected;
import uk.co.matloob.indietracks2014.data.DataBundle;
import uk.co.matloob.indietracks2014.data.Event;
import uk.co.matloob.indietracks2014.event.EventListAdaptor;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: maq
 * Date: 16/07/2013
 * Time: 18:37
 * To change this template use File | Settings | File Templates.
 */
public class ScheduleFragment extends SherlockFragment {
    private static final String TAG = "ScheduleFragment";

    private static final String SELECTED_TAB_TAG = "selected_tab";
    private static final String SELECTED_LOCATION = "selected_location";

    SimpleDateFormat dateFormat=new SimpleDateFormat("EEEE", Locale.UK);
    OnArtistSelected callBack;
    IndietracksApplication application;
    IndietracksMainAcvitity parentActivity;
    DataBundle data;
    String openTab;
    String openLocation;

    TabHost host;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        parentActivity = (IndietracksMainAcvitity) getActivity();
        application = (IndietracksApplication) parentActivity.getApplication();
        data = application.getData();

        //TODO instance state not being saved correctly MAQ
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_TAB_TAG))
                openTab = savedInstanceState.getString(SELECTED_TAB_TAG);
            if (savedInstanceState.containsKey(SELECTED_LOCATION))
                openLocation = savedInstanceState.getString(SELECTED_LOCATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");

        View scheduleView = inflater.inflate(R.layout.schedule, container, false);
        host = (TabHost) scheduleView.findViewById(R.id.tabHost);
        host.setup();
        for (final Calendar day : data.days) {
            String dayName = dateFormat.format(day.getTime());
            TabHost.TabSpec spec = host.newTabSpec(dayName);
            spec.setIndicator(dayName);
            spec.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String tag) {
                    View view = null;
                    view = createDayView(day);
                    /*
                    if (cachedTabViews.containsKey(dayOfWeekNumber)) {
                        Log.d(TAG, "Re-using existing tab view for day = "
                                + dayOfWeekNumber);
                        view = cachedTabViews.get(dayOfWeekNumber);
                    }else {
                          view = createDayView(day);
                        cachedTabViews.put(dayOfWeekNumber, view);
                    }
                    */
                    return view;
                }
            });
            host.addTab(spec);
        }
        resetState();
        return scheduleView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState called saving tab state");
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        if (host != null) {
            outState.putString(SELECTED_TAB_TAG, host.getCurrentTabTag());
            Spinner locationSpinner = (Spinner) host.getCurrentView().findViewById(R.id.locationlist);
            outState.putString(SELECTED_LOCATION, (String) locationSpinner.getSelectedItem());
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onPause() {
        if (host != null) {
            openTab = host.getCurrentTabTag();
            Spinner locationSpinner = (Spinner) host.getCurrentView().findViewById(R.id.locationlist);
            openLocation =  (String) locationSpinner.getSelectedItem();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (host != null && openLocation != null && openTab != null)
            resetState();
    }

    void resetState() {
        if (openTab != null && openLocation != null) {
            host.setCurrentTabByTag(openTab);
            Spinner locationSpinner = (Spinner) host.getCurrentView().findViewById(R.id.locationlist);
            ArrayAdapter<String> locationAdaptor = (ArrayAdapter<String>) locationSpinner.getAdapter();
            int locationIndex = locationAdaptor.getPosition(openLocation);
            locationSpinner.setSelection(locationIndex);
        } else {
            Calendar today = GregorianCalendar.getInstance();
            today.setTimeZone(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
            String todayName = dateFormat.format(today.getTime());
            Log.d(TAG, "Today is " + todayName);
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            if (data.days.contains(today)) {
                host.setCurrentTabByTag(todayName);
            }
        }
    }

    View createDayView(final Calendar day){
        Log.d(TAG, "Creating Day View for " + day.get(Calendar.DAY_OF_WEEK));
        List<String> unsortedLocations = new ArrayList<String>();
        for (String location : data.dayLocationEvents.get(day).keySet()) {
            unsortedLocations.add(location);
        }
        List<String> sortedLocations = new ArrayList<String>();
        for (String location : data.locationOrder) {
            if (unsortedLocations.contains(location))
                sortedLocations.add(location);
        }
        final View dayTab = parentActivity.getLayoutInflater().inflate(R.layout.schedule_tab, null, false);

        Spinner locationsSpinner = (Spinner) dayTab.findViewById(R.id.locationlist);
        ArrayAdapter<String> locationAdaptor = new ArrayAdapter<String>(parentActivity, android.R.layout.simple_spinner_item, sortedLocations);
        locationAdaptor.setDropDownViewResource(android.R.layout.select_dialog_item);
        locationsSpinner.setAdapter(locationAdaptor);
        locationsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String location = (String) parent.getItemAtPosition(position);

                ListView eventList = (ListView) dayTab.findViewById(R.id.eventlist);
                List<Event> events = new ArrayList<Event>(data.dayLocationEvents.get(day).get(location));
                eventList.removeAllViewsInLayout();
                eventList.setAdapter(new EventListAdaptor(parentActivity,R.layout.event_row, events, false));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return dayTab;
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

