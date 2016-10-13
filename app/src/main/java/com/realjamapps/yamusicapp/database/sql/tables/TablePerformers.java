package com.realjamapps.yamusicapp.database.sql.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.realjamapps.yamusicapp.database.INamesTable;
import com.realjamapps.yamusicapp.database.sql.SqlStatementValidation;
import com.realjamapps.yamusicapp.models.Performer;

import javax.inject.Inject;

public class TablePerformers {

    public static final String TABLE_PERFORMERS = INamesTable.PERFORMERS;

    public static final String KEY_ID = INamesTable.Performer.ID;
    public static final String KEY_PERFORMER_NAME = INamesTable.Performer.NAME;
    public static final String KEY_PERFORMER_GENRES = INamesTable.Performer.GENRES;
    public static final String KEY_PERFORMER_LINK = INamesTable.Performer.LINK;
    public static final String KEY_COUNT_TRACKS = INamesTable.Performer.COUNT_TRACKS;
    public static final String KEY_COUNT_ALBUMS = INamesTable.Performer.COUNT_ALBUMS;
    public static final String KEY_DESCRIPTION = INamesTable.Performer.DESCRIPTION;
    public static final String KEY_COVER_SMALL = INamesTable.Performer.COVER_SMALL;
    public static final String KEY_COVER_BIG = INamesTable.Performer.COVER_BIG;

    private static final String CREATE_PERFORMER_TABLE = "create table "
            + TABLE_PERFORMERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_PERFORMER_NAME + " text, "
            + KEY_PERFORMER_GENRES + " text, "
            + KEY_COUNT_TRACKS + " INTEGER, "
            + KEY_COUNT_ALBUMS + " INTEGER, "
            + KEY_PERFORMER_LINK + " text, "
            + KEY_DESCRIPTION + " text, "
            + KEY_COVER_SMALL + " text, "
            + KEY_COVER_BIG + " text);";

    private final SqlStatementValidation sqlStatementValidation = new SqlStatementValidation();

    @Inject
    public TablePerformers() {
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_PERFORMER_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(TablePerformers.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PERFORMERS);
    }

    public void add(Performer performer, SQLiteOpenHelper helper) {
        String sql = "INSERT OR REPLACE INTO " + TABLE_PERFORMERS +
                " ( " + KEY_ID +
                ", " + KEY_PERFORMER_NAME +
                ", " + KEY_PERFORMER_GENRES +
                ", " + KEY_COUNT_TRACKS +
                ", " + KEY_COUNT_ALBUMS +
                ", " + KEY_PERFORMER_LINK +
                ", " + KEY_DESCRIPTION +
                ", " + KEY_COVER_SMALL +
                ", " + KEY_COVER_BIG +
                " ) " + "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        final SQLiteDatabase database = helper.getWritableDatabase();
        database.beginTransactionNonExclusive();
        sqlStatementValidation.runSqlStatement(database, sql, performer);
    }

    public void add(Iterable<Performer> iterable, SQLiteOpenHelper helper) {
        for(Performer item : iterable){
            add(item, helper);
        }
    }

    public void update(Performer performer, SQLiteOpenHelper helper) {
        final SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PERFORMER_NAME, performer.getmName());
        values.put(KEY_COVER_SMALL, performer.getmCoverSmall());

        db.update(TABLE_PERFORMERS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(performer.getmId())});
    }

    public void remove(Performer performer, SQLiteOpenHelper helper) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_PERFORMERS, KEY_ID + " = ?",
                new String[]{String.valueOf(performer.getmId())});
        if (db.isOpen())
            db.close();
    }

    public int count(SQLiteOpenHelper helper) {
        int num;
        final SQLiteDatabase db = helper.getWritableDatabase();
        try {
            String QUERY = "SELECT  * FROM " + TABLE_PERFORMERS;
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            cursor.close();
            if (db.isOpen())
                db.close();
            return num;
        } catch (Exception e) {
            Log.e("error", e + "");
        }
        return 0;
    }
}
