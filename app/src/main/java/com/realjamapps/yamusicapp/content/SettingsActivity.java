package com.realjamapps.yamusicapp.content;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.realjamapps.yamusicapp.R;

public class SettingsActivity extends AppCompatActivity {

    public static final String APP_SETTINGS = "settings";
    private Toolbar toolbar;
    private Switch mSwitchSplash;
    int checkedNotifyIndex;
    public static final String KEY_SWITCH_NOTIFY_INDEX = "SAVED_SWITCH_NOTIFY_INDEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSwitchSplash = (Switch) findViewById(R.id.switch_splash_screen);

        mSwitchSplash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    checkedNotifyIndex = 1;
                } else {
                    checkedNotifyIndex = 0;
                }
                SavePreferences(KEY_SWITCH_NOTIFY_INDEX, checkedNotifyIndex);
            }
        });

        LoadPreferences();
    }

    private void SavePreferences(String key, int value) {
        SharedPreferences sharedPreferences = getSharedPreferences(
                APP_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
        //showNotifyToast();
    }

    private void LoadPreferences() {

        SharedPreferences sharedPreferences = getSharedPreferences(
                APP_SETTINGS, Context.MODE_PRIVATE);
        /** load Switch position */
        int savedSwitchIndex = sharedPreferences.getInt(
                KEY_SWITCH_NOTIFY_INDEX, 1);
        if (savedSwitchIndex == 1) {
            mSwitchSplash.setChecked(true);
        } else {
            mSwitchSplash.setChecked(false);
        }

    }

    private void showNotifyToast() {
        Toast.makeText(this, getString(R.string.settings_changed), Toast.LENGTH_SHORT).show();
    }
}