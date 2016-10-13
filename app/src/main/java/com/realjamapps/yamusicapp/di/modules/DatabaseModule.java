package com.realjamapps.yamusicapp.di.modules;

import android.app.Application;

import com.realjamapps.yamusicapp.app.YaMusicApp;
import com.realjamapps.yamusicapp.database.sql.DatabaseHandler;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    DatabaseHandler provideDatabaseHandler(Application application) {
        return DatabaseHandler.getInstance(application);
    }

    @Provides
    @Singleton
    Realm provideRealm() {
        return Realm.getInstance(YaMusicApp.getRealmConfiguration());
    }

}
