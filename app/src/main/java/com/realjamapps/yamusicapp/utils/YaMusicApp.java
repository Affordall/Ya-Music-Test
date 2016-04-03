package com.realjamapps.yamusicapp.utils;


import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;


public class YaMusicApp extends Application {

    private static Application instance;
    private final Context mContext;
    private static boolean activityVisible;

    public YaMusicApp(Context mContext) {
        this.mContext = mContext;
    }

    public YaMusicApp() {
        mContext = null;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Fresco.initialize(this);
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

}