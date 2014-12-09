package br.com.androiddit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import br.com.androiddit.data.Reddit;

public class MainActivity extends ActionBarActivity {

    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    private int mLastFirstVisibleItem;
    private boolean mIsScrollingUp;

    @Override
    protected void onStart() {
        super.onStart();
//        Toast.makeText(this,"onStart",Toast.LENGTH_SHORT).show();
        //getAppState
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Toast.makeText(this,"onRestart",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText(this,"onResume",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Toast.makeText(this,"onPause",Toast.LENGTH_SHORT).show();
        //startRecordState
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Toast.makeText(this,"onStop",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this,"onDestroy",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchRedditTask fetchRedditTask = new FetchRedditTask();
                fetchRedditTask.execute();
            }
        });
        // Create global configuration and initialize ImageLoader with this config
        // link https://github.com/nostra13/Android-Universal-Image-Loader
        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
//                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
//                .diskCacheExtraOptions(480, 800, null)
//                .taskExecutor(...)
//        .taskExecutorForCachedImages(...)
//        .threadPoolSize(3) // default
//                .threadPriority(Thread.NORM_PRIORITY - 2) // default
//                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
//                .denyCacheImageMultipleSizesInMemory()
//                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
//                .memoryCacheSize(2 * 1024 * 1024)
//                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiscCache(cacheDir)) // default
//                .diskCacheSize(50 * 1024 * 1024)
//                .diskCacheFileCount(100)
//                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
//                .imageDownloader(new BaseImageDownloader(context)) // default
//                .imageDecoder(new BaseImageDecoder()) // default
//                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
//                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);


        listView = (ListView) findViewById(R.id.listView);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

//                if (scrollState == SCROLL_STATE_FLING) {
//                    Log.d("Scroll", "The user had previously been scrolling using touch and had performed a fling.");
//
//                } else if (scrollState == SCROLL_STATE_IDLE) {
//                    Log.d("Scroll", "The view is not scrolling.");
//
//                } else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
//                    Log.d("Scroll", "Scrolling using touch, and their finger is still on the screen");
//                }

                if (view.getId() == listView.getId()) {
                    final int currentFirstVisibleItem = listView.getFirstVisiblePosition();

                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        mIsScrollingUp = false;
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        mIsScrollingUp = true;
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                Log.w("Scroll","onScroll firstVisibleItem=" +firstVisibleItem + " visibleItemCount="+visibleItemCount+" totalItemCount"+totalItemCount);

                if (firstVisibleItem > 1 && firstVisibleItem > (totalItemCount - visibleItemCount) - 1) {
                    Log.w("Scroll", firstVisibleItem + "Loading..." + mIsScrollingUp);
                }
            }

//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                this.currentFirstVisibleItem = firstVisibleItem;
//                this.currentVisibleItemCount = visibleItemCount;
//            }
//
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                this.currentScrollState = scrollState;
//                this.isScrollCompleted();
//            }
//
//            private void isScrollCompleted() {
//                if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE) {
//                    /*** In this way I detect if there's been a scroll which has completed ***/
//                    /*** do the work for load more date! ***/
//                    if(!isLoading){
//                        isLoading = true;
//                        loadMoreData();
//                    }
//                }
//            }

        });

        FetchRedditTask fetchRedditTask = new FetchRedditTask();
        fetchRedditTask.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class FetchRedditTask extends AsyncTask<Void, Void, List<Reddit>> {

        private final String LOG_TAG = FetchRedditTask.class.getSimpleName();

        @Override
        protected List<Reddit> doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://www.reddit.com/r/android/top/.json?sort=new&count=10&limit=10");
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
        protected void onPostExecute(List<Reddit> reddits) {
            super.onPostExecute(reddits);
            if (null != reddits && !reddits.isEmpty()) {
                RowAdapter adapter = new RowAdapter(getApplicationContext(), reddits);
                listView.setAdapter(adapter);
                listView.refreshDrawableState();

//                RedditDAO redditDAO = new RedditDAO(getApplicationContext());
//                redditDAO.setRedditList(reddits);

                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}