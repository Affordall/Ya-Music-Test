package com.realjamapps.yamusicapp.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.app.YaMusicApp;
import com.realjamapps.yamusicapp.receivers.DownloadResultReceiver;
import com.realjamapps.yamusicapp.services.DownloadServiceIntent;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

public class Utils {

    public static final String APP_SETTINGS = "settings";
    private static int deviceAPI = Build.VERSION.SDK_INT;
    private static Context mContext = YaMusicApp.getContext();

    /**
     * Method to check Internet available.
     * @return boolean
     * */
    public static boolean isNetworkUnavailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Method that get app URL and launch Google Play to rate it.
     * @return void
     * */
    public static void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        myAppLinkToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            mContext.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
        }
    }

    public static void initToolBar(Context context, Toolbar toolbar, boolean homeEnabled, boolean homeAsUp, boolean showTitleEnabled, String title) {
        try {
            if (toolbar != null) {
                ((AppCompatActivity) context).setSupportActionBar(toolbar);
                getToolBar(context).setHomeButtonEnabled(homeEnabled);
                getToolBar(context).setDisplayHomeAsUpEnabled(homeAsUp);
                getToolBar(context).setDisplayShowTitleEnabled(showTitleEnabled);
                getToolBar(context).setTitle(title);
                getToolBar(context).setElevation(7);
            }
        } catch (NullPointerException e) {
            logError(e);
        }
    }

    /**
     * Method to get Support Action Bar.
     * @param context
     * @return ActionBar
     * */
    private static ActionBar getToolBar(Context context) {
        return ((AppCompatActivity) context).getSupportActionBar();
    }

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static void createIntentStartService(Context context, DownloadResultReceiver receiver) {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, context, DownloadServiceIntent.class);
        intent.putExtra("receiver", receiver);
        context.startService(intent);
    }

    @Contract(pure = true)
    public static boolean isKitkat() {
        return deviceAPI >= Build.VERSION_CODES.KITKAT;
    }

    @Contract(pure = true)
    public static boolean isLollipop() {
        return deviceAPI >= Build.VERSION_CODES.LOLLIPOP;
    }

    @Contract(pure = true)
    public static boolean isMarshmallow() {
        return deviceAPI >= Build.VERSION_CODES.M;
    }

    @Contract(pure = true)
    public static boolean isNougat() {
        return deviceAPI >= Build.VERSION_CODES.N;
    }

    public static void logError(Exception e) {
        String error = e.getMessage() + " " + e.getCause();
        Log.e("-YA-MUSIC-ERROR:"," " + error);
        //Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
    }

    @Contract(" -> !null")
    public static synchronized <Genres>ArrayList<Genres> newInstanceGenres() {
        return new ArrayList<>();
    }

    @Contract(" -> !null")
    public static synchronized <Performer>ArrayList<Performer> newInstancePerformer() {
        return new ArrayList<>();
    }

    private void measureExecutionTime() {
        long startTime = System.currentTimeMillis();
        /* Insert Method here */
        long endTime = System.currentTimeMillis();
        System.out.println("That took " + (endTime - startTime) + " milliseconds");
    }


}
