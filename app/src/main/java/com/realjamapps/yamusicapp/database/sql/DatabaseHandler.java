package com.realjamapps.yamusicapp.database.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;

import com.realjamapps.yamusicapp.app.YaMusicApp;
import com.realjamapps.yamusicapp.database.sql.tables.TableGenres;
import com.realjamapps.yamusicapp.database.sql.tables.TablePerformers;
import com.realjamapps.yamusicapp.models.Genres;
import com.realjamapps.yamusicapp.models.Performer;

import java.io.File;

import javax.inject.Inject;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static volatile DatabaseHandler sInstance;

    private static final String DATABASE_NAME = "yamusicbase";
    private static final int DATABASE_VERSION = 1;

    @Inject TablePerformers tablePerformers;
    @Inject TableGenres tableGenres;

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        YaMusicApp.get(YaMusicApp.getContext()).getAppComponent().inject(this);
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        TablePerformers.onCreate(db);
        TableGenres.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TablePerformers.onUpgrade(db, oldVersion, newVersion);
        TableGenres.onUpgrade(db, oldVersion, newVersion);
        onCreate(db);
    }

    private boolean isDBlocked() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.isDbLockedByCurrentThread();
    }

    private boolean isDatabaseExist() {
        File database = YaMusicApp.getContext().getDatabasePath(DATABASE_NAME);
        return database.exists();
    }

    public void deleteAllDBs() {
        if (isDatabaseExist() && !isDBlocked()) {
            SQLiteDatabase db = this.getWritableDatabase();
            try {
                db.delete(TablePerformers.TABLE_PERFORMERS, null, null);
                db.delete(TableGenres.TABLE_GENRES, null, null);
            } catch (SQLiteDatabaseLockedException e) {
                e.printStackTrace();
            } finally {
                closeDB(db);
            }
        }
    }

    private Boolean doIt(String TableName, String ColumnName, String ColumnData) {
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

    private Boolean doesRecordExist(String TableName, String ColumnName, String ColumnData) throws SQLException {
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
     * CRUD Operations
     * */

    /** Performer */

    public boolean isPerformerExist(String name) {
        return doIt(TablePerformers.TABLE_PERFORMERS, TablePerformers.KEY_PERFORMER_NAME, name);
    }

    public void addPerformer(Performer performer) {
        if (!isPerformerExist(performer.getmName()))
            tablePerformers.add(performer, this);
    }

    public void updatePerformer(Performer performer) {
        tablePerformers.update(performer, this);
    }

    public void removePerformer(Performer performer) {
        tablePerformers.remove(performer, this);
    }

    public int getPerformersCount() {
        return tablePerformers.count(this);
    }

    /** Genre */

    public boolean isGenreExist(String name) {
        return doIt(TableGenres.TABLE_GENRES, TableGenres.KEY_GENRES_NAME, name);
    }

    public void addGenre(Genres genre) {
        if (!isGenreExist(genre.getName()))
            tableGenres.add(genre, this);
    }

    public void updateGenre(Genres genre) {
        tableGenres.update(genre, this);
    }

    public void removeGenre(Genres genre) {
        tableGenres.remove(genre, this);
    }

    public int getGenresCount() {
        return tableGenres.count(this);
    }

//    public Performer getSinglePerformer(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_PERFORMERS,
//                new String[]{KEY_ID,
//                        KEY_PERFORMER_NAME,
//                        KEY_PERFORMER_GENRES,
//                        KEY_COUNT_TRACKS,
//                        KEY_COUNT_ALBUMS,
//                        KEY_PERFORMER_LINK,
//                        KEY_DESCRIPTION,
//                        KEY_COVER_SMALL,
//                        KEY_COVER_BIG}, KEY_ID + "=?",
//                new String[]{String.valueOf(id)}, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//            Performer single_item = createNewPerformerByCursor(cursor);
//        if (cursor != null)
//            cursor.close();
//        closeDB(db);
//        return single_item;
//    }
//

//
//
//    public ArrayList<Performer> getAllPerformersByGenre(String[] str) {
//        ArrayList<Performer> itemList = Utils.newInstancePerformer();
//
//        String selectQuery = "SELECT  * FROM " + TABLE_PERFORMERS + " WHERE "
//                + KEY_PERFORMER_GENRES + " LIKE " + "%cns";
//
//        StringBuilder cns = new StringBuilder();
//        cns.append("'%");
//        for(int i = 0; i < str.length; i++) {
//            cns.append(String.valueOf(str[i]));
//            if (i < str.length - 1) {
//                cns.append("%' OR " + KEY_PERFORMER_GENRES + " LIKE '%");
//            }
//        }
//        cns.append("%'");
//        String finalQuery = selectQuery.replaceAll("%cns", cns.toString());
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(finalQuery, null);
//        try {
//        if (cursor.moveToFirst()) {
//            do {
//                Performer item = createNewPerformerByCursor(cursor);
//                itemList.add(item);
//            } while (cursor.moveToNext());
//        }
//        } catch (IllegalStateException e) {
//            Utils.logError(e);
//        } finally {
//            closeDB(db);
//        }
//        return itemList;
//    }
//
//
//    public ArrayList<Performer> getSearchResult(String[] wordsForSearch) {
//
//        ArrayList<Performer> itemList = Utils.newInstancePerformer();
//
//        String selectQuery = "SELECT  * FROM " + TABLE_PERFORMERS + " WHERE "
//                + KEY_PERFORMER_NAME + " LIKE " + "(cast(" + "%cns" +" as text))";
//
//        StringBuilder cns = new StringBuilder();
//        cns.append("'%$");
//        for(int i = 0; i < wordsForSearch.length; i++) {
//            cns.append(String.valueOf(wordsForSearch[i]));
//            if (i < wordsForSearch.length - 1) {
//                cns.append("%','%$");
//            }
//        }
//        cns.append("%'");
//
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        String finalQuery = null;
//        try {
//            finalQuery = selectQuery.replaceAll("%cns", cns.toString());
//        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
//            e.printStackTrace();
//        }
//
//        assert finalQuery != null;
//        Cursor cursor = db.rawQuery(finalQuery, null);
//        assert cursor != null;
//        try {
//        if (cursor.moveToFirst()) {
//            do {
//                Performer item = createNewPerformerByCursor(cursor);
//                itemList.add(item);
//            } while (cursor.moveToNext());
//        }
//        } catch (IllegalStateException e) {
//            Utils.logError(e);
//        } finally {
//            closeDB(db);
//        }
//        return itemList;
//    }


    private void closeDB(SQLiteDatabase db) {
        if (db != null && db.isOpen())
            db.close();
    }



}
