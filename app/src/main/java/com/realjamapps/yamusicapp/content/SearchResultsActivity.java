package com.realjamapps.yamusicapp.content;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.adapters.MainGridAdapter;
import com.realjamapps.yamusicapp.database.DatabaseHandler;
import com.realjamapps.yamusicapp.models.Performer;
import com.realjamapps.yamusicapp.utils.Utils;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class SearchResultsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Menu menu;
    RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    SpotsDialog dialog;
    private MainGridAdapter mAdapter;
    DatabaseHandler handler;
    private ArrayList<Performer> mSearchPerformerList;

    @Nullable
    MainGridAdapter.OnItemClickListener onItemClickListener = new MainGridAdapter.OnItemClickListener() {
        //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onItemClick(View v, int position) {

            Intent transitionIntent = new Intent(SearchResultsActivity.this, DetailsActivity.class);
            transitionIntent.putExtra(DetailsActivity.EXTRA_PARAM_ID, position);

            ImageView placeImage = (ImageView) v.findViewById(R.id.iv_image_url);
            placeImage.setDrawingCacheEnabled(true);

            //LinearLayout placeNameHolder = (LinearLayout) v.findViewById(R.id.placeNameHolder);
            TextView placeNameHolder = (TextView) v.findViewById(R.id.tv_performer_name);

            //RelativeLayout placePriceHolder = (RelativeLayout) v.findViewById(R.id.placePriceHolder);
            //TransitionManager.setTransitionName(placePriceHolder, "tPriceHolder");


            //View navigationBar = findViewById(android.R.id.navigationBarBackground);
            //View statusBar = findViewById(android.R.id.statusBarBackground);


            Pair<View, String> imagePair = Pair.create((View) placeImage, "tImage");
            //Pair<View, String> holderPair = Pair.create((View) placeNameHolder, "tNameHolder");
            //Pair<View, String> pricePair = Pair.create((View) placePriceHolder, "tPriceHolder");
            //Pair<View, String> navPair = Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME); //Shared elem is null
            //Pair<View, String> statusPair = Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
            Pair<View, String> toolbarPair = Pair.create((View) mToolbar, "tActionBar");

            //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(TestMain.this, imagePair, holderPair, navPair, statusPair, toolbarPair);
            //ActivityOptionsCompat options = makeSceneTransitionAnimation(TestMain.this, imagePair, holderPair, navPair, statusPair, toolbarPair);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SearchResultsActivity.this, imagePair , toolbarPair);
            ActivityCompat.startActivity(SearchResultsActivity.this, transitionIntent, options.toBundle());

            //TODO: https://github.com/lgvalle/Material-Animations/

            // TODO: https://codeforandroid.wordpress.com/2015/02/09/android-sample-code/

            // TODO: https://github.com/code-computerlove/FastScrollRecyclerView/


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        setUpSupportActionBar();

        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.search_result_list);
        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mSearchPerformerList = new ArrayList<>();

        mAdapter = new MainGridAdapter(getApplicationContext(), mSearchPerformerList);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(onItemClickListener);

        /****/
        handler = new DatabaseHandler(this);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            new SearchInDatabaseAT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.empty_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpSupportActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.initToolBar(SearchResultsActivity.this, mToolbar, true, true, true, null); //false, null
    }

    /** SearchInDatabaseAT **/
    class SearchInDatabaseAT extends AsyncTask<String,Void,ArrayList<Performer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new SpotsDialog(SearchResultsActivity.this, R.style.CustomProgressDialogStyle);
            dialog.show();
        }

        @Override
        protected ArrayList<Performer> doInBackground(String... params) {

            return handler.getSearchResult(params);
            //return handler.getSearchTestResult(params);
        }

        @Override
        protected void onPostExecute(ArrayList<Performer> result) {
            super.onPostExecute(result);
            mAdapter.refresh(result);
            dialog.dismiss();
        }
    }
}
