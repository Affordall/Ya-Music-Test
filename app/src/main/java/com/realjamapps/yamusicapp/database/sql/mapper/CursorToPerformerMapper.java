package com.realjamapps.yamusicapp.database.sql.mapper;

import android.database.Cursor;

import com.realjamapps.yamusicapp.database.sql.CursorUtils;
import com.realjamapps.yamusicapp.database.sql.tables.TablePerformers;
import com.realjamapps.yamusicapp.models.Performer;

import java.util.Arrays;
import java.util.List;

public class CursorToPerformerMapper implements IBaseMapper<Cursor,Performer> {

    public Performer map(Cursor cursor) {
        return createNewPerformerByCursor(cursor);
    }

    private Performer createNewPerformerByCursor(Cursor cursor) {
        Performer performer = new Performer();
            performer.setmId(getPerfIdFromCursor(cursor));
            performer.setmName(getPerfNameFromCursor(cursor));
            performer.setmGenres(getGenresListFromCursor(cursor));
            performer.setmTracks(getPerfCountTracksFromCursor(cursor));
            performer.setmAlbums(getPerfCountAlbumsFromCursor(cursor));
            performer.setmLink(getPerfLinkFromCursor(cursor));
            performer.setmDescription(getPerfDescriptionFromCursor(cursor));
            performer.setmCoverSmall(getPerfCoverSmallFromCursor(cursor));
            performer.setmCoverBig(getPerfCoverBigFromCursor(cursor));
        return performer;
    }

    private int getPerfIdFromCursor(Cursor cursor) {
        return CursorUtils.getIntCursor(cursor, TablePerformers.KEY_ID);
    }

    private String getPerfNameFromCursor(Cursor cursor) {
        return CursorUtils.getStringCursor(cursor, TablePerformers.KEY_PERFORMER_NAME);
    }

    private List<String> getGenresListFromCursor(Cursor cursor) {
        String genresString = CursorUtils.getStringCursor(cursor, TablePerformers.KEY_PERFORMER_GENRES);
        return Arrays.asList(genresString.split("\\s*,\\s*"));
    }

    private int getPerfCountTracksFromCursor(Cursor cursor) {
        return CursorUtils.getIntCursor(cursor, TablePerformers.KEY_COUNT_TRACKS);
    }

    private int getPerfCountAlbumsFromCursor(Cursor cursor) {
        return CursorUtils.getIntCursor(cursor, TablePerformers.KEY_COUNT_ALBUMS);
    }

    private String getPerfLinkFromCursor(Cursor cursor) {
        return CursorUtils.getStringCursor(cursor, TablePerformers.KEY_PERFORMER_LINK);
    }

    private String getPerfDescriptionFromCursor(Cursor cursor) {
        return CursorUtils.getStringCursor(cursor, TablePerformers.KEY_DESCRIPTION);
    }

    private String getPerfCoverSmallFromCursor(Cursor cursor) {
        return CursorUtils.getStringCursor(cursor, TablePerformers.KEY_COVER_SMALL);
    }

    private String getPerfCoverBigFromCursor(Cursor cursor) {
        return CursorUtils.getStringCursor(cursor, TablePerformers.KEY_COVER_BIG);
    }
}
