package com.app.parkingpal.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.app.parkingpal.ParkingPalApplication;
import com.app.parkingpal.model.ParkingSpot;
import com.app.parkingpal.repository.ParkingSpotRepository;

import java.util.List;

import javax.inject.Inject;

public class GmapMainViewModel extends ViewModel {

    @Inject
    ParkingSpotRepository parkingSpotRepository;

    public GmapMainViewModel(){
        ParkingPalApplication.getAppComponent().inject(this);
    }

    public LiveData<List<ParkingSpot>> findAll(){
        return parkingSpotRepository.findAll();
    }

}
