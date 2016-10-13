package com.realjamapps.yamusicapp.parsers;

import android.util.Log;

import com.annimon.stream.Stream;
import com.realjamapps.yamusicapp.app.YaMusicApp;
import com.realjamapps.yamusicapp.events.UpdateEvent;
import com.realjamapps.yamusicapp.models.Genres;
import com.realjamapps.yamusicapp.models.Performer;
import com.realjamapps.yamusicapp.repository.impl.sql.GenresSqlRepository;
import com.realjamapps.yamusicapp.repository.impl.sql.PerformersSqlRepository;
import com.realjamapps.yamusicapp.specifications.ISpecification;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class GetAllDataParser implements IParser {

    private static final String SOURCE_URL = "http://cache-spb03.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    private static volatile GetAllDataParser sInstance;

    @Inject PerformersJsonToModelConverter performerConverter;
    @Inject GenresJsonToModelConverter genresConverter;
    @Inject PerformersSqlRepository performersSqlRepository;
    @Inject GenresSqlRepository genresSqlRepository;
    @Inject OkHttpClient client;

    public static synchronized GetAllDataParser getInstance() {
        if (sInstance == null) {
            sInstance = new GetAllDataParser();
        }
        return sInstance;
    }

    private GetAllDataParser() {
        YaMusicApp.get(YaMusicApp.getContext()).getAppComponent().inject(this);
    }

    private void runParser(ISpecification specification) {
        String serverData = null;
        try {
            Request request = new Request.Builder()
                    .url(SOURCE_URL)
                    .addHeader("Content-type", "application/json")
                    .build();

            serverData = client.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (serverData != null) {
            long startTime = System.currentTimeMillis();
            getJson(serverData, specification);
            long endTime = System.currentTimeMillis();
            System.out.println("That took " + (endTime - startTime) + " milliseconds");
        } else {
            //Toast.makeText(context, context.getString(R.string.oops_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void getJson(String serverData, ISpecification specification) {
        Log.e("-GetAllDataParser-", "-Started-");
        try {
            JSONArray jsonArray = new JSONArray(serverData);
            for (int jsonArrayIndex = 0; jsonArrayIndex < jsonArray.length(); jsonArrayIndex++) {
                try {
                    JSONObject jsonObjectItem = jsonArray.getJSONObject(jsonArrayIndex);

                    Performer performer = new Performer();
                    performer.setmId(performerConverter.getPerformerId(jsonObjectItem));
                    performer.setmName(performerConverter.getPerformerName(jsonObjectItem));
                    performer.setmGenres(performerConverter.getPerformerGenresList(jsonObjectItem));
                    performer.setmTracks(performerConverter.getPerformerTracksCount(jsonObjectItem));
                    performer.setmAlbums(performerConverter.getPerformerAlbumsCount(jsonObjectItem));
                    performer.setmLink(performerConverter.getPerformerUrl(jsonObjectItem));
                    performer.setmDescription(performerConverter.getPerformerDescription(jsonObjectItem));
                    performer.setmCoverSmall(performerConverter.getPerformerCoverSmall(jsonObjectItem));
                    performer.setmCoverBig(performerConverter.getPerformerCoverBig(jsonObjectItem));

                    performersSqlRepository.add(performer);

                    addGenreToRepository(jsonObjectItem);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("-GetAllDataParser-", "-Done-");
        EventBus.getDefault().post(new UpdateEvent((ArrayList<Performer>) performersSqlRepository.query(specification)));
    }

    private void addGenreToRepository(JSONObject jsonObjectItem) throws JSONException {
        List<String> genresList = genresConverter.getGenresList(jsonObjectItem);
        Stream.of()
                .forEach(item -> safeCheck(item, genresSqlRepository));
    }

    private void safeCheck(Object ColumnData, GenresSqlRepository repository) {
        Genres genres = new Genres();
        genres.setName((String)ColumnData);
        repository.add(genres);
    }

    @Override
    public void getData(ISpecification specification) {
        runParser(specification);
    }
}

