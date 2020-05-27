package com.app.parkingpal;

import android.app.Application;

import com.app.parkingpal.injection.AppComponent;
import com.app.parkingpal.injection.AppModule;
import com.app.parkingpal.injection.DaggerAppComponent;
import com.app.parkingpal.injection.DaoModule;
import com.app.parkingpal.injection.RepositoryModule;

public class ParkingPalApplication extends Application {

    private static AppComponent component;

    public static AppComponent getAppComponent() {
        return component;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent
                .builder()
                .appModule(new AppModule(getApplicationContext()))
                .daoModule(new DaoModule())
                .repositoryModule(new RepositoryModule())
                .build();
    }
}
