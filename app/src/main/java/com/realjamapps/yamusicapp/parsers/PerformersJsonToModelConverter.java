package com.realjamapps.yamusicapp.parsers;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

public class PerformersJsonToModelConverter implements IPerformersJsonToModelListener {

    @Inject
    public PerformersJsonToModelConverter() {
    }

    private String getPerformerNameResult(JSONObject jsonObjectItem) throws JSONException {
        return jsonObjectItem.getString("name");
    }

    private int getPerformerIdResult(JSONObject jsonObjectItem) throws JSONException {
        return universalGetIntByString(jsonObjectItem, "id");
    }

    private int getPerformerTracksCountResult(JSONObject jsonObjectItem) throws JSONException {
        return universalGetIntByString(jsonObjectItem, "tracks");
    }

    private int getPerformerAlbumsCountResult(JSONObject jsonObjectItem) throws JSONException {
        return universalGetIntByString(jsonObjectItem, "albums");
    }

    private int universalGetIntByString(JSONObject jsonObjectItem, String instr) throws JSONException {
        return Integer.parseInt(jsonObjectItem.getString(instr));
    }

    private List<String> getPerformerGenresListResult(JSONObject jsonObjectItem) throws JSONException {
        JSONArray jsonArrayGenres = jsonObjectItem.getJSONArray("genres");

        String jsonFormattedString = jsonArrayGenres.toString().replaceAll("\"|\\[|\\]", "");

        List<String> finalList = Stream.of(jsonFormattedString.split("\\s*,\\s*"))
                .collect(Collectors.toList());

        return finalList;
    }

    private String getPerformerUrlResult(JSONObject jsonObjectItem) throws JSONException {
        String perfUrl;
        if (jsonObjectItem.has("link")) {
            perfUrl = jsonObjectItem.getString("link");
        } else perfUrl = null;
        return perfUrl;
    }

    private String getPerformerDescriptionResult(JSONObject jsonObjectItem) throws JSONException {
        String rawDescription  = jsonObjectItem.getString("description");
        return rawDescription.substring(0,1).toUpperCase() + rawDescription.substring(1);
    }

    private String getPerformerCoverSmallResult(JSONObject jsonObjectItem) throws JSONException {
        JSONObject coversJO = jsonObjectItem.getJSONObject("cover");
        return coversJO.getString("small");
    }

    private String getPerformerCoverBigResult(JSONObject jsonObjectItem) throws JSONException {
        JSONObject coversJO = jsonObjectItem.getJSONObject("cover");
        return coversJO.getString("big");
    }

    /**
     * Interface callback to encapsulate class methods.
     * */

//    private IPerformersJsonToModelListener mCallbackJsonPerformersToModelConverter = new IPerformersJsonToModelListener() {
//
//        @Override
//        public String getPerformerName(JSONObject jsonObjectItem) throws JSONException {
//            return getPerformerNameResult(jsonObjectItem);
//        }
//
//        @Override
//        public int getPerformerId(JSONObject jsonObjectItem) throws JSONException {
//            return getPerformerIdResult(jsonObjectItem);
//        }
//
//        @Override
//        public int getPerformerTracksCount(JSONObject jsonObjectItem) throws JSONException {
//            return getPerformerTracksCountResult(jsonObjectItem);
//        }
//
//        @Override
//        public int getPerformerAlbumsCount(JSONObject jsonObjectItem) throws JSONException {
//            return getPerformerAlbumsCountResult(jsonObjectItem);
//        }
//
//        @Override
//        public List<String> getPerformerGenresList(JSONObject jsonObjectItem) throws JSONException {
//            return getPerformerGenresListResult(jsonObjectItem);
//        }
//
//        @Override
//        public String getPerformerUrl(JSONObject jsonObjectItem) throws JSONException {
//            return getPerformerUrlResult(jsonObjectItem);
//        }
//
//        @Override
//        public String getPerformerDescription(JSONObject jsonObjectItem) throws JSONException {
//            return getPerformerDescriptionResult(jsonObjectItem);
//        }
//
//        @Override
//        public String getPerformerCoverSmall(JSONObject jsonObjectItem) throws JSONException {
//            return getPerformerCoverSmallResult(jsonObjectItem);
//        }
//
//        @Override
//        public String getPerformerCoverBig(JSONObject jsonObjectItem) throws JSONException {
//            return getPerformerCoverBigResult(jsonObjectItem);
//        }
//
//    };
//
//    public IPerformersJsonToModelListener getmCallbackJsonPerformersToModelConverter() {
//        return mCallbackJsonPerformersToModelConverter;
//    }

    @Override
    public String getPerformerName(JSONObject jsonObjectItem) throws JSONException {
        return getPerformerNameResult(jsonObjectItem);
    }

    @Override
    public int getPerformerId(JSONObject jsonObjectItem) throws JSONException {
        return getPerformerIdResult(jsonObjectItem);
    }

    @Override
    public int getPerformerTracksCount(JSONObject jsonObjectItem) throws JSONException {
        return getPerformerTracksCountResult(jsonObjectItem);
    }

    @Override
    public int getPerformerAlbumsCount(JSONObject jsonObjectItem) throws JSONException {
        return getPerformerAlbumsCountResult(jsonObjectItem);
    }

    @Override
    public List<String> getPerformerGenresList(JSONObject jsonObjectItem) throws JSONException {
        return getPerformerGenresListResult(jsonObjectItem);
    }

    @Override
    public String getPerformerUrl(JSONObject jsonObjectItem) throws JSONException {
        return getPerformerUrlResult(jsonObjectItem);
    }

    @Override
    public String getPerformerDescription(JSONObject jsonObjectItem) throws JSONException {
        return getPerformerDescriptionResult(jsonObjectItem);
    }

    @Override
    public String getPerformerCoverSmall(JSONObject jsonObjectItem) throws JSONException {
        return getPerformerCoverSmallResult(jsonObjectItem);
    }

    @Override
    public String getPerformerCoverBig(JSONObject jsonObjectItem) throws JSONException {
        return getPerformerCoverBigResult(jsonObjectItem);
    }
}
