package com.realjamapps.yamusicapp.app;


import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.realjamapps.yamusicapp.BuildConfig;
import com.realjamapps.yamusicapp.di.components.DaggerYaMusicAppComponent;
import com.realjamapps.yamusicapp.di.components.YaMusicAppComponent;
import com.realjamapps.yamusicapp.di.modules.YaMusicAppModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;


public class YaMusicApp extends Application {

    private static volatile Application instance;
    private YaMusicAppComponent appComponent;
    private static RealmConfiguration realmConfiguration;
    private static boolean activityVisible;

    public YaMusicApp() {}

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static YaMusicApp get(Context context) {
        return (YaMusicApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Fresco.initialize(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        //buildRealm();
        buildGraphAndInject();
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    public void buildGraphAndInject() {
        appComponent = DaggerYaMusicAppComponent.builder()
                .yaMusicAppModule(new YaMusicAppModule(this))
                .build();
        appComponent.inject(this);
    }

    public YaMusicAppComponent getAppComponent() {
        return appComponent;
    }

    private void buildRealm() {
        realmConfiguration = new RealmConfiguration.Builder(this)
                .name("yamusic.realm")
                //.modules(new ModelsRealmModule())
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public static void clearRealmDatabase() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    public static RealmConfiguration getRealmConfiguration() {
        return realmConfiguration;
    }

}