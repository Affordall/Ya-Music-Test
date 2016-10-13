package com.realjamapps.yamusicapp.database.sql;

import android.annotation.TargetApi;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.text.TextUtils;

import com.realjamapps.yamusicapp.models.Genres;
import com.realjamapps.yamusicapp.models.Performer;

public class SqlStatementValidation implements ISqlStatementValidation {

    private final int deviceAPI = Build.VERSION.SDK_INT;

    public SqlStatementValidation() {}

    @Override
    public void runSqlStatement(SQLiteDatabase db, String sql, Object incomeObj) {
        runSqlStatementDependingByApiVersion(db, sql, incomeObj);
    }

    private void runSqlStatementDependingByApiVersion(SQLiteDatabase db, String sql, Object incomeObj) {
        if (isKitkat()) {
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

    private void safeStmt(SQLiteStatement stmt, int index, String incString) {
        if (incString != null) {
            stmt.bindString(index, incString);
        } else {
            stmt.bindNull(index);
        }
    }

    private void safeStmt(SQLiteStatement stmt, int index, long incLong) {
        String referrer = Long.toString(incLong);
        if (referrer != null) {
            stmt.bindLong(index, incLong);
        } else {
            stmt.bindNull(index);
        }
    }

    private void safeStmt(SQLiteStatement stmt, int index, double incDouble) {
        String referrer = Double.toString(incDouble);
        if (referrer != null) {
            stmt.bindDouble(index, incDouble);
        } else {
            stmt.bindNull(index);
        }
    }

    private void closeDB(SQLiteDatabase db) {
        if (db != null && db.isOpen())
            db.close();
    }

    private void commonSqlStatementPerformer(SQLiteDatabase db, SQLiteStatement stmt, Performer performer) {
        try {
            safeStmt(stmt, 1, performer.getmId());
            safeStmt(stmt, 2, performer.getmName());
            safeStmt(stmt, 3, TextUtils.join(", ", performer.getmGenres()));
            safeStmt(stmt, 4, performer.getmTracks());
            safeStmt(stmt, 5, performer.getmAlbums());
            safeStmt(stmt, 6, performer.getmLink());
            safeStmt(stmt, 7, performer.getmDescription());
            safeStmt(stmt, 8, performer.getmCoverSmall());
            safeStmt(stmt, 9, performer.getmCoverBig());
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

    private boolean isKitkat() {
        return deviceAPI >= Build.VERSION_CODES.KITKAT;
    }

}
