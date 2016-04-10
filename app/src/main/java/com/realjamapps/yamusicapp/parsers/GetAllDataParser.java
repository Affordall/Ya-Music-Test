package com.realjamapps.yamusicapp.parsers;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.realjamapps.yamusicapp.database.DatabaseHandler;
import com.realjamapps.yamusicapp.models.Genres;
import com.realjamapps.yamusicapp.models.Performer;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GetAllDataParser {
    private final String url = "http://cache-spb03.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    private DatabaseHandler handler;

    private static final int MAX_CACHE_SIZE = 10 * 1024 * 1024;

    //private final OkHttpClient client = new OkHttpClient();

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public void getDataPlease(final Context context) {
        handler = DatabaseHandler.getInstance(context);

        OkHttpClient client = createCustomOkHttpClient(context);

        String serverData = null;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-type", "application/json")
                    .build();

            serverData = client.newCall(request).execute().body().string();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if (serverData != null) {
            getJson(serverData);
        } else {
            return;
        }
    }

    private static OkHttpClient createCustomOkHttpClient(Context context) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);

        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(context.getCacheDir(), "responses");
        Cache cache = new Cache(cacheDir, MAX_CACHE_SIZE);
        okHttpClient.setCache(cache);

        /** Dangerous interceptor that rewrites the server's cache-control header. */
        okHttpClient.interceptors().add(new Interceptor() {
            @Override public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .header("Cache-Control", "max-age=300")
                        .build();
            }
        });

        return okHttpClient;
    }

    private void checkGenreExist(DatabaseHandler handler, String TableName, String ColumnName,
                                String ColumnData) throws JSONException {
        if (handler.doIt(TableName, ColumnName, ColumnData)) {
            //Do Nothing. Genre already exist.
        } else {
            if(ColumnData != null && !ColumnData.isEmpty()) {
                handler.addGenres(new Genres(ColumnData));
            }
        }
    }

    private void getJson(String serverData) {
        try {
            JSONArray jsonArray = new JSONArray(serverData);
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObjectItem = jsonArray.getJSONObject(i);
                String itemNameRAW = jsonObjectItem.getString("name");

                JSONObject coversJO = jsonObjectItem.getJSONObject("cover");


                JSONArray jsonArrayGenres = jsonObjectItem.getJSONArray("genres");

                String rawGenreString = jsonArrayGenres.toString();

                String jsonFormattedString = rawGenreString.replaceAll("\"|\\[|\\]", "");

                List<String> eachGenreStringList =
                        Arrays.asList(jsonFormattedString.split("\\s*,\\s*"));


                List<String> genresList = new ArrayList<>();

                for (int j = 0; j < eachGenreStringList.size(); j++) {
                    Genres genre = new Genres();
                    String eachGenreNameString = eachGenreStringList.get(j);
                    genre.setName(eachGenreNameString);
                    checkGenreExist(handler, "genres", "genres_name", eachGenreNameString);
                    genresList.add(eachGenreNameString);
                }

                Performer item = new Performer();

                item.setmId(Integer.parseInt(jsonObjectItem.getString("id")));
                item.setmName(itemNameRAW);

                item.setmGenres(genresList);

                item.setmTracks(Integer.parseInt(jsonObjectItem.getString("tracks")));
                item.setmAlbums(Integer.parseInt(jsonObjectItem.getString("albums")));

                if(jsonObjectItem.has("link")) {
                    item.setmLink(jsonObjectItem.getString("link"));
                }

                String rawDescription  = jsonObjectItem.getString("description");
                String finDescription = rawDescription.substring(0,1).toUpperCase() + rawDescription.substring(1);

                item.setmDescription(finDescription);

                item.setmCoverSmall(coversJO.getString("small"));
                item.setmCoverBig(coversJO.getString("big"));

                handler.addPerformer(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

