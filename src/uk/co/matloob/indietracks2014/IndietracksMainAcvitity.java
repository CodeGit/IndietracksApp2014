package uk.co.matloob.indietracks2014;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import uk.co.matloob.indietracks2014.artist.ArtistFragment;
import uk.co.matloob.indietracks2014.artist.ArtistListFragment;
import uk.co.matloob.indietracks2014.artist.OnArtistSelected;
import uk.co.matloob.indietracks2014.data.Event;
import uk.co.matloob.indietracks2014.event.TimelineFragment;
import uk.co.matloob.indietracks2014.schedule.ScheduleFragment;


/**
 * Created with IntelliJ IDEA.
 * User: maq
 * Date: 13/07/2013
 * Time: 12:36
 * To change this template use File | Settings | File Templates.
 */
public class IndietracksMainAcvitity extends SherlockFragmentActivity implements OnArtistSelected {
    private static final String TAG = "IndietracksMainAcvitity";
    static final String ARTISTLIST_FRAGMENT = "artislistFragment";
    static final String SCHEDULE_FRAGMENT = "scheduleFragment";
    static final String TIMELINE_FRAGMENT = "timelineFragment";
    static final String ARTIST_FRAGMENT = "artistFragment";
    public static final String EVENTKEY = "eventAlarmKey";

    private static Intent intent = new Intent();

    boolean isTwoFragmentLayout = false;
    ArtistListFragment artistListFragment;
    ScheduleFragment scheduleFragment;
    TimelineFragment timeLineFragment;
    ArtistFragment artistFragment;
    String currentFragmentTag;
    String currentArtistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        //MAQ - debugging only
        //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());

        setContentView(R.layout.main);

        if (findViewById(R.id.artistContent) != null) {
            isTwoFragmentLayout = true;
        }
        artistListFragment = (ArtistListFragment) getSupportFragmentManager().findFragmentByTag(ARTISTLIST_FRAGMENT);
        if (artistListFragment == null) {
            artistListFragment = new ArtistListFragment();
        }
        scheduleFragment = (ScheduleFragment) getSupportFragmentManager().findFragmentByTag(SCHEDULE_FRAGMENT);
        if (scheduleFragment == null) {
            scheduleFragment = new ScheduleFragment();
        }
        timeLineFragment = (TimelineFragment) getSupportFragmentManager().findFragmentByTag(TIMELINE_FRAGMENT);
        if (timeLineFragment == null) {
            timeLineFragment = new TimelineFragment();
        }
        artistFragment = (ArtistFragment) getSupportFragmentManager().findFragmentByTag(ARTIST_FRAGMENT);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            if (extras.containsKey(EVENTKEY)) {
                String eventKey = extras.getString(EVENTKEY);
                Event event = ((IndietracksApplication) getApplication()).getData().eventKeyMap.get(eventKey);
                Log.d(TAG, "Recieved intent that must have come from alarm for " + eventKey);
                displayArtist(event.artist.sortName);
                return;
            }
        }
        if (savedInstanceState == null)
            displaySchedule();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_artists:
                displayArtistList();
                return true;
            case R.id.action_schedule:
                displaySchedule();
                return true;
            case R.id.action_timeline:
                displayTimeline();
                return true;
            case R.id.action_info:
                displayInfo();
                return true;
            case android.R.id.home:
                intent.setAction("uk.co.matloob.indietracks2014.INDIETRACKS");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayArtist(String artistSortName) {
        currentArtistName = artistSortName;
        Bundle message = new Bundle();
        message.putString(ArtistFragment.ARTIST_NAME, artistSortName);
        artistFragment = new ArtistFragment();
        artistFragment.setArguments(message);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (isTwoFragmentLayout) {
            transaction.replace(R.id.artistContent, artistFragment, ARTIST_FRAGMENT);
            if (getSupportFragmentManager().findFragmentById(R.id.artistContent) != null)
                transaction.addToBackStack(null);
       } else {
            currentFragmentTag = ARTIST_FRAGMENT;
            transaction.replace(R.id.indietracksContent, artistFragment, ARTIST_FRAGMENT);
            if (getSupportFragmentManager().findFragmentById(R.id.indietracksContent) != null)
                transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void displayArtistList() {
        currentFragmentTag = ARTISTLIST_FRAGMENT;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.indietracksContent, artistListFragment, currentFragmentTag);
        if (getSupportFragmentManager().findFragmentById(R.id.indietracksContent) != null)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    public void displayTimeline() {
        currentFragmentTag = TIMELINE_FRAGMENT;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.indietracksContent, timeLineFragment, currentFragmentTag);
        if (getSupportFragmentManager().findFragmentById(R.id.indietracksContent) != null)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    public void displaySchedule() {
        currentFragmentTag = SCHEDULE_FRAGMENT;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.indietracksContent, scheduleFragment, currentFragmentTag);
        if (getSupportFragmentManager().findFragmentById(R.id.indietracksContent) != null)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    public void displayInfo() {
        intent.setAction("uk.co.matloob.indietracks2014.INFO");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onArtistSelected(String sortName) {
        displayArtist(sortName);
    }
}
