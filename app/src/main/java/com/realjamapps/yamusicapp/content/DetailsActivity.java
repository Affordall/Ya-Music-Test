package com.realjamapps.yamusicapp.content;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.database.DatabaseHandler;
import com.realjamapps.yamusicapp.models.Performer;
import com.realjamapps.yamusicapp.utils.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_PARAM_ID = "item_id";
    private TextView mDescription, mGenres, mTracksAndAlbums;
    private DatabaseHandler handler;
    private Menu menu;
    private int gotId;
    private Toolbar mToolbar;
    private SimpleDraweeView draweeView;

    ShareActionProvider mShareActionProvider;

    Uri uri;
    String performerName;
    String tracksAlbumsString;

    Intent shareIntent;

    public static Handler newThread = new Handler();


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

            uri = Uri.parse(result.getmCoverBig());

            assert draweeView != null;
            draweeView.setImageURI(uri);

            assert getSupportActionBar() != null;
            performerName = result.getmName();
            getSupportActionBar().setTitle(performerName);

            List genres = result.getmGenres();

            String genresString = TextUtils.join(", ", genres);

            mGenres.setText(genresString);

            String ascii="  \u00B7  ";

            tracksAlbumsString = (
                    String.valueOf(result.getmAlbums())
                    + " " + getString(R.string.albums)
                    + ascii
                    + String.valueOf(result.getmTracks())
                    + " " + getString(R.string.tracks));
            mTracksAndAlbums.setText(tracksAlbumsString);

            String description = result.getmDescription();
            mDescription.setText(description);

            newThread.post(new Runnable() {
                @Override
                public void run() {
                    shareIntent = readyForShare(getStreamAndCreateBitmap(uri),performerName,tracksAlbumsString);
                }
            });
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);

        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Return true to display menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case (R.id.menu_item_share):
                doShare(shareIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupContentForShare() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, performerName);
        shareIntent.putExtra(Intent.EXTRA_TEXT, tracksAlbumsString);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        doShare(shareIntent);

                   /*Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, received_link_single);
                shareIntent.setType("text/plain");
                doShare(shareIntent);*/

    }

    private void doShare(Intent shareIntent) {
        // When you want to share set the share intent.
        /*if (mShareActionProvider != null) {
            mShareActionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
            mShareActionProvider.setShareIntent(shareIntent);
        }*/
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Artist"));
    }

    private InputStream openStreamToGetBitmap(final String inputURL) {
        final InputStream[] is = {null};
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                //code to do the HTTP request
                OkHttpClient client = new OkHttpClient();
                Response response = null;
                assert inputURL != null;
                Request request = new Request.Builder().url(inputURL)
                        .build();
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    assert response != null;
                    is[0] = response.body().byteStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        return is[0];
    }

    private Bitmap getStreamAndCreateBitmap(Uri uri) {
        InputStream inputStream = openStreamToGetBitmap(String.valueOf(uri));
        Bitmap immutableBpm = BitmapFactory.decodeStream(inputStream);
        Bitmap mutableBitmap = immutableBpm.copy(Bitmap.Config.ARGB_8888, true);

        View view  = new View(this);
        view.draw(new Canvas(mutableBitmap));
        return mutableBitmap;
    }

    private Intent readyForShare(Bitmap image, String performerName, String tracksAlbumsString) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, performerName);
        intent.putExtra(Intent.EXTRA_TEXT, tracksAlbumsString);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "", null);
        Uri screenshotUri = Uri.parse(path);

        intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        intent.setType("image/*");
        return intent;
    }
}
