package com.realjamapps.yamusicapp.database.sql;

import android.database.sqlite.SQLiteDatabase;

public interface ISqlStatementValidation {
    void runSqlStatement(SQLiteDatabase db, String sql, Object incomeObj);
}
