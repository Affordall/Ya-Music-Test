package com.realjamapps.yamusicapp.intro;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.utils.YaMusicApp;


public class FancyAppIntro extends AppIntro2 {

    public static final String APP_SETTINGS = "settings";
    final String KEY_VIEW_INTRO_OR_NOT_INDEX = "SAVED_VIEW_INTRO_OR_NOT_INDEX";

    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        Context mContext = YaMusicApp.getContext();

        try {

            /** Welcome */
            addSlide(AppIntroFragment.newInstance(
                    getString(R.string.slide_one_title),
                    getString(R.string.app_name),
                    R.drawable.source,
                    ContextCompat.getColor(this, R.color.colorPrimaryDark)));

            /** Pull to Refresh */
            addSlide(AppIntroFragment.newInstance(
                    getString(R.string.slide_two_title),
                    getString(R.string.slide_two_text),
                    R.drawable.intro_png_pull,
                    ContextCompat.getColor(this, R.color.colorPrimary)));

            /** Use filters */
            addSlide(AppIntroFragment.newInstance(
                    getString(R.string.slide_four_title),
                    getString(R.string.slide_four_text),
                    R.drawable.intro_png_filters,
                    ContextCompat.getColor(this, R.color.colorPrimary)));

            /** "Permissions" for Android 6.0 */
            addSlide(AppIntroFragment.newInstance(
                    getString(R.string.slide_fifth_title),
                    getString(R.string.slide_fifth_text),
                    R.drawable.intro_png_details,
                    ContextCompat.getColor(this, R.color.colorPrimaryDark)));

            askForPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);


        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            SavePreferences(KEY_VIEW_INTRO_OR_NOT_INDEX,1);
            Toast.makeText(mContext, getString(R.string.oops_error), Toast.LENGTH_SHORT).show();
            recreate();
        }

        setFadeAnimation();

        setProgressButtonEnabled(true);
    }

    @Override
    public void onDonePressed() {
        SavePreferences(KEY_VIEW_INTRO_OR_NOT_INDEX,1);
        finish();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onSlideChanged() {

    }

    private void SavePreferences(String key, int value) {
        SharedPreferences sharedPreferences = getSharedPreferences(
                APP_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
}
