package com.realjamapps.yamusicapp.content;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.realjamapps.yamusicapp.database.DatabaseHandler;
import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.adapters.MainGridAdapter;
import com.realjamapps.yamusicapp.models.Performer;
import com.realjamapps.yamusicapp.receivers.DownloadResultReceiver;
import com.realjamapps.yamusicapp.services.DownloadServiceIntent;
import com.realjamapps.yamusicapp.utils.DividerItemDecoration;
import com.realjamapps.yamusicapp.utils.Utils;
import com.realjamapps.yamusicapp.utils.YaMusicApp;
import com.transitionseverywhere.TransitionManager;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    public static final String APP_SETTINGS = "settings";
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private final int REQUEST_CODE_FILTERS = 1;
    private Toolbar toolbar;
    private Menu menu;
    private SpotsDialog dialog;
    private MainGridAdapter mAdapter;
    private DatabaseHandler handler;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<Performer> mPerformerList;

    private DownloadResultReceiver mReceiver;


    @Nullable
    MainGridAdapter.OnItemClickListener onItemClickListener = new MainGridAdapter.OnItemClickListener() {
        //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onItemClick(View v, int position) {

            Intent transitionIntent = new Intent(MainActivity.this, DetailsActivity.class);
            transitionIntent.putExtra(DetailsActivity.EXTRA_PARAM_ID, position);

            ImageView placeImage = (ImageView) v.findViewById(R.id.iv_image_url);
            placeImage.setDrawingCacheEnabled(true);
            TransitionManager.setTransitionName(placeImage, "tImage");

            //LinearLayout placeNameHolder = (LinearLayout) v.findViewById(R.id.placeNameHolder);
            TextView placeNameHolder = (TextView) v.findViewById(R.id.tv_performer_name);
            TransitionManager.setTransitionName(placeNameHolder, "tNameHolder");

            //RelativeLayout placePriceHolder = (RelativeLayout) v.findViewById(R.id.placePriceHolder);
            //TransitionManager.setTransitionName(placePriceHolder, "tPriceHolder");

            TransitionManager.setTransitionName(toolbar, "tActionBar");

            //View navigationBar = findViewById(android.R.id.navigationBarBackground);
            //View statusBar = findViewById(android.R.id.statusBarBackground);


            Pair<View, String> imagePair = Pair.create((View) placeImage, "tImage");
            //Pair<View, String> holderPair = Pair.create((View) placeNameHolder, "tNameHolder");
            //Pair<View, String> pricePair = Pair.create((View) placePriceHolder, "tPriceHolder");
            //Pair<View, String> navPair = Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME); //Shared elem is null
            //Pair<View, String> statusPair = Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
            Pair<View, String> toolbarPair = Pair.create((View) toolbar, "tActionBar");

            //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(TestMain.this, imagePair, holderPair, navPair, statusPair, toolbarPair);
            //ActivityOptionsCompat options = makeSceneTransitionAnimation(TestMain.this, imagePair, holderPair, navPair, statusPair, toolbarPair);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, imagePair , toolbarPair);
            ActivityCompat.startActivity(MainActivity.this, transitionIntent, options.toBundle());

            //TODO: https://github.com/lgvalle/Material-Animations/

            // TODO: https://codeforandroid.wordpress.com/2015/02/09/android-sample-code/

            // TODO: https://github.com/code-computerlove/FastScrollRecyclerView/


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.rc_list);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, null));
        //https://gist.github.com/zokipirlo/82336d89249e05bba5aa

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mPerformerList = new ArrayList<>();

        mAdapter = new MainGridAdapter(getApplicationContext(), mPerformerList);


        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(onItemClickListener);


        handler = new DatabaseHandler(this);


        /* Starting Download Service */
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);


        if (isDBempty()) {
            Toast.makeText(this,"Load from DataBase",Toast.LENGTH_SHORT).show();
            mPerformerList = handler.getAllPerformers();
            mAdapter.refresh(mPerformerList);
        } else {
            //checkInternetAndStartAT();
            checkInternetAndStartService();
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override

            public void onRefresh() {

                refreshList();

            }

        });

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_red_dark);
    }

    private boolean isDBempty() {
        return handler.getPerformersCount() !=0;
    }

    void refreshList() {
        //checkInternetAndStartAT();
        checkInternetAndStartService();
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /*private void checkInternetAndStartAT() {
        if (Utils.isNetworkUnavailable()) {
            if (isDBempty()) {
                handler.deleteAllDBs();
                if (mAdapter != null) {
                    mAdapter.clear();
                }
            }
            dts = new DataFetcherTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else {
            Toast.makeText(this, getString(R.string.internet_is_off), Toast.LENGTH_SHORT).show();
        }
    }*/

    private void checkInternetAndStartService() {

        if (Utils.isNetworkUnavailable()) {
            if (isDBempty()) {
                handler.deleteAllDBs();
                if (mAdapter != null) {
                    mAdapter.clear();
                }
            }

            Utils.createIntentStartService(this, mReceiver);

        } else {
            Toast.makeText(this, getString(R.string.internet_is_off), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadServiceIntent.STATUS_RUNNING:

                if (YaMusicApp.isActivityVisible()) {
                    dialog = new SpotsDialog(MainActivity.this, R.style.CustomProgressDialogStyle);
                    dialog.show();
                }
                break;
            case DownloadServiceIntent.STATUS_FINISHED:

                /** After many tests on Execution Time
                 * (Direct call from DB this or wrap this in AsyncTask)
                 * Decided leave direct call method getAllPerformers from DB handler.
                 * Difference in 2 times.
                 * on Middle-End devices: (AT 87ms) vs (Direct 77ms)
                 * on Low-End devices: (AT 407ms) vs (Direct 211ms)*/

                ArrayList<Performer> itemList = handler.getAllPerformers();
                mAdapter.refresh(itemList);
                assert dialog != null;
                dialog.dismiss();
                Toast.makeText(MainActivity.this, getString(R.string.update_complete), Toast.LENGTH_LONG).show();

                break;
            case DownloadServiceIntent.STATUS_ERROR:
                assert dialog != null;
                dialog.dismiss();
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }


    /** DataFetcherTask **/
    /*class DataFetcherTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new SpotsDialog(MainActivity.this, R.style.CustomProgressDialogStyle);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            new GetAllDataParser().getDataPlease(getApplicationContext());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayList<Performer> itemList = handler.getAllPerformers();
            mAdapter.refresh(itemList);
            dialog.dismiss();
            Toast.makeText(MainActivity.this, getString(R.string.update_complete), Toast.LENGTH_LONG).show();
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(getApplicationContext(), SearchResultsActivity.class)));

        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case (R.id.action_settings):
                return true;

            case (R.id.listview):
                Intent filtersIntent = new Intent(MainActivity.this, GenreFilterActivity.class);
                startActivityForResult(filtersIntent, REQUEST_CODE_FILTERS);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        YaMusicApp.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        YaMusicApp.activityPaused();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable("myState"));
            mLayoutManager.scrollToPosition(savedInstanceState.getInt("lastPosition"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putParcelable("myState", mLayoutManager.onSaveInstanceState());
            outState.putInt("lastPosition",mLayoutManager.findFirstCompletelyVisibleItemPosition());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return;
        }
    }

    /** Method call AsyncTask for get All Items from Category
     * by Selected String param
     * return ArrayList<Item> as result
     * **/
    class SelectPerformerByGenreTask extends AsyncTask<String,Void,ArrayList<Performer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new SpotsDialog(MainActivity.this, R.style.CustomProgressDialogStyle);
            dialog.show();
        }

        @Override
        protected ArrayList<Performer> doInBackground(String... params) {
            //return handler.getAllPerformersByGenre(params);
            return handler.getAllPerformersByGenreTest(params);

        }

        @Override
        protected void onPostExecute(ArrayList<Performer> result) {
            super.onPostExecute(result);
            mAdapter.refresh(result);
            dialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SelectPerformerByGenreTask selectPerformerByGenreTask = new SelectPerformerByGenreTask();

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FILTERS:

                    ArrayList<String> filterListFromIntent = data.getStringArrayListExtra("filter");

                    String[] stringArrayOfFilters = filterListFromIntent.toArray(new String[filterListFromIntent.size()]);

                    try {
                        selectPerformerByGenreTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, stringArrayOfFilters);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.wrong_result), Toast.LENGTH_LONG).show();
        }
    }
}