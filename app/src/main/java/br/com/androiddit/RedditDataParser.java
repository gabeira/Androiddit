package br.com.androiddit;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.androiddit.data.Page;
import br.com.androiddit.data.Reddit;

/**
 * Created by gabrielbernardopereira on 12/2/14.
 */
public class RedditDataParser {

    String after;

    public static Page parseReddit(String response) {

        Page page = new Page();

        List<Reddit> redditEntries = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray children = data.getJSONArray("children");

            for (int i = 0; i < children.length(); i++) {
                try {
                    JSONObject dataObj = children.getJSONObject(i).getJSONObject("data");
                    Reddit entry = new Reddit();
                    entry.setTitle(dataObj.getString("title"));
                    entry.setAuthor(dataObj.getString("author"));
                    entry.setThumbnail(dataObj.getString("thumbnail"));
                    entry.setCreated(dataObj.getLong("created_utc"));
                    entry.setNumberOfComments(dataObj.getInt("num_comments"));
                    redditEntries.add(entry);
                }catch (Exception e){
                    Log.e("REDDIT",
                            ">>> Error => " + e.getLocalizedMessage());
                }
            }
page.setAfter(data.getString("after"));
            Log.i("REDDIT", ">>>>Entries size:" + redditEntries.size()+" after:"+data.getString("after"));
page.setReddits(redditEntries);

        } catch (JSONException e) {
            Log.e("REDDIT",
                    "Error => " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return page;
    }

}
