package com.realjamapps.yamusicapp.di.modules;

import com.realjamapps.yamusicapp.database.sql.tables.TableGenres;
import com.realjamapps.yamusicapp.database.sql.tables.TablePerformers;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TablesModule {

    @Provides
    @Singleton
    TablePerformers provideTablePerformers() {
        return new TablePerformers();
    }

    @Provides
    @Singleton
    TableGenres provideTableGenres() {
        return new TableGenres();
    }

}
