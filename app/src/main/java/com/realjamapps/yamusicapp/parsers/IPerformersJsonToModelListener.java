package com.realjamapps.yamusicapp.parsers;

import com.realjamapps.yamusicapp.models.Genres;
import com.realjamapps.yamusicapp.repository.impl.sql.GenresSqlRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.realm.RealmList;

public interface IPerformersJsonToModelListener {

    String getPerformerName(JSONObject jsonObjectItem) throws JSONException;
    int getPerformerId(JSONObject jsonObjectItem) throws JSONException;
    int getPerformerTracksCount(JSONObject jsonObjectItem) throws JSONException;
    int getPerformerAlbumsCount(JSONObject jsonObjectItem) throws JSONException;
    List<String> getPerformerGenresList(JSONObject jsonObjectItem) throws JSONException;
    String getPerformerUrl(JSONObject jsonObjectItem) throws JSONException;
    String getPerformerDescription(JSONObject jsonObjectItem) throws JSONException;
    String getPerformerCoverSmall(JSONObject jsonObjectItem) throws JSONException;
    String getPerformerCoverBig(JSONObject jsonObjectItem) throws JSONException;

}
