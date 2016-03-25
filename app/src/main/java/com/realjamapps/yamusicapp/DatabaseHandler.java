package com.realjamapps.yamusicapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.realjamapps.yamusicapp.listeners.PerformersListener;
import com.realjamapps.yamusicapp.models.Performer;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper implements PerformersListener {

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void addPerformer(Performer item) {

    }

    @Override
    public ArrayList<Performer> getAllPerformers() {
        return null;
    }

    @Override
    public int getPerformersCount() {
        return 0;
    }


}
