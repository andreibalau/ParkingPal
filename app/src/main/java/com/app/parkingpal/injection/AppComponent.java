package com.app.parkingpal.injection;

import com.app.parkingpal.service.SSEListener;
import com.app.parkingpal.ui.main.GmapMainViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class,RepositoryModule.class, DaoModule.class})
public interface AppComponent {
    void inject(GmapMainViewModel gmapMainActivity);
    void inject(SSEListener sseListener);
}
