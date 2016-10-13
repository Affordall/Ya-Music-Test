package com.realjamapps.yamusicapp.content;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.adapters.MainGridAdapter;
import com.realjamapps.yamusicapp.app.YaMusicApp;
import com.realjamapps.yamusicapp.database.sql.DatabaseHandler;
import com.realjamapps.yamusicapp.events.UpdateEvent;
import com.realjamapps.yamusicapp.intro.FancyAppIntro;
import com.realjamapps.yamusicapp.models.Performer;
import com.realjamapps.yamusicapp.receivers.DownloadResultReceiver;
import com.realjamapps.yamusicapp.repository.impl.sql.PerformersSqlRepository;
import com.realjamapps.yamusicapp.services.DownloadServiceIntent;
import com.realjamapps.yamusicapp.specifications.impl.sql.SqlAllPerformersSortedByNameSpecification;
import com.realjamapps.yamusicapp.utils.DividerItemDecoration;
import com.realjamapps.yamusicapp.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver {

    private LinearLayoutManager mLayoutManager;
    private int savedIntoViewIndex;
    private final int REQUEST_CODE_FILTERS = 1;
    private final int RESULT_GETALL = 77;
    private Menu menu;
    private SpotsDialog dialog;
    private MainGridAdapter mAdapter;
    private DatabaseHandler handler;
    private ArrayList<Performer> mPerformerList;
    private DownloadResultReceiver mReceiver;

    private MainGridAdapter.OnItemClickListener onItemClickListener;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rc_list) RecyclerView mRecyclerView;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout mSwipeRefreshLayout;

//    @Nullable
//    MainGridAdapter.OnItemClickListener onItemClickListener = new MainGridAdapter.OnItemClickListener() {
//        @Override
//        public void onItemClick(View v, int position) {
//
//            Intent transitionIntent = new Intent(MainActivity.this, DetailsActivity.class);
//            transitionIntent.putExtra(DetailsActivity.EXTRA_PARAM_ID, position);
//            startActivity(transitionIntent);
//        }
//    };

//    @Nullable
//    MainGridAdapter.OnItemClickListener onItemClickListener = (v, position) -> {
//
//        Intent transitionIntent = new Intent(MainActivity.this, DetailsActivity.class);
//        transitionIntent.putExtra(DetailsActivity.EXTRA_PARAM_ID, position);
//        startActivity(transitionIntent);
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            EventBus.getDefault().register(this);
        } catch (Throwable t){
            //this may crash if registration did not go through. just be safe
        }
        getAllSharedPreferences();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        //showIntroDependingByIntroIndexVariable();

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, null));

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mPerformerList = Utils.newInstancePerformer();

        onItemClickListener =
                (view1, position) -> onItemClick(mAdapter.getArticle(position));

        mAdapter = new MainGridAdapter(getApplicationContext(), mPerformerList, onItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(mAdapter);

        handler = DatabaseHandler.getInstance(this);

        initializeDownloadService();




//        if (!isDBempty()) {
//            getArtistAndRefreshAdapter();
//        } else {
            checkInternetAndStartService();
//        }

        initializeSwipeRefreshLayout();

    }

    private void onItemClick(Performer performer) {
        Intent transitionIntent = new Intent(MainActivity.this, DetailsActivity.class);
        transitionIntent.putExtra(DetailsActivity.EXTRA_PARAM_ID, performer.getmId());
        startActivity(transitionIntent);
    }

    private boolean isDBempty() {
        return handler.getPerformersCount() == 0;
    }

    private void initializeSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshList);

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_red_dark);
    }

    private void initializeDownloadService() {
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
    }

    void refreshList() {
        checkInternetAndStartService();
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void checkInternetAndStartService() {
        if (Utils.isNetworkUnavailable()) {
//            if (isDBempty()) {
//                handler.deleteAllDBs();
////                if (mAdapter != null) {
////                    mAdapter.clear();
////                }
//            }
            Utils.createIntentStartService(this, mReceiver);
        } else {
            Toast.makeText(this, getString(R.string.internet_is_off), Toast.LENGTH_SHORT).show();
        }
    }

    private void getArtistAndRefreshAdapter() {
            //mPerformerList = handler.getAllPerformers();
            mAdapter.refresh(mPerformerList);
    }

    @Subscribe
    private void onRefresh(UpdateEvent event) {
        mPerformerList = event.list;
        mAdapter.refresh(mPerformerList);
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    protected void onDestroy() {
        try {
            EventBus.getDefault().unregister(this);
        } catch (Throwable t){
            //this may crash if registration did not go through. just be safe
        }
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadServiceIntent.STATUS_RUNNING:

//                 dialog = new SpotsDialog(MainActivity.this, R.style.CustomProgressDialogStyle);
//                 dialog.show();

                //setRecyclerViewVisibility(false);

                break;
            case DownloadServiceIntent.STATUS_FINISHED:

                /** After many tests on Execution Time
                 * (Direct call from DB this or wrap this in AsyncTask)
                 * Decided leave direct call method getAllPerformers from DB handler.
                 * Difference in 2 times.
                 * on Middle-End devices: (AT 87ms) vs (Direct 77ms)
                 * on Low-End devices: (AT 407ms) vs (Direct 211ms)*/

                //ArrayList<Performer> itemList = handler.getAllPerformers();
                mPerformerList = (ArrayList<Performer>) new PerformersSqlRepository(handler).query(new SqlAllPerformersSortedByNameSpecification());
                mAdapter.refresh(mPerformerList);

//                if (dialog != null) {
//                    dialog.dismiss();
//                }

                //setRecyclerViewVisibility(true);

                Toast.makeText(MainActivity.this, getString(R.string.update_complete), Toast.LENGTH_LONG).show();
                break;
            case DownloadServiceIntent.STATUS_ERROR:
//                if (dialog != null) {
//                    dialog.dismiss();
//                }

                //setRecyclerViewVisibility(true);

                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }

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
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            case (R.id.action_filter):
                Intent filtersIntent = new Intent(MainActivity.this, GenreFilterActivity.class);
                startActivityForResult(filtersIntent, REQUEST_CODE_FILTERS);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    private void setRecyclerViewVisibility(boolean shouldVisible) {
//        if (shouldVisible) {
//            mRecyclerView.setVisibility(View.VISIBLE);
//        } else {
//            mRecyclerView.setVisibility(View.INVISIBLE);
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        YaMusicApp.activityResumed();
        //mReceiver = new DownloadResultReceiver(new Handler());
        //mReceiver.setReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        YaMusicApp.activityPaused();
        //mReceiver.setReceiver(null);
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
            Utils.logError(e);
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
            // TODO: 04.10.16 fix
            return null;
            //return handler.getAllPerformersByGenre(params);
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

        if (resultCode == RESULT_GETALL) {
            //mPerformerList = null;
            //getArtistAndRefreshAdapter();
        }

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

    private void getAllSharedPreferences() {
        SharedPreferences sharedPreferences =
                getSharedPreferences(Utils.APP_SETTINGS, Context.MODE_PRIVATE);
        savedIntoViewIndex = sharedPreferences.getInt("SAVED_VIEW_INTRO_OR_NOT_INDEX", 0);
    }

    private void showIntroDependingByIntroIndexVariable() {
        if (savedIntoViewIndex == 0) {
            Intent intentIntro = new Intent(this, FancyAppIntro.class);
            startActivity(intentIntro);
        }
    }

}
