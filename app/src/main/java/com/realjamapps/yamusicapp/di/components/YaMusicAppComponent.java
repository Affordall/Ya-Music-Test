package com.realjamapps.yamusicapp.di.components;

import com.realjamapps.yamusicapp.app.YaMusicApp;
import com.realjamapps.yamusicapp.database.sql.DatabaseHandler;
import com.realjamapps.yamusicapp.di.modules.YaMusicAppModule;
import com.realjamapps.yamusicapp.intro.FancyAppIntro;
import com.realjamapps.yamusicapp.parsers.GetAllDataParser;
import com.realjamapps.yamusicapp.repository.impl.sql.PerformersSqlRepository;
import com.realjamapps.yamusicapp.repository.impl.sql.GenresSqlRepository;
import com.realjamapps.yamusicapp.services.DownloadServiceIntent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = YaMusicAppModule.class)
public interface YaMusicAppComponent {

    void inject(YaMusicApp app);

    void inject(FancyAppIntro fancyAppIntro);

    void inject(DatabaseHandler handler);
    void inject(GetAllDataParser parser);
    void inject(DownloadServiceIntent downloadServiceIntent);

    void inject(PerformersSqlRepository cityRepository);
    void inject(GenresSqlRepository stationRepository);

}
