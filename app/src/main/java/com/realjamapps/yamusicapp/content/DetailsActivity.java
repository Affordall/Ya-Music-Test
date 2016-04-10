package com.realjamapps.yamusicapp.content;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.database.DatabaseHandler;
import com.realjamapps.yamusicapp.models.Performer;
import com.realjamapps.yamusicapp.utils.Utils;

import java.io.InputStream;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_PARAM_ID = "item_id";
    private final int CAMERA_PIC_REQUEST = 99;
    private DatabaseHandler handler;
    private int gotId;

    private Uri uri;
    private String performerName;
    private String tracksAlbumsString;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.iv_image_details) SimpleDraweeView draweeView;
    @Bind(R.id.tv_details_genres) TextView mGenres;
    @Bind(R.id.tv_details_description) TextView mDescription;
    @Bind(R.id.tv_details_tracks_and_albums) TextView mTracksAndAlbums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setUpSupportActionBar();

        gotId = 0;
        gotId = getIntent().getIntExtra(EXTRA_PARAM_ID, 0);

        assert draweeView != null;
        draweeView.setDrawingCacheEnabled(true);

        //handler = new DatabaseHandler(DetailsActivity.this);
        handler = DatabaseHandler.getInstance(this);

        new GetItemByIdAT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(gotId));

        if (Utils.isLollipop()) {
            //windowTransition();
            //setupWindowAnimations();
        }
    }

    private void setUpSupportActionBar() {
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
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
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
                new DownloadImageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(uri));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void readyForShare(Bitmap image, String performerName, String tracksAlbumsString) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, performerName + "\n" + tracksAlbumsString);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "", null);
        Uri screenshotUri = Uri.parse(path);

        shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(Intent.createChooser(shareIntent, "Share Artist"),
                CAMERA_PIC_REQUEST);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
           String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            readyForShare(result,performerName,tracksAlbumsString);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            DetailsActivity.this.getContentResolver().delete(data.getData(), null, null);
        }
    }
}
