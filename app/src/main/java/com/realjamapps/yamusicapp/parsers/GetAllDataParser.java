package com.realjamapps.yamusicapp.parsers;

import android.content.Context;
import android.widget.Toast;

import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.database.DatabaseHandler;
import com.realjamapps.yamusicapp.models.Genres;
import com.realjamapps.yamusicapp.models.Performer;
import com.realjamapps.yamusicapp.models.PerformerBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
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
    private final String SOURCE_URL = "http://cache-spb03.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    private DatabaseHandler handler;

    private static final int MAX_CACHE_SIZE = 10 * 1024 * 1024;

    public void getDataPlease(final Context context) {
        handler = DatabaseHandler.getInstance(context);

        OkHttpClient client = createCustomOkHttpClient(context);

        String serverData = null;
        try {
            Request request = new Request.Builder()
                    .url(SOURCE_URL)
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
            Toast.makeText(context, context.getString(R.string.oops_error), Toast.LENGTH_SHORT).show();
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
        if (!handler.checkIfRecordExist(TableName, ColumnName, ColumnData)) {
            if(ColumnData != null && !ColumnData.isEmpty()) {
                handler.addGenres(new Genres(ColumnData));
            }
        }
    }

    private void getJson(String serverData) {
        try {
            JSONArray jsonArray = new JSONArray(serverData);
            for (int jsonArrayIndex = 0; jsonArrayIndex < jsonArray.length(); jsonArrayIndex++) {

                JSONObject jsonObjectItem = jsonArray.getJSONObject(jsonArrayIndex);

                String itemNameRAW = getPerfName(jsonObjectItem);

                List<String> genresList = getPerfGenres(jsonObjectItem);

                int perfId = universalGetIntByString(jsonObjectItem, "id");
                int perfTracks = universalGetIntByString(jsonObjectItem, "tracks");
                int perfAlbums = universalGetIntByString(jsonObjectItem, "albums");

                String perfUrl = getPerfLink(jsonObjectItem);

                String finDescription = getPerfDescription(jsonObjectItem);

                String perfCoverSmall = getPerfCover(jsonObjectItem, "small");
                String perfCoverBig = getPerfCover(jsonObjectItem, "big");

                Performer item = new PerformerBuilder()
                        .withId(perfId)
                        .withName(itemNameRAW)
                        .withGenres(genresList)
                        .withTracks(perfTracks)
                        .withAlbums(perfAlbums)
                        .withLink(perfUrl)
                        .withDescription(finDescription)
                        .withCoverSmall(perfCoverSmall)
                        .withCoverBig(perfCoverBig)
                        .build();

                handler.addPerformer(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getPerfName(JSONObject jsonObjectItem) throws JSONException {
        return jsonObjectItem.getString("name");
    }

    private String getPerfCover(JSONObject jsonObjectItem, String instr) throws JSONException {
        JSONObject coversJO = jsonObjectItem.getJSONObject("cover");
        return coversJO.getString(instr);
    }

    private List<String> getPerfGenres(JSONObject jsonObjectItem) throws JSONException {
        JSONArray jsonArrayGenres = jsonObjectItem.getJSONArray("genres");

        String jsonFormattedString = jsonArrayGenres.toString().replaceAll("\"|\\[|\\]", "");

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
        return genresList;
    }

    private String getPerfLink(JSONObject jsonObjectItem) throws JSONException {
        String perfUrl;
        if (jsonObjectItem.has("link")) {
                perfUrl = jsonObjectItem.getString("link");
        } else perfUrl = null;
        return perfUrl;
    }

    private String getPerfDescription(JSONObject jsonObjectItem) throws JSONException {
        String rawDescription  = jsonObjectItem.getString("description");
        return rawDescription.substring(0,1).toUpperCase() + rawDescription.substring(1);
    }

    private int universalGetIntByString(JSONObject jsonObjectItem, String instr) throws JSONException {
        return Integer.parseInt(jsonObjectItem.getString(instr));
    }
}

