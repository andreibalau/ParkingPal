package com.app.parkingpal.injection;

import android.content.Context;

import com.app.parkingpal.database.AppDatabase;
import com.app.parkingpal.model.dao.ParkingSpotDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lombok.AllArgsConstructor;

@Module
@AllArgsConstructor
public class DaoModule {

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(Context context){
        return AppDatabase.getInstance(context);
    }

    @Provides
    @Singleton
    ParkingSpotDao provideParkingSpotDao(AppDatabase appDatabase){
        return appDatabase.getParkingSpotDao();
    }
}
