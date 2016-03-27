package com.realjamapps.yamusicapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.realjamapps.yamusicapp.listeners.GenresListener;
import com.realjamapps.yamusicapp.listeners.PerformersListener;
import com.realjamapps.yamusicapp.models.Genres;
import com.realjamapps.yamusicapp.models.Performer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper implements PerformersListener, GenresListener {

    private static final String DATABASE_NAME = "yamusicbase";
    private static final int DATABASE_VERSION = 1;

    // Tables name
    private static final String TABLE_PERFORMERS = "performers";
    private static final String TABLE_GENRES = "genres";

    // Performer Table Columns names
    private static final String KEY_ID = "id_from_api";
    private static final String KEY_PERFORMER_NAME = "performer_name";
    private static final String KEY_PERFORMER_GENRES = "genres";
    private static final String KEY_PERFORMER_LINK = "link";
    private static final String KEY_COUNT_TRACKS = "count_tracks";
    private static final String KEY_COUNT_ALBUMS = "count_albums";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_COVER_SMALL = "cover_small";
    private static final String KEY_COVER_BIG = "cover_big";

    // Genres Table Columns names
    private static final String KEY_GENRES_ID = "genres_id";
    private static final String KEY_GENRES_NAME = "genres_name";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Database creation sql statement
    private static final String CREATE_PERFORMER_TABLE = "create table "
            + TABLE_PERFORMERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_PERFORMER_NAME + " text, "
            + KEY_PERFORMER_GENRES + " text, "
            + KEY_PERFORMER_LINK + " text, "
            + KEY_COUNT_TRACKS + " INTEGER, "
            + KEY_COUNT_ALBUMS + " INTEGER, "
            + KEY_DESCRIPTION + " text, "
            + KEY_COVER_SMALL + " text, "
            + KEY_COVER_BIG + " text);";

    private static final String CREATE_TABLE_GENRES = "CREATE TABLE "
            + TABLE_GENRES + "("
            + KEY_GENRES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_GENRES_NAME + " TEXT" + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PERFORMER_TABLE);
        db.execSQL(CREATE_TABLE_GENRES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERFORMERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRES);
        onCreate(db);
    }

    public void deleteAllDBs(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PERFORMERS, null, null);
        db.delete(TABLE_GENRES, null, null);
        db.close();
    }

    public Boolean doIt(String TableName, String ColumnName, String ColumnData) {
        boolean outValue = false;
        try {
            outValue = doesRecordExist(TableName, ColumnName, ColumnData);
        } catch (SQLException e) {
            //e.printStackTrace();
            // It's fine if throws a NPE
            // Sorry :(
        }
        return outValue;
    }

    public Boolean doesRecordExist(String TableName, String ColumnName, String ColumnData) throws SQLException {
        String q = "Select * FROM "+ TableName + " WHERE " + ColumnName + "='" + ColumnData + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()) {
            cursor.close();
            closeDB(db);
            return true;
        } else {
            cursor.close();
            closeDB(db);
            return false;
        }
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    @Override
    public void addPerformer(Performer perf) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, perf.getmId());
            values.put(KEY_PERFORMER_NAME, perf.getmName());
            values.put(KEY_PERFORMER_GENRES, TextUtils.join(", ", perf.getmGenres()));
            values.put(KEY_PERFORMER_LINK, perf.getmLink());
            values.put(KEY_COUNT_TRACKS, perf.getmTracks());
            values.put(KEY_COUNT_ALBUMS, perf.getmAlbums());
            values.put(KEY_DESCRIPTION, perf.getmDescription());
            values.put(KEY_COVER_SMALL, perf.getmCoverSmall());
            values.put(KEY_COVER_BIG, perf.getmCoverBig());

            db.insert(TABLE_PERFORMERS, null, values);
            closeDB(db);
        } catch (Exception e) {
            Log.e("problem", e + "");
        }
    }

    @Override
    public void addGenres(Genres genres) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_GENRES_ID, genres.getId());
            values.put(KEY_GENRES_NAME, genres.getName());

            db.insert(TABLE_GENRES, null, values);
            closeDB(db);
        } catch (Exception e) {
            Log.e("problem", e + "");
        }
    }

    @Override
    public ArrayList<Performer> getAllPerformers() {
        ArrayList<Performer> itemList = new ArrayList<>();

        //String selectQuery = "SELECT  * FROM " + TABLE_ITEMS;
        String selectQuery =  "SELECT  * FROM " + TABLE_PERFORMERS + " ORDER BY "+ KEY_PERFORMER_NAME +" ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Performer item = new Performer();
                item.setmId(getIntCursor(cursor, KEY_ID));
                item.setmName(getStringCursor(cursor, KEY_PERFORMER_NAME));

                String genresString = getStringCursor(cursor, KEY_PERFORMER_GENRES);
                List<String> genresList = Arrays.asList(genresString.split("\\s*,\\s*"));
                item.setmGenres(genresList);

                item.setmTracks(getIntCursor(cursor, KEY_COUNT_TRACKS));
                item.setmAlbums(getIntCursor(cursor, KEY_COUNT_ALBUMS));
                item.setmLink(getStringCursor(cursor, KEY_PERFORMER_LINK));
                item.setmDescription(getStringCursor(cursor, KEY_DESCRIPTION));
                item.setmCoverSmall(getStringCursor(cursor, KEY_COVER_SMALL));
                item.setmCoverBig(getStringCursor(cursor, KEY_COVER_BIG));

                itemList.add(item);
            } while (cursor.moveToNext());
        }
        closeDB(db);
        cursor.close();
        return itemList;
    }

    @Override
    public ArrayList<Genres> getAllGenres() {
        ArrayList<Genres> shopList = new ArrayList<>();

        //String selectQuery = "SELECT  * FROM " + TABLE_SHOP;
        String selectQuery = "SELECT  * FROM " + TABLE_GENRES + " ORDER BY "+ KEY_GENRES_NAME +" ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Genres genres = new Genres();
                genres.setId(getIntCursor(cursor, KEY_GENRES_ID));
                genres.setName(getStringCursor(cursor, KEY_GENRES_NAME));
                shopList.add(genres);
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDB(db);
        return shopList;
    }

    private String getStringCursor(Cursor cursor, String columnIndex) {
        return cursor.getString(cursor.getColumnIndex(columnIndex));
    }

    private int getIntCursor(Cursor cursor, String columnIndex) {
        return cursor.getInt(cursor.getColumnIndex(columnIndex));
    }


    /**
     * Get count of tables
     */

    @Override
    public int getPerformersCount() {
        int num;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String QUERY = "SELECT  * FROM " + TABLE_PERFORMERS;
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            cursor.close();
            closeDB(db);
            return num;
        } catch (Exception e) {
            Log.e("error", e + "");
        }
        return 0;
    }

    @Override
    public int getGenresCount() {
        int num;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String QUERY = "SELECT  * FROM " + TABLE_GENRES;
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            cursor.close();
            closeDB(db);
            return num;
        } catch (Exception e) {
            Log.e("error", e + "");
        }
        return 0;
    }

    private void closeDB(SQLiteDatabase db) {
        if (db != null && db.isOpen())
            db.close();
    }


}
