package com.realjamapps.yamusicapp.database.sql;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;

public class CursorUtils {

    public static String getStringCursor(Cursor cursor, String columnIndex) {
        return cursor.getString(cursor.getColumnIndex(columnIndex));
    }

    public static int getIntCursor(Cursor cursor, String columnIndex) throws CursorIndexOutOfBoundsException {
        return cursor.getInt(cursor.getColumnIndex(columnIndex));
    }

    public static long getLongCursor(Cursor cursor, String columnIndex) throws CursorIndexOutOfBoundsException {
        return cursor.getLong(cursor.getColumnIndex(columnIndex));
    }

    public static double getDoubleCursor(Cursor cursor, String columnIndex) throws CursorIndexOutOfBoundsException {
        return cursor.getDouble(cursor.getColumnIndex(columnIndex));
    }

}
