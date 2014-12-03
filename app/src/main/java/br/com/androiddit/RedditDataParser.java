package br.com.androiddit;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by gabrielbernardopereira on 12/2/14.
 */
public class RedditDataParser {


    public static List<RedditEntry> parseReddit(String response) {

        List<RedditEntry> redditEntries = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray children = data.getJSONArray("children");

            for (int i = 0; i < children.length(); i++) {
                JSONObject dataObj = children.getJSONObject(i).getJSONObject("data");
                RedditEntry entry = new RedditEntry();
                entry.setTitle(dataObj.getString("title"));
                entry.setAuthor(dataObj.getString("author"));
                entry.setThumbnail(dataObj.getString("thumbnail"));
                entry.setCreationDate(dataObj.getLong("created_utc"));
                entry.setNumberOfComments(dataObj.getInt("num_comments"));
                redditEntries.add(entry);
            }
            Log.i("REDDIT", "Entries size:" + redditEntries.size());


        } catch (JSONException e) {
            Log.e("REDDIT",
                    "Error => " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return redditEntries;
    }
}
