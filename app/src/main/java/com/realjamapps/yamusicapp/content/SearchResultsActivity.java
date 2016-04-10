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

import butterknife.Bind;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

public class SearchResultsActivity extends AppCompatActivity {

    private Menu menu;
    private LinearLayoutManager mLayoutManager;
    private SpotsDialog dialog;
    private MainGridAdapter mAdapter;
    private DatabaseHandler handler;
    private ArrayList<Performer> mSearchPerformerList;

    @Bind(R.id.search_result_list) RecyclerView mRecyclerView;
    @Bind(R.id.toolbar_search) Toolbar mToolbar;

    @Nullable
    MainGridAdapter.OnItemClickListener onItemClickListener = new MainGridAdapter.OnItemClickListener() {
        //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onItemClick(View v, int position) {

            Intent transitionIntent = new Intent(SearchResultsActivity.this, DetailsActivity.class);
            transitionIntent.putExtra(DetailsActivity.EXTRA_PARAM_ID, position);
            startActivity(transitionIntent);

            //TODO: https://github.com/lgvalle/Material-Animations/

            // TODO: https://codeforandroid.wordpress.com/2015/02/09/android-sample-code/

            // TODO: https://github.com/code-computerlove/FastScrollRecyclerView/
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.bind(this);

        setUpSupportActionBar();

        mLayoutManager = new LinearLayoutManager(this);

        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mSearchPerformerList = new ArrayList<>();

        mAdapter = new MainGridAdapter(getApplicationContext(), mSearchPerformerList);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(onItemClickListener);

        //handler = new DatabaseHandler(this);
        handler = DatabaseHandler.getInstance(this);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
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
        Utils.initToolBar(SearchResultsActivity.this, mToolbar, true, true, true, getString(R.string.search_result));
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
        }

        @Override
        protected void onPostExecute(ArrayList<Performer> result) {
            super.onPostExecute(result);
            mAdapter.refresh(result);
            dialog.dismiss();
        }
    }
}
