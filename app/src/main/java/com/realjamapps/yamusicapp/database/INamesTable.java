package com.realjamapps.yamusicapp.database;

public interface INamesTable {

    String PERFORMERS = "performers";
    String GENRES = "genres";

    interface Performer {
        String ID = "id_from_api";
        String NAME = "performer_name";
        String GENRES = "genres";
        String LINK = "link";
        String COUNT_TRACKS = "count_tracks";
        String COUNT_ALBUMS = "count_albums";
        String DESCRIPTION = "description";
        String COVER_SMALL = "cover_small";
        String COVER_BIG = "cover_big";
    }

    interface Genres {
        String ID = "genres_id";
        String NAME = "genres_name";
    }
}
