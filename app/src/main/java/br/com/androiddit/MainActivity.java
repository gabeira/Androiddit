package br.com.androiddit;

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

    @Override
    protected void onStart() {
        super.onStart();
//        Toast.makeText(this,"onStart",Toast.LENGTH_SHORT).show();
        //getAppState

        redditDAO.open();
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

        redditDAO.close();
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


        redditDAO = new RedditDAO(getApplicationContext());

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getApplicationContext(), getString(R.string.new_reddits), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(),pageLoaded+" "+getString(R.string.load_reddits),Toast.LENGTH_LONG).show();
                    pageLoaded++;
                    getRedditsTask(afterLoad);
                }
            }
        });
//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
////                if (scrollState == SCROLL_STATE_FLING) {
////                    Log.d("Scroll", "The user had previously been scrolling using touch and had performed a fling.");
////
////                } else if (scrollState == SCROLL_STATE_IDLE) {
////                    Log.d("Scroll", "The view is not scrolling.");
////
////                } else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
////                    Log.d("Scroll", "Scrolling using touch, and their finger is still on the screen");
////                }
//
//                if (view.getId() == listView.getId()) {
//                    final int currentFirstVisibleItem = listView.getFirstVisiblePosition();
//
//                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
//                        mIsScrollingUp = false;
//                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
//                        mIsScrollingUp = true;
//                    }
//
//                    mLastFirstVisibleItem = currentFirstVisibleItem;
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
////                Log.w("Scroll","onScroll firstVisibleItem=" +firstVisibleItem + " visibleItemCount="+visibleItemCount+" totalItemCount"+totalItemCount);
//
//                if (firstVisibleItem > 1 && firstVisibleItem > (totalItemCount - visibleItemCount) - 1) {
//
//
//                    Log.w("Scroll", firstVisibleItem + "Loading..." + mIsScrollingUp);
//
////                    FetchRedditTask fetchRedditTask = new FetchRedditTask();
////                    fetchRedditTask.setListener(MainActivity.this);
////                    fetchRedditTask.execute(afterLoad);
//
//                }
//            }
//
////            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
////                this.currentFirstVisibleItem = firstVisibleItem;
////                this.currentVisibleItemCount = visibleItemCount;
////            }
////
////            public void onScrollStateChanged(AbsListView view, int scrollState) {
////                this.currentScrollState = scrollState;
////                this.isScrollCompleted();
////            }
////
////            private void isScrollCompleted() {
////                if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE) {
////                    /*** In this way I detect if there's been a scroll which has completed ***/
////                    /*** do the work for load more date! ***/
////                    if(!isLoading){
////                        isLoading = true;
////                        loadMoreData();
////                    }
////                }
////            }
//
//        });

        getRedditsTask(null);


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
            Toast.makeText(getApplicationContext(), adapter.getCount()+" DB has " + redditDAO.getRedditList().size(), Toast.LENGTH_SHORT).show();

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

        redditDAO.deleteAllReddits();
        redditDAO.setRedditList(page.getReddits());

        swipeRefreshLayout.setRefreshing(false);
    }

    public void getRedditsTask(String after){
        FetchRedditTask fetchRedditTask = new FetchRedditTask();
        fetchRedditTask.setListener(MainActivity.this);
        fetchRedditTask.execute(after);
    }
}