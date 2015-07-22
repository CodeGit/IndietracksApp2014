package uk.co.matloob.indietracks2014.data;


import uk.co.matloob.indietracks2014.IndietracksApplication;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: maq
 * Date: 11/07/2013
 * Time: 00:01
 * To change this template use File | Settings | File Templates.
 */
public class DataBundle implements Serializable{
    private final static String TAG = "DataBundle";
    public List<Artist> artists;
    public List<Event> events;
    public SortedSet<Calendar> days;
    //utility collections for fast lookup
    public Map<String, Artist> artistNameMap;
    public Map<String,Event> eventKeyMap;
    public List<String> locationOrder;
    public SortedMap<Calendar, List<Event>> dayEvents;
    public SortedMap<Calendar, Map<String, SortedSet<Event>>> dayLocationEvents;

    public DataBundle() {
        locationOrder = new ArrayList<String>();
        locationOrder.add("Outdoor stage");
        locationOrder.add("Indoor stage");
        locationOrder.add("Church stage");
        locationOrder.add("Train stage");
        locationOrder.add("Workshops");
        locationOrder.add("Marquee");
        locationOrder.add("Campsite");

        artists = new ArrayList<Artist>();
        events = new ArrayList<Event>();
        artistNameMap = new HashMap<String, Artist>();
        eventKeyMap = new HashMap<String, Event>();
        days = new TreeSet<Calendar>();
        dayEvents = new TreeMap<Calendar, List<Event>>();
        dayLocationEvents = new TreeMap<Calendar, Map<String, SortedSet<Event>>>();
    }

    public void addArtist(Artist artist) {
        //Log.d(TAG, "Adding artist " + artist.displayName);
        artists.add(artist);
        artistNameMap.put(artist.sortName, artist);
    }

    public void addEvent(Event event) {
        //Log.d(TAG, "Adding event " + event.key);
        events.add(event);
        eventKeyMap.put(event.key, event);
        Calendar day = new GregorianCalendar(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
        day.set(event.start.get(Calendar.YEAR),
                event.start.get(Calendar.MONTH),
                event.start.get(Calendar.DAY_OF_MONTH),
                0,0,0);
        //required to ensure days equal each other correctly
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);

        days.add(day);
        if (!dayEvents.containsKey(day)) {
            dayEvents.put(day, new ArrayList<Event>());
        }
        dayEvents.get(day).add(event);
        if (!dayLocationEvents.containsKey(day)) {
            dayLocationEvents.put(day, new HashMap<String, SortedSet<Event>>());
        }
        if (!dayLocationEvents.get(day).containsKey(event.location)) {
            dayLocationEvents.get(day).put(event.location, new TreeSet<Event>());
        }
        dayLocationEvents.get(day).get(event.location).add(event);
    }

    public void sortData() {
        Collections.sort(artists);
        Collections.sort(events);
        for (Calendar day : dayEvents.keySet()) {
            Collections.sort(dayEvents.get(day));
        }
    }
}
