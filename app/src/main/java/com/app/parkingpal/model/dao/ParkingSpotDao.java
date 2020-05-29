package com.app.parkingpal.model.dao;

import android.accounts.Account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.app.parkingpal.model.ParkingSpot;

import java.util.List;

@Dao
public interface ParkingSpotDao {

    @Insert
    void saveAll(List<ParkingSpot> parkingSpots);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(ParkingSpot parkingSpot);

    @Query("SELECT * FROM parking_spots")
    LiveData<List<ParkingSpot>> findAll();
}
