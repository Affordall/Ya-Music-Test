package com.realjamapps.yamusicapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.realjamapps.yamusicapp.adapters.MainGridAdapter;
import com.realjamapps.yamusicapp.models.Performer;
import com.realjamapps.yamusicapp.parsers.GetAllDataParser;
import com.realjamapps.yamusicapp.utils.DividerItemDecoration;
import com.realjamapps.yamusicapp.utils.Utils;
import com.transitionseverywhere.TransitionManager;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    public static final String APP_SETTINGS = "settings";
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private final int REQUEST_CODE_FILTERS = 1;
    private Toolbar toolbar;
    private SpotsDialog dialog;
    private MainGridAdapter mAdapter;
    private DatabaseHandler handler;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<Performer> mPerformerList;

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
            Pair<View, String> holderPair = Pair.create((View) placeNameHolder, "tNameHolder");
            //Pair<View, String> pricePair = Pair.create((View) placePriceHolder, "tPriceHolder");
            //Pair<View, String> navPair = Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME); //Shared elem is null
            //Pair<View, String> statusPair = Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
            Pair<View, String> toolbarPair = Pair.create((View) toolbar, "tActionBar");

            //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(TestMain.this, imagePair, holderPair, navPair, statusPair, toolbarPair);
            //ActivityOptionsCompat options = makeSceneTransitionAnimation(TestMain.this, imagePair, holderPair, navPair, statusPair, toolbarPair);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, imagePair , holderPair, toolbarPair);
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




       /* 1) База полная
        2) База пустая
        3) Интернет есть
        4) Интернета нет

        if (1) -> view saved db
        else if (3) -> parsing
        else -> error msg */

        if (handler.getPerformersCount() != 0) {
            Toast.makeText(this,"Load from DataBase",Toast.LENGTH_SHORT).show();
            mPerformerList = handler.getAllPerformers();

            mAdapter.refresh(mPerformerList);

        } else {
            checkInternetAndStartAT();
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

    void refreshList() {
        checkInternetAndStartAT();
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void checkInternetAndStartAT() {
        if (Utils.isNetworkUnavailable()) {
            if (handler.getPerformersCount() != 0) {
                handler.deleteAllDBs();
                //mAdapter.clear();
            }
            new DataFetcherTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Toast.makeText(this, getString(R.string.internet_is_off), Toast.LENGTH_SHORT).show();
        }
    }

    /** DataFetcherTask **/
    class DataFetcherTask extends AsyncTask<Void,Void,Void> {

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
}
