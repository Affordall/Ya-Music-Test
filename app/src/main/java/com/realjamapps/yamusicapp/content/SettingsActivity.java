package com.realjamapps.yamusicapp.content;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    public static final String APP_SETTINGS = "settings";
    int checkedNotifyIndex;
    public static final String KEY_SWITCH_NOTIFY_INDEX = "SAVED_SWITCH_NOTIFY_INDEX";

    @BindView(R.id.switch_splash_screen) Switch mSwitchSplash;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setUpSupportActionBar();

        mSwitchSplash.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkedNotifyIndex = 1;
            } else {
                checkedNotifyIndex = 0;
            }
            SavePreferences(KEY_SWITCH_NOTIFY_INDEX, checkedNotifyIndex);
        });

        LoadPreferences();
    }

    private void setUpSupportActionBar() {
        Utils.initToolBar(SettingsActivity.this, mToolbar, true, true, true, getString(R.string.settings_activity_title));
    }

    private void SavePreferences(String key, int value) {
        SharedPreferences sharedPreferences = getSharedPreferences(
                APP_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
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
}