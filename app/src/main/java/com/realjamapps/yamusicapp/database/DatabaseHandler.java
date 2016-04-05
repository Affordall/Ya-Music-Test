package com.realjamapps.yamusicapp.database;

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
            + KEY_COUNT_TRACKS + " INTEGER, "
            + KEY_COUNT_ALBUMS + " INTEGER, "
            + KEY_PERFORMER_LINK + " text, "
            + KEY_DESCRIPTION + " text, "
            + KEY_COVER_SMALL + " text, "
            + KEY_COVER_BIG + " text);";

    private static final String CREATE_TABLE_GENRES = "CREATE TABLE "
            + TABLE_GENRES + "("
            + KEY_GENRES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_GENRES_NAME + " TEXT NOT NULL" + ")";

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

    public boolean isDBlocked() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.isDbLockedByCurrentThread();
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
            values.put(KEY_COUNT_TRACKS, perf.getmTracks());
            values.put(KEY_COUNT_ALBUMS, perf.getmAlbums());
            values.put(KEY_PERFORMER_LINK, perf.getmLink());
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
            //values.put(KEY_GENRES_ID, genres.getId());
            values.put(KEY_GENRES_NAME, genres.getName());

            db.insert(TABLE_GENRES, null, values);
            closeDB(db);
        } catch (Exception e) {
            Log.e("problem", e + "");
        }
    }

    public Performer getSinglePerformer(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PERFORMERS,
                new String[]{KEY_ID,
                        KEY_PERFORMER_NAME,
                        KEY_PERFORMER_GENRES,
                        KEY_COUNT_TRACKS,
                        KEY_COUNT_ALBUMS,
                        KEY_PERFORMER_LINK,
                        KEY_DESCRIPTION,
                        KEY_COVER_SMALL,
                        KEY_COVER_BIG}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        String genresGetString = getStringCursor(cursor, KEY_PERFORMER_GENRES);
        List<String> genresGetList = Arrays.asList(genresGetString.split("\\s*,\\s*"));

        Performer single_item = new Performer(getIntCursor(cursor, KEY_ID),
                getStringCursor(cursor, KEY_PERFORMER_NAME),
                genresGetList,
                getIntCursor(cursor, KEY_COUNT_TRACKS),
                getIntCursor(cursor, KEY_COUNT_ALBUMS),
                getStringCursor(cursor, KEY_PERFORMER_LINK),
                getStringCursor(cursor, KEY_DESCRIPTION),
                getStringCursor(cursor, KEY_COVER_SMALL),
                getStringCursor(cursor, KEY_COVER_BIG));

        if (cursor != null)
            cursor.close();
        closeDB(db);
        return single_item;
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
        ArrayList<Genres> genresArrayList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_GENRES + " ORDER BY "+ KEY_GENRES_NAME +" ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Genres genres = new Genres();
                genres.setId(getIntCursor(cursor, KEY_GENRES_ID));
                genres.setName(getStringCursor(cursor, KEY_GENRES_NAME));
                genresArrayList.add(genres);
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDB(db);
        return genresArrayList;
    }

    private String getStringCursor(Cursor cursor, String columnIndex) {
        return cursor.getString(cursor.getColumnIndex(columnIndex));
    }

    private int getIntCursor(Cursor cursor, String columnIndex) {
        return cursor.getInt(cursor.getColumnIndex(columnIndex));
    }

    public ArrayList<Performer> getAllPerformersByGenreTest(String [] str) {
        ArrayList<Performer> itemList = new ArrayList<>();

        //String selectQuery = "SELECT  * FROM " + TABLE_PERFORMERS + " WHERE " + KEY_PERFORMER_GENRES + " IN " + "%cns";
        String selectQuery = "SELECT  * FROM " + TABLE_PERFORMERS + " WHERE " + KEY_PERFORMER_GENRES + " IN " + "%cns";

        StringBuilder cns = new StringBuilder();
        cns.append("('");
        for(int i = 0; i < str.length; i++) {
            cns.append(String.valueOf(str[i]));
            if (i < str.length - 1) {
                cns.append("','");
            }
        }
        cns.append("')");

        String finalQuery = selectQuery.replaceAll("%cns", cns.toString());
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(finalQuery, null);
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
        cursor.close();
        closeDB(db);
        return itemList;
    }

    public ArrayList<Performer> getAllPerformersByGenre(String [] str) {
        ArrayList<Performer> itemList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_PERFORMERS + " WHERE " + KEY_PERFORMER_GENRES + " IN " + "%cns";

        StringBuilder cns = new StringBuilder();
        cns.append("('");
        for(int i = 0; i < str.length; i++) {
            cns.append(String.valueOf(str[i]));
            if (i < str.length - 1) {
                cns.append("','");
            }
        }
        cns.append("')");

        String finalQuery = selectQuery.replaceAll("%cns", cns.toString());
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(finalQuery, null);
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
        cursor.close();
        closeDB(db);
        return itemList;
    }


    public ArrayList<Performer> getSearchTestResult(String[] wordsForSearch) {

        ArrayList<Performer> itemList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder cns = new StringBuilder();
        cns.append("'%$");
        for(int i = 0; i < wordsForSearch.length; i++) {
            cns.append(String.valueOf(wordsForSearch[i]));
            if (i < wordsForSearch.length - 1) {
                cns.append("%','%$");
            }
        }
        cns.append("%'");

        String finalQuery = cns.toString();

        String[] columns = new String[] {KEY_PERFORMER_NAME};
        Cursor cursor = db.query(TABLE_PERFORMERS, columns, KEY_PERFORMER_NAME + " like ?",
                new String[]{finalQuery}, null, null, null, null);
                //new String[]{"'%" + Arrays.toString(wordsForSearch) + "%'"}, null, null, null, null);

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
        cursor.close();
        closeDB(db);
        return itemList;
    }


    public ArrayList<Performer> getSearchResult(String[] wordsForSearch) {

        ArrayList<Performer> itemList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_PERFORMERS + " WHERE "
                + KEY_PERFORMER_NAME + " LIKE " + "(cast(" + "%cns" +" as text))";

        /*String selectQuery = "SELECT * FROM " + TABLE_PERFORMERS +  " WHERE "
                + "(" + KEY_PERFORMER_NAME + ")"
                //+ " OR "
                //+ "UPPER(" + KEY_CATEGORY_NAME + ")" + " OR "
                //+ "UPPER(" + KEY_TEXT_SHORT + ")" + " OR "
                //+ "UPPER(" + KEY_TEXT_FULL + ")"
                //+ "LIKE " + "UPPER(" + "%cns" + ")";
                //+ "%";
                //+ KEY_TEXT_FULL
                //+ " LIKE " + "'" + Arrays.toString(wordsForSearch) + "'";
                + " LIKE " + "(" + "%cns" + ")";*/

        /*StringBuilder cns = new StringBuilder();
        cns.append("('");
        for(int i = 0; i < wordsForSearch.length; i++) {
            cns.append(String.valueOf(wordsForSearch[i]));
            if (i < wordsForSearch.length - 1) {
                cns.append("','");
            }
        }
        cns.append("')");*/

        StringBuilder cns = new StringBuilder();
        cns.append("'%$");
        for(int i = 0; i < wordsForSearch.length; i++) {
            cns.append(String.valueOf(wordsForSearch[i]));
            if (i < wordsForSearch.length - 1) {
                cns.append("%','%$");
            }
        }
        cns.append("%'");



        //String selectQuery = "SELECT  * FROM " + TABLE_ITEMS + " WHERE " + KEY_IN_CART + "=?";

        //String finalQuery = selectQuery.replaceAll("%cns", cns.toString());
        //SQLiteDatabase db = this.getReadableDatabase();

        //Cursor cursor = db.rawQuery(selectQuery, null);
        //Cursor cursor = db.rawQuery(finalQuery, null);

        //String finDescription = wordForSearch.substring(0, 1).toUpperCase() + wordForSearch.substring(1);




        SQLiteDatabase db = this.getReadableDatabase();
        /*Cursor cursor = db.query(TABLE_PERFORMERS,
                new String[]{KEY_ID,
                        KEY_PERFORMER_NAME,
                        KEY_PERFORMER_GENRES,
                        KEY_COUNT_TRACKS,
                        KEY_COUNT_ALBUMS,
                        KEY_PERFORMER_LINK,
                        KEY_DESCRIPTION,
                        KEY_COVER_SMALL,
                        KEY_COVER_BIG}, KEY_PERFORMER_NAME + "=?",
                new String[] {finDescription}, null, null, null, null);*/
        String finalQuery = null;
        try {
            finalQuery = selectQuery.replaceAll("%cns", cns.toString());
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        assert finalQuery != null;
        Cursor cursor = db.rawQuery(finalQuery, null);
        /*if (cursor != null)
            cursor.moveToFirst();*/
        assert cursor != null;
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
        cursor.close();
        closeDB(db);
        return itemList;
    }

    public void deleteItem(Performer item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PERFORMERS, KEY_ID + " = ?",
                new String[]{String.valueOf(item.getmId())});
        closeDB(db);
    }

    public int updateItem(Performer item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PERFORMER_NAME, item.getmName());
        values.put(KEY_COVER_SMALL, item.getmCoverSmall());

        return db.update(TABLE_PERFORMERS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(item.getmId())});
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
