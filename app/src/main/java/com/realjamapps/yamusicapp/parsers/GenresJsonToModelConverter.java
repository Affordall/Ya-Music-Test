package com.realjamapps.yamusicapp.parsers;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.realjamapps.yamusicapp.models.Genres;
import com.realjamapps.yamusicapp.repository.impl.sql.GenresSqlRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by affy on 04.10.16.
 */

public class GenresJsonToModelConverter implements IGenresJsonToModelListener {

    @Inject
    public GenresJsonToModelConverter() {
    }

    private List<String> getGenresListResult(JSONObject jsonObjectItem) throws JSONException {
        JSONArray jsonArrayGenres = jsonObjectItem.getJSONArray("genres");

        String jsonFormattedString = jsonArrayGenres.toString().replaceAll("\"|\\[|\\]", "");

        List<String> finalList = Stream.of(jsonFormattedString.split("\\s*,\\s*"))
                .collect(Collectors.toList());

        return finalList;
    }

    @Override
    public List<String> getGenresList(JSONObject jsonObjectItem) throws JSONException {
        return getGenresListResult(jsonObjectItem);
    }
}
