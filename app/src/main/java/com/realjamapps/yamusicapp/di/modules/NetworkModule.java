package com.realjamapps.yamusicapp.di.modules;

import com.realjamapps.yamusicapp.parsers.GetAllDataParser;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;


@Module
public class NetworkModule {

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        final OkHttpClient okHttpClient;

        okHttpClient = new OkHttpClient.Builder()
                .build();

        /**
         * Uncomment only for test. Slowdown network for 10 seconds (configurable).
         * */
        //okHttpClient.networkInterceptors().add(new NetworkSlowdown());

        return okHttpClient;
    }

    @Provides
    @Singleton
    GetAllDataParser provideParser() {
        return GetAllDataParser.getInstance();
    }

}
