package com.app.parkingpal.model.dao;

import android.accounts.Account;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.app.parkingpal.model.ParkingSpot;

import java.util.List;

@Dao
public interface ParkingSpotDao {

    @Query("SELECT * FROM parking_spots")
    LiveData<List<ParkingSpot>> findAll();
}
