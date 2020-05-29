package com.app.parkingpal.repository;

import androidx.lifecycle.LiveData;

import com.app.parkingpal.model.ParkingSpot;
import com.app.parkingpal.model.dao.ParkingSpotDao;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParkingSpotRepository {

    private final ParkingSpotDao parkingSpotDao;

    public void saveAll(List<ParkingSpot> parkingSpots){
        new Thread(() ->parkingSpotDao.saveAll(parkingSpots)).start();
    }

    public void save(ParkingSpot parkingSpot){
        new Thread(() ->parkingSpotDao.save(parkingSpot)).start();
    }

    public LiveData<List<ParkingSpot>> findAll() {
        return parkingSpotDao.findAll();
    }
}
