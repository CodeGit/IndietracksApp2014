package uk.co.matloob.indietracks2014.artist;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockListFragment;
import uk.co.matloob.indietracks2014.IndietracksApplication;
import uk.co.matloob.indietracks2014.IndietracksMainAcvitity;
import uk.co.matloob.indietracks2014.R;
import uk.co.matloob.indietracks2014.data.Artist;
import uk.co.matloob.indietracks2014.data.DataBundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: maq
 * Date: 14/07/2013
 * Time: 15:40
 * To change this template use File | Settings | File Templates.
 */
public class ArtistListFragment extends SherlockListFragment {
    private static final String TAG = "ArtistListFragment";
    private static final String ARTIST_POSITION_IN_LIST = "ArtistPositionInList";

    IndietracksApplication application;
    IndietracksMainAcvitity parentActivity;
    DataBundle data;

    int currentListPosition = -1;
    OnArtistSelected callBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = (IndietracksMainAcvitity) getSherlockActivity();
        application = (IndietracksApplication) parentActivity.getApplication();
        data = application.getData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        if (currentListPosition > 0) {
            outState.putInt(ARTIST_POSITION_IN_LIST, currentListPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        ListView listView = getListView();
        currentListPosition = listView.getSelectedItemPosition();
        super.onPause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        List<String> namesList = new ArrayList<String>(
                data.artists.size());
        for (Artist artist : data.artists) {
            namesList.add(artist.displayName);
        }
        //ListView artistList = new ListView(parentActivity);
        setListAdapter(new ArrayAdapter<String>(parentActivity,
                R.layout.artistlist_row, R.id.artistsRowLabel, namesList));


        // enable text filtering
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(parentActivity.getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
                ((OnArtistSelected) parentActivity).onArtistSelected(data.artists.get(position).sortName);
            }

        });
        if (savedInstanceState != null && savedInstanceState.containsKey(ARTIST_POSITION_IN_LIST)) {
            currentListPosition = savedInstanceState.getInt(ARTIST_POSITION_IN_LIST);
            listView.setSelection(currentListPosition);
        }

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
