package com.realjamapps.yamusicapp.database.sql.mapper;

import android.database.Cursor;

import com.realjamapps.yamusicapp.database.sql.CursorUtils;
import com.realjamapps.yamusicapp.database.sql.tables.TableGenres;
import com.realjamapps.yamusicapp.models.Genres;

public class CursorToGenresMapper implements IBaseMapper<Cursor,Genres> {

    public Genres map(Cursor cursor) {
        return createNewGenreByCursor(cursor);
    }

    private Genres createNewGenreByCursor(Cursor cursor) {
        Genres genres = new Genres();
            genres.setId(getGenreIdFromCursor(cursor));
            genres.setName(getGenreNameFromCursor(cursor));
        return genres;
    }

    private int getGenreIdFromCursor(Cursor cursor) {
        return CursorUtils.getIntCursor(cursor, TableGenres.KEY_GENRES_ID);
    }

    private String getGenreNameFromCursor(Cursor cursor) {
        return CursorUtils.getStringCursor(cursor, TableGenres.KEY_GENRES_NAME);
    }
}
