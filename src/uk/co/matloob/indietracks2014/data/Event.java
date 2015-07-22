package uk.co.matloob.indietracks2014.data;

import org.json.JSONException;
import org.json.JSONObject;
import uk.co.matloob.indietracks2014.IndietracksApplication;
import uk.co.matloob.indietracks2014.alarm.EventAlarmManager;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: maq
 * Date: 10/07/2013
 * Time: 23:33
 * To change this template use File | Settings | File Templates.
 */
public class Event implements Serializable, Comparable<Event>{
    public String location;
    public Calendar start;
    public Calendar end;
    public String key;
    public String cost;
    public Artist artist;
    public static final SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Event(JSONObject data, Artist artist) throws JSONException, ParseException {
        this.artist = artist;
        location = data.getString("location");
        if (data.has("cost"))
            cost = data.getString("cost");
        Date startDate = dayFormat.parse(data.getString("start"));
        Date endDate = dayFormat.parse(data.getString("end"));

        start = new GregorianCalendar(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
        start.setTime(startDate);
        end = new GregorianCalendar(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
        end.setTime(endDate);

        key = String.format("%s|%s|%s|%s", EventAlarmManager.ALARMKEY_PREFIX, artist.displayName, location, dayFormat.format(start.getTime()));
    }

    @Override
    public boolean equals(Object o) {
        return this.start.equals(((Event) o).start);
    }

    @Override
    public int compareTo(Event event) {
        return start.compareTo(event.start);
    }
}
