package com.realjamapps.yamusicapp.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by affy on 04.10.16.
 */

public interface IGenresJsonToModelListener {
    List<String> getGenresList(JSONObject jsonObjectItem) throws JSONException;
}
