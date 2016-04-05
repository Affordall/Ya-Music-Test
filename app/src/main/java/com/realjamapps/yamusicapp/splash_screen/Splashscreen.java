package com.realjamapps.yamusicapp.splash_screen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.content.MainActivity;
import com.realjamapps.yamusicapp.content.SettingsActivity;

public class Splashscreen extends AppCompatActivity {

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    /** Called when the activity is first created. */
    Thread splashTread;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences(SettingsActivity.APP_SETTINGS, Context.MODE_PRIVATE);
        int showSplashScreen = sharedPreferences.getInt(SettingsActivity.KEY_SWITCH_NOTIFY_INDEX, 1);

        super.onCreate(savedInstanceState);

        if (showSplashScreen == 1) {
            setContentView(R.layout.activity_splashscreen);
            StartAnimations();
        } else {
            finishThisAndRunMain();
        }
    }

    /**Yes i know that Splash-screen is a anti-pattern.
     * But it's look soo cool :) */

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
        assert l != null;
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splash);
        assert iv != null;
        iv.clearAnimation();
        iv.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        TextView l2 = (TextView) findViewById(R.id.tv_splash_name);
        assert l2 != null;
        l2.setVisibility(View.VISIBLE);
        l2.clearAnimation();
        l2.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3000) {
                        sleep(100);
                        waited += 100;
                    }
                    finishThisAndRunMain();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    Splashscreen.this.finish();
                }

            }
        };
        splashTread.start();

    }

    private void finishThisAndRunMain() {
        Intent intent = new Intent(Splashscreen.this,
                MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        Splashscreen.this.finish();
    }

}
