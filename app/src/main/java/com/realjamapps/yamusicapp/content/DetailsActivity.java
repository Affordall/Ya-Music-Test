package com.realjamapps.yamusicapp.content;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.database.DatabaseHandler;
import com.realjamapps.yamusicapp.models.Performer;
import com.realjamapps.yamusicapp.utils.Utils;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_PARAM_ID = "item_id";
    //public static final String NAV_BAR_VIEW_NAME = Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME;
    private TextView mDescription, mGenres, mTracksAndAlbums;
    private DatabaseHandler handler;
    private int gotId;
    private Toolbar mToolbar;
    private SimpleDraweeView draweeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setUpSupportActionBar();

        gotId = 0;
        gotId = getIntent().getIntExtra(EXTRA_PARAM_ID, 0);

        draweeView = (SimpleDraweeView) findViewById(R.id.iv_image_details);
        assert draweeView != null;
        draweeView.setDrawingCacheEnabled(true);

        mGenres = (TextView) findViewById(R.id.tv_details_genres);
        mTracksAndAlbums = (TextView) findViewById(R.id.tv_details_tracks_and_albums);
        mDescription = (TextView) findViewById(R.id.tv_details_description);

        handler = new DatabaseHandler(DetailsActivity.this);

        new GetItemByIdAT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(gotId));

        if (Utils.isLollipop()) {
            //windowTransition();
            //setupWindowAnimations();
        }
    }

    private void setUpSupportActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.initToolBar(DetailsActivity.this, mToolbar, true, true, true, null); //false, null
    }


    public class GetItemByIdAT extends AsyncTask<String, Void, Performer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Performer doInBackground(String... value) {

            String identifier = value[0];
            return handler.getSinglePerformer(Integer.parseInt(identifier));
        }

        @Override
        protected void onPostExecute(Performer result) {
            super.onPostExecute(result);

            Uri uri = Uri.parse(result.getmCoverBig());

            assert draweeView != null;
            draweeView.setImageURI(uri);

            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle(result.getmName());

            List genres = result.getmGenres();

            String genresString = TextUtils.join(", ", genres);

            mGenres.setText(genresString);

            String ascii="  \u00B7  ";

            String tracksAlbumsString = (
                    String.valueOf(result.getmAlbums())
                    + " " + getString(R.string.albums)
                    + ascii
                    + String.valueOf(result.getmTracks())
                    + " " + getString(R.string.tracks));
            mTracksAndAlbums.setText(tracksAlbumsString);

            String description = result.getmDescription();
            mDescription.setText(description);
        }
    }
}
