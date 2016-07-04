package com.realjamapps.yamusicapp.database;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.realjamapps.yamusicapp.listeners.GenresListener;
import com.realjamapps.yamusicapp.listeners.PerformersListener;
import com.realjamapps.yamusicapp.models.Genres;
import com.realjamapps.yamusicapp.models.Performer;
import com.realjamapps.yamusicapp.models.PerformerBuilder;
import com.realjamapps.yamusicapp.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class DatabaseHandler extends SQLiteOpenHelper implements PerformersListener, GenresListener {

    private static DatabaseHandler sInstance;

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

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Use the application context, which will ensure that you
     * don't accidentally leak an Activity's context.
     * See this article for more information: http://bit.ly/6LRzfx
     */
    public static synchronized DatabaseHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHandler(context);
        }
        return sInstance;
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

    public Boolean checkIfRecordExist(String TableName, String ColumnName, String ColumnData) {
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

        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransactionNonExclusive();
        runSqlStatementDependingByApiVersion(db, sql, perf);
    }

    @Override
    public void addGenres(Genres genres) {
        String sql = "INSERT OR REPLACE INTO " + TABLE_GENRES +
                " ( " + KEY_GENRES_NAME +
                " ) " + "VALUES ( ? )";

        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransactionNonExclusive();
        runSqlStatementDependingByApiVersion(db, sql, genres);
    }

    private void runSqlStatementDependingByApiVersion(SQLiteDatabase db, String sql, Object incomeObj) {
        if (Utils.isKitkat()) {
            newApiSqlStatement(db, sql, incomeObj);
        } else {
            oldApiSqlStatement(db, sql, incomeObj);
        }
    }

    private void oldApiSqlStatement(SQLiteDatabase db, String sql, Object incomeObj) {
        SQLiteStatement stmt = db.compileStatement(sql);
        try {
            validateIncomingInstance(db, stmt, incomeObj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            stmt.close();
            closeDB(db);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void newApiSqlStatement(SQLiteDatabase db, String sql, Object incomeObj) {
        try (SQLiteStatement stmt = db.compileStatement(sql)) {
            validateIncomingInstance(db, stmt, incomeObj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            closeDB(db);
        }
    }

    private void validateIncomingInstance(SQLiteDatabase db, SQLiteStatement stmt, Object incomeObj) throws Exception {
        if (incomeObj instanceof Performer) {
            commonSqlStatementPerformer(db, stmt, (Performer)incomeObj);
        } else if (incomeObj instanceof Genres) {
            commonSqlStatementGenres(db, stmt, (Genres)incomeObj);
        } else throw new Exception("Unknown incoming instance of class");
    }

    private void commonSqlStatementPerformer(SQLiteDatabase db, SQLiteStatement stmt, Performer perf) {
        try {
            stmt.bindLong(1, perf.getmId());
            stmt.bindString(2, perf.getmName());
            stmt.bindString(3, TextUtils.join(", ", perf.getmGenres()));
            stmt.bindLong(4, perf.getmTracks());
            stmt.bindLong(5, perf.getmAlbums());
            if (perf.getmLink() != null) {
                stmt.bindString(6, perf.getmLink());
            } else {
                stmt.bindNull(6);
            }
            stmt.bindString(7, perf.getmDescription());
            stmt.bindString(8, perf.getmCoverSmall());
            stmt.bindString(9, perf.getmCoverBig());

            stmt.execute();
            stmt.clearBindings();

            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    private void commonSqlStatementGenres(SQLiteDatabase db, SQLiteStatement stmt, Genres genres) {
        try {
            stmt.bindString(1, genres.getName());

            stmt.execute();
            stmt.clearBindings();

            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
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
            Performer single_item = createNewPerformerByCursor(cursor);
        if (cursor != null)
            cursor.close();
        closeDB(db);
        return single_item;
    }

    @Override
    public ArrayList<Performer> getAllPerformers() {
        ArrayList<Performer> itemList = Utils.newInstancePerformer();

        String selectQuery =  "SELECT  * FROM " + TABLE_PERFORMERS + " ORDER BY "+ KEY_PERFORMER_NAME +" ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Performer item = createNewPerformerByCursor(cursor);
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        closeDB(db);
        cursor.close();
        return itemList;
    }

    @Override
    public ArrayList<Genres> getAllGenres() {
        ArrayList<Genres> genresArrayList = Utils.newInstanceGenres();

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

    public ArrayList<Performer> getAllPerformersByGenre(String[] str) {
        ArrayList<Performer> itemList = Utils.newInstancePerformer();

        String selectQuery = "SELECT  * FROM " + TABLE_PERFORMERS + " WHERE "
                + KEY_PERFORMER_GENRES + " LIKE " + "%cns";

        StringBuilder cns = new StringBuilder();
        cns.append("'%");
        for(int i = 0; i < str.length; i++) {
            cns.append(String.valueOf(str[i]));
            if (i < str.length - 1) {
                cns.append("%' OR " + KEY_PERFORMER_GENRES + " LIKE '%");
            }
        }
        cns.append("%'");
        String finalQuery = selectQuery.replaceAll("%cns", cns.toString());

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(finalQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Performer item = createNewPerformerByCursor(cursor);
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDB(db);
        return itemList;
    }


    public ArrayList<Performer> getSearchResult(String[] wordsForSearch) {

        ArrayList<Performer> itemList = Utils.newInstancePerformer();

        String selectQuery = "SELECT  * FROM " + TABLE_PERFORMERS + " WHERE "
                + KEY_PERFORMER_NAME + " LIKE " + "(cast(" + "%cns" +" as text))";

        StringBuilder cns = new StringBuilder();
        cns.append("'%$");
        for(int i = 0; i < wordsForSearch.length; i++) {
            cns.append(String.valueOf(wordsForSearch[i]));
            if (i < wordsForSearch.length - 1) {
                cns.append("%','%$");
            }
        }
        cns.append("%'");

        SQLiteDatabase db = this.getReadableDatabase();

        String finalQuery = null;
        try {
            finalQuery = selectQuery.replaceAll("%cns", cns.toString());
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        assert finalQuery != null;
        Cursor cursor = db.rawQuery(finalQuery, null);
        assert cursor != null;
        if (cursor.moveToFirst()) {
            do {
                Performer item = createNewPerformerByCursor(cursor);
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

    private Performer createNewPerformerByCursor(Cursor cursor) {
        return new PerformerBuilder()
                .withId(getPerfIdFromCursor(cursor))
                .withName(getPerfNameFromCursor(cursor))
                .withGenres(getGenresListFromCursor(cursor))
                .withTracks(getPerfCountTracksFromCursor(cursor))
                .withAlbums(getPerfCountAlbumsFromCursor(cursor))
                .withLink(getPerfLinkFromCursor(cursor))
                .withDescription(getPerfDescriptionFromCursor(cursor))
                .withCoverSmall(getPerfCoverSmallFromCursor(cursor))
                .withCoverBig(getPerfCoverBigFromCursor(cursor))
                .build();
    }

    private int getPerfIdFromCursor(Cursor cursor) {
        return getIntCursor(cursor, KEY_ID);
    }

    private String getPerfNameFromCursor(Cursor cursor) {
        return getStringCursor(cursor, KEY_PERFORMER_NAME);
    }

    private List<String> getGenresListFromCursor(Cursor cursor) {
        String genresString = getStringCursor(cursor, KEY_PERFORMER_GENRES);
        return Arrays.asList(genresString.split("\\s*,\\s*"));
    }

    private int getPerfCountTracksFromCursor(Cursor cursor) {
        return getIntCursor(cursor, KEY_COUNT_TRACKS);
    }

    private int getPerfCountAlbumsFromCursor(Cursor cursor) {
        return getIntCursor(cursor, KEY_COUNT_ALBUMS);
    }

    private String getPerfLinkFromCursor(Cursor cursor) {
        return getStringCursor(cursor, KEY_PERFORMER_LINK);
    }

    private String getPerfDescriptionFromCursor(Cursor cursor) {
        return getStringCursor(cursor, KEY_DESCRIPTION);
    }

    private String getPerfCoverSmallFromCursor(Cursor cursor) {
        return getStringCursor(cursor, KEY_COVER_SMALL);
    }

    private String getPerfCoverBigFromCursor(Cursor cursor) {
        return getStringCursor(cursor, KEY_COVER_BIG);
    }

}
