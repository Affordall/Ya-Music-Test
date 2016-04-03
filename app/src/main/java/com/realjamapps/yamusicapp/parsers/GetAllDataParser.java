package com.realjamapps.yamusicapp.parsers;

import android.content.Context;

import com.realjamapps.yamusicapp.database.DatabaseHandler;
import com.realjamapps.yamusicapp.models.Genres;
import com.realjamapps.yamusicapp.models.Performer;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetAllDataParser {
    private final String url = "http://cache-spb03.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    private DatabaseHandler handler;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public void getDataPlease(final Context context) {
        handler = new DatabaseHandler(context);

        String serverData = null;
        OkHttpClient client = new OkHttpClient();

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


                // TODO: 29.03.16 Add if statement for catch exceptions

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
            e.printStackTrace(); // TODO: тоже залогировать или обработать надо
        }
    }
}

