package com.realjamapps.yamusicapp.content;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.adapters.GenresListViewAdapter;
import com.realjamapps.yamusicapp.database.DatabaseHandler;
import com.realjamapps.yamusicapp.models.Genres;
import com.realjamapps.yamusicapp.utils.DividerItemDecoration;
import com.realjamapps.yamusicapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;


public class GenreFilterActivity extends AppCompatActivity {

    private GenresListViewAdapter mAdapter;
    private ArrayList<Genres> genresList;
    private SpotsDialog dialog;
    private DatabaseHandler handler;
    private final int RESULT_GETALL = 77;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.rv_genres_filter) RecyclerView mRecyclerView;
    @Bind(R.id.fabFilters) FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);

        setUpSupportActionBar();

        handler = DatabaseHandler.getInstance(this);

        genresList = new ArrayList<>();

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, null));
        assert mRecyclerView != null;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new GenresListViewAdapter(genresList);
        mRecyclerView.setAdapter(mAdapter);

        if (isDBnotEmpty()) {
            startGenresFetcherTask();
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: 06.07.16 Can be simplified

                String data;
                Intent filterIntent = new Intent();

                ArrayList<Genres> genreFilters = mAdapter.getGenresList();

                ArrayList<String> onlyGenresNameList = new ArrayList<>();

//                for (Genres single : genreFilters) {
//
//                }

                for (int i = 0; i < genreFilters.size(); i++) {
                    Genres singleGenre = genreFilters.get(i);
                    if (singleGenre.isSelected()) {
                        data = singleGenre.getName();
                        onlyGenresNameList.add(data);
                    }
                }
                filterIntent.putStringArrayListExtra("filter", onlyGenresNameList);
                setResult(RESULT_OK, filterIntent);
                finish();
            }
        });
    }

    private boolean isDBnotEmpty() {
        return handler.getGenresCount() !=0;
    }

    private AsyncTask startGenresFetcherTask() {
        return new SelectAllGenresTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    class SelectAllGenresTask extends AsyncTask<String,Void,ArrayList<Genres>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new SpotsDialog(GenreFilterActivity.this, R.style.CustomProgressDialogStyle);
            dialog.show();
        }

        @Override
        protected ArrayList<Genres> doInBackground(String... params) {
            return handler.getAllGenres();
        }

        @Override
        protected void onPostExecute(ArrayList<Genres> result) {
            super.onPostExecute(result);
            mAdapter.refresh(result);
            dialog.dismiss();
        }
    }

    private void setUpSupportActionBar() {
        Utils.initToolBar(GenreFilterActivity.this, toolbar, false, true, true, getString(R.string.title_filter_activity));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_item_select_all:
                Intent returnIntent = new Intent();
                setResult(RESULT_GETALL, returnIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filters, menu);
        return true;
    }
}
