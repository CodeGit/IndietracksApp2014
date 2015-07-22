package uk.co.matloob.indietracks2014.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: maq
 * Date: 11/07/2013
 * Time: 00:01
 * To change this template use File | Settings | File Templates.
 */
public class DataParser {

    public DataBundle load(JSONArray jsonData) throws JSONException, ParseException {
        DataBundle data = new DataBundle();
        for (int i = 0; i < jsonData.length(); i++) {
            JSONObject artistJson = (JSONObject) jsonData.get(i);
            Artist artist = new Artist(artistJson);
            Event event = new Event(artistJson, artist);
            artist.event = event;
            data.addArtist(artist);
            data.addEvent(event);
        }
        data.sortData();
        return data;
    }
}
