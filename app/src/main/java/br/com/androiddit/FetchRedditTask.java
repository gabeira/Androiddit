package br.com.androiddit;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.androiddit.data.Page;
import br.com.androiddit.interfaces.TaskFinishListener;

/**
 * Created by gabrielbernardopereira on 12/9/14.
 */
public class FetchRedditTask extends AsyncTask<String, Void, Page> {

    private final String LOG_TAG = FetchRedditTask.class.getSimpleName();
    private TaskFinishListener listener;

    public void setListener(TaskFinishListener listener) {
        this.listener = listener;
    }

    @Override
    protected Page doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String after = "";
        if (null != params && params.length >=1 && params[0] != null) {
            after = "&after=" + params[0];
        }

        try {
            URL url = new URL("http://www.reddit.com/r/android/top/.json?sort=new" + after + "&count=10&limit=10");
            // Create the request to Reddit, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            return RedditDataParser.parseReddit(buffer.toString());

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Page reddits) {
        super.onPostExecute(reddits);
        if (null != reddits && !reddits.getReddits().isEmpty()) {
            if (listener != null) {
                listener.onFinish(reddits);
            }

        }
    }
}
