package com.realjamapps.yamusicapp.content;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.adapters.MainGridAdapter;
import com.realjamapps.yamusicapp.database.DatabaseHandler;
import com.realjamapps.yamusicapp.intro.FancyAppIntro;
import com.realjamapps.yamusicapp.models.Performer;
import com.realjamapps.yamusicapp.receivers.DownloadResultReceiver;
import com.realjamapps.yamusicapp.services.DownloadServiceIntent;
import com.realjamapps.yamusicapp.utils.DividerItemDecoration;
import com.realjamapps.yamusicapp.utils.Utils;
import com.realjamapps.yamusicapp.utils.YaMusicApp;

import java.util.ArrayList;

import butterknife.Bind;
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

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.rc_list) RecyclerView mRecyclerView;
    @Bind(R.id.swipeContainer) SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    MainGridAdapter.OnItemClickListener onItemClickListener = new MainGridAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {

            Intent transitionIntent = new Intent(MainActivity.this, DetailsActivity.class);
            transitionIntent.putExtra(DetailsActivity.EXTRA_PARAM_ID, position);
            startActivity(transitionIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getAllSharedPreferences();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        showIntroDependingByIntroIndexVariable();

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, null));

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mPerformerList = Utils.newInstancePerformer();

        mAdapter = new MainGridAdapter(getApplicationContext(), mPerformerList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(onItemClickListener);

        handler = DatabaseHandler.getInstance(this);

        /* Initialize Download Service */
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        if (isDBempty()) {
            getArtistAndRefreshAdapter();
        } else {
            checkInternetAndStartService();
        }

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
        checkInternetAndStartService();
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

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

    private void getArtistAndRefreshAdapter() {
            mPerformerList = handler.getAllPerformers();
            mAdapter.refresh(mPerformerList);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadServiceIntent.STATUS_RUNNING:

                 dialog = new SpotsDialog(MainActivity.this, R.style.CustomProgressDialogStyle);
                 dialog.show();

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

                if (dialog != null) {
                    dialog.dismiss();
                }
                Toast.makeText(MainActivity.this, getString(R.string.update_complete), Toast.LENGTH_LONG).show();
                break;
            case DownloadServiceIntent.STATUS_ERROR:
                if (dialog != null) {
                    dialog.dismiss();
                }
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
    protected void onStop() {
        System.gc();
        super.onStop();
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
            return handler.getAllPerformersByGenre(params);
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
            mPerformerList = null;
            getArtistAndRefreshAdapter();
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
