package com.realjamapps.yamusicapp.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.receivers.DownloadResultReceiver;
import com.realjamapps.yamusicapp.services.DownloadServiceIntent;

import java.util.ArrayList;

public class Utils {

    public static final String APP_SETTINGS = "settings";
    private static int deviceAPI = Build.VERSION.SDK_INT;
    private static Context mContext = YaMusicApp.getContext();

    public static boolean isNetworkUnavailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

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
                ((AppCompatActivity) context).getSupportActionBar().setHomeButtonEnabled(homeEnabled);
                ((AppCompatActivity) context).getSupportActionBar().setDisplayHomeAsUpEnabled(homeAsUp);
                ((AppCompatActivity) context).getSupportActionBar().setDisplayShowTitleEnabled(showTitleEnabled);
                ((AppCompatActivity) context).getSupportActionBar().setTitle(title);
                ((AppCompatActivity) context).getSupportActionBar().setElevation(7);
            }

        } catch (NullPointerException e) {
            logError(e);
            //e.printStackTrace();
        }
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

    public static boolean isLollipop() {
        return deviceAPI >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isMarshmallow() {
        return deviceAPI >= Build.VERSION_CODES.M;
    }

    public static boolean isKitkat() {
        return deviceAPI >= Build.VERSION_CODES.KITKAT;
    }

    public static void logError(Exception e) {
        Log.e("-YA-MUSIC-ERROR:"," " + e.getMessage());
    }

    public static synchronized <Genres>ArrayList<Genres> newInstanceGenres() {
        return new ArrayList<>();
    }

    public static synchronized <Performer>ArrayList<Performer> newInstancePerformer() {
        return new ArrayList<>();
    }


}
