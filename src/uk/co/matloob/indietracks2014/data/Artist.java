package uk.co.matloob.indietracks2014.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: maq
 * Date: 10/07/2013
 * Time: 23:33
 * To change this template use File | Settings | File Templates.
 */
public class Artist implements Serializable, Comparable<Artist> {
    public String sortName;
    public String displayName;
    public String description;
    public String image;
    public String link;
    public Event event;

    public Artist(JSONObject data) throws JSONException {
        sortName = data.getString("key");
        displayName = data.getString("name");
        description = data.getString("description");
        description = description.replaceAll("\\\\n", "\n");

        if (data.has("image"))
            image = data.getString("image");
        if (data.has("link"))
            link = data.getString("link");

    }

    @Override
    public boolean equals(Object o) {
        return this.sortName.equals(((Artist) o).sortName);
    }

    @Override
    public int compareTo(Artist artist) {
        return sortName.compareTo(artist.sortName);
    }
}
