package com.app.parkingpal.injection;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lombok.RequiredArgsConstructor;

@Module
@RequiredArgsConstructor
public class AppModule {
    private final Context context;

    @Provides
    @Singleton
    Context provideContext(){
        return context;
    }
}
