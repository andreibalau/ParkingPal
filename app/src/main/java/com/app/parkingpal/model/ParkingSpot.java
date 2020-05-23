package com.app.parkingpal.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "parking_spots")
public class ParkingSpot {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long id;
    @ColumnInfo(name = "latitude")
    private Double latitude;
    @ColumnInfo(name = "longitude")
    private Double longitude;
    @ColumnInfo(name = "availability")
    private Boolean availability;
}
