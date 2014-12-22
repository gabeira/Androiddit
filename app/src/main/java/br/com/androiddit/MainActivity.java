package br.com.androiddit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import br.com.androiddit.data.Page;
import br.com.androiddit.data.RedditDAO;
import br.com.androiddit.interfaces.TaskFinishListener;

public class MainActivity extends ActionBarActivity implements TaskFinishListener {

    ScrollListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    private int mLastFirstVisibleItem;
    private boolean mIsScrollingUp;
    RowAdapter adapter;
    RedditDAO redditDAO;
    String afterLoad;
    int pageLoaded;
    private boolean loading;
    private static SharedPreferences.Editor editor;
    private static SharedPreferences sharedPreferences;

    @Override
    protected void onStart() {
        super.onStart();
        redditDAO.open();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.editor.putInt("position", listView.getFirstVisiblePosition());
        MainActivity.editor.commit();
        redditDAO.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        MainActivity.editor.putInt("position", listView.getLastVisiblePosition());
//        MainActivity.editor.commit();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.sharedPreferences = this.getSharedPreferences("redlist",0);
        MainActivity.editor = MainActivity.sharedPreferences.edit();

        redditDAO = new RedditDAO(getApplicationContext());

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getApplicationContext(), getString(R.string.new_reddits), Toast.LENGTH_SHORT).show();

                MainActivity.editor.putInt("position", 0);
                MainActivity.editor.commit();

                getRedditsTask(null);
            }
        });
        // Create global configuration and initialize ImageLoader with this config
        // link https://github.com/nostra13/Android-Universal-Image-Loader
        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
//                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
//                .diskCacheExtraOptions(480, 800, null)
//                .taskExecutor(...)
//                .taskExecutorForCachedImages(...)
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 2) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
//                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiscCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(this)) // default
//                .imageDecoder(new BaseImageDecoder()) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);


        listView = (ScrollListView) findViewById(R.id.listView);

        listView.setOnBottomReachedListener(new ScrollListView.OnBottomReachedListener() {
            @Override
            public void onBottomReached() {
                if (!loading && pageLoaded<4) {
                    loading = true;
                    Toast.makeText(getApplicationContext(),getString(R.string.load_reddits)+" "+adapter.getCount(),Toast.LENGTH_LONG).show();
                    pageLoaded++;
                    getRedditsTask(afterLoad);
                }
            }
        });

        if (null != sharedPreferences && sharedPreferences.getInt("position",0) > 0) {
            adapter = new RowAdapter(getApplicationContext(), redditDAO.getRedditList());
            listView.setAdapter(adapter);
            listView.refreshDrawableState();
            loading = false;

            listView.setSelection(sharedPreferences.getInt("position", 0));
            Toast.makeText(getApplicationContext(),getString(R.string.load_reddits_fromdb),Toast.LENGTH_LONG).show();

        }else {
            getRedditsTask(null);
        }

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
        if (id == R.id.action_refresh) {
            getRedditsTask(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFinish(Page page) {

        if (null == adapter || null == afterLoad || afterLoad.isEmpty()) {

            adapter = new RowAdapter(getApplicationContext(), page.getReddits());
            listView.setAdapter(adapter);
        } else {
            adapter.addEntries(page.getReddits());
            adapter.notifyDataSetChanged();
        }
        loading = false;
        afterLoad = page.getAfter();
        listView.refreshDrawableState();

        redditDAO.setRedditList(page.getReddits());

        swipeRefreshLayout.setRefreshing(false);
    }

    public void getRedditsTask(String after){

        if (null == after || after.isEmpty()){
            redditDAO.deleteAllReddits();
            if (null != adapter)
                adapter.clear();
        }

        FetchRedditTask fetchRedditTask = new FetchRedditTask();
        fetchRedditTask.setListener(MainActivity.this);
        fetchRedditTask.execute(after);

    }
}