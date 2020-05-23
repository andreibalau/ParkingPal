package com.app.parkingpal.injection;

import com.app.parkingpal.model.dao.ParkingSpotDao;
import com.app.parkingpal.repository.ParkingSpotRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    ParkingSpotRepository provideParkingSpotRepository(ParkingSpotDao parkingSpotDao){
        return new ParkingSpotRepository(parkingSpotDao);
    }
}
