package com.realjamapps.yamusicapp.database.sql.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.realjamapps.yamusicapp.database.INamesTable;
import com.realjamapps.yamusicapp.database.sql.SqlStatementValidation;
import com.realjamapps.yamusicapp.models.Genres;

import javax.inject.Inject;

public class TableGenres {

    public static final String TABLE_GENRES = INamesTable.GENRES;

    public static final String KEY_GENRES_ID = INamesTable.Genres.ID;
    public  static final String KEY_GENRES_NAME = INamesTable.Genres.NAME;

    private static final String CREATE_TABLE_GENRES = "CREATE TABLE "
            + TABLE_GENRES + "("
            + KEY_GENRES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_GENRES_NAME + " TEXT NOT NULL" + ")";

    private final SqlStatementValidation sqlStatementValidation = new SqlStatementValidation();

    @Inject
    public TableGenres() {
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_GENRES);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(TableGenres.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRES);
    }

    public void add(Genres genre, SQLiteOpenHelper helper) {
        String sql = "INSERT OR REPLACE INTO " + TABLE_GENRES +
                " ( " + KEY_GENRES_NAME +
                " ) " + "VALUES ( ? )";
        final SQLiteDatabase database = helper.getWritableDatabase();
        database.beginTransactionNonExclusive();
        sqlStatementValidation.runSqlStatement(database, sql, genre);
    }

    public void add(Iterable<Genres> iterable, SQLiteOpenHelper helper) {
        for(Genres item : iterable){
            add(item, helper);
        }
    }

    public void update(Genres genre, SQLiteOpenHelper helper) {
        final SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GENRES_ID, genre.getId());
        values.put(KEY_GENRES_NAME, genre.getName());

        db.update(TABLE_GENRES, values, KEY_GENRES_ID + " = ?",
                new String[]{String.valueOf(genre.getId())});
    }

    public void remove(Genres genre, SQLiteOpenHelper helper) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_GENRES, KEY_GENRES_ID + " = ?",
                new String[]{String.valueOf(genre.getId())});
        if (db.isOpen())
            db.close();
    }

    public int count(SQLiteOpenHelper helper) {
        int num;
        final SQLiteDatabase db = helper.getWritableDatabase();
        try {
            String QUERY = "SELECT  * FROM " + TABLE_GENRES;
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
